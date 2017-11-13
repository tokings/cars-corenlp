package com.embracesource.corenlp.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.embracesource.corenlp.exception.CoreNLPException;
import com.embracesource.corenlp.service.AbstractCoreNLPService;
import com.embracesource.corenlp.util.CoreNLPNERUtil;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * 
 * @author tokings.tang@embracesource.com
 * @date 2017年11月10日 下午5:07:17
 * @copyright http://www.embracesource.com
 */
@Service("coreNLPService")
@Scope("singleton")
public class CoreNLPServiceImpl extends AbstractCoreNLPService {

	private static final Logger log = LoggerFactory.getLogger(CoreNLPServiceImpl.class);

	private static AbstractSequenceClassifier<CoreLabel> nerClassifier;

	@PostConstruct
	public void init() {
		String serializedClassifier = "classifiers/chinese.misc.distsim.crf.ser.gz"; // chinese.misc.distsim.crf.ser.gz
		try {
			nerClassifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			log.error("初始化命名实体识别Classifier出错:" + e.getMessage(), e);
		}
	}

	@PreDestroy
	public void destory() {
		// 显示设置为null，加快GC处理
		nerClassifier = null;
	}

	/**
	 * 对数据做命名实体识别
	 * 
	 * @param source
	 * @return
	 * @throws CoreNLPException
	 */
	public Map<String, Object> doNER(Object source) throws CoreNLPException {

		if (StringUtils.isEmpty(source)) {
			return generateMapResult(source, true, NER_ERROR, "输入为空！", "");
		}
		// 采用默认的slashTags输出风格返回ner结果
		String ner = nerClassifier.classifyToString(source.toString());

		log.info("ner ret:" + ner);
		if (StringUtils.isEmpty(ner)) {
			return generateMapResult(source, true, NER_EMPTY, "NER结果为空！", ner);
		}

		Map<String, Object> ret = generateMapResult(source, true, SUCCESSS, "命名实体识别成功！",
				CoreNLPNERUtil.parseNerBySlashTags(ner.toString()));

		return ret;
	}

	/**
	 * 对数据做命名实体识别
	 * 
	 * @param sources
	 * @return
	 * @throws CoreNLPException
	 */
	public List<Map<String, Object>> doNER(List<Object> sources) throws CoreNLPException {

		if (sources.isEmpty()) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

		for (Object source : sources) {
			ret.add(doNER(source));
		}

		return ret;
	}

	@Override
	public Map<String, Object> doNER(Object source, String respDataType) throws CoreNLPException {

		if (StringUtils.isEmpty(source)) {
			return generateMapResult(source, true, NER_ERROR, "输入为空！", "");
		}
		// 采用默认的respDataType输出风格返回ner结果
		String ner = nerClassifier.classifyToString(source.toString(), respDataType, true);

		log.info("ner ret:" + ner);
		if (StringUtils.isEmpty(ner)) {
			return generateMapResult(source, true, NER_EMPTY, "NER结果为空！", ner);
		}

		Map<String, Object> ret = generateMapResult(source, true, SUCCESSS, "命名实体识别成功！", ner);

		return ret;
	}

	@Override
	public List<Map<String, Object>> doNER(List<Object> sources, String respDataType) throws CoreNLPException {

		if (sources.isEmpty()) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

		for (Object source : sources) {
			ret.add(doNER(source, respDataType));
		}

		return ret;
	}

	@Override
	public Map<String, Object> doNERByFile(String filePath) throws CoreNLPException {

		File file = new File(filePath);

		if (!file.exists()) {
			return generateMapResult(filePath, true, NER_FILE_NOT_EXIST, "文件不存在", "");
		}

		if (file.isDirectory()) {
			return generateMapResult(filePath, true, NER_FILE_IS_DIR, "文件是一个目录，而不是文件", "");
		}

		BufferedReader bf = null;

		try {
			bf = IOUtils.readerFromFile(file, CoreNLPNERUtil.ENCODE);

			StringBuffer sb = new StringBuffer();
			String tmp = null;
			while ((tmp = bf.readLine()) != null) {
				sb.append(tmp);
			}

			log.debug("doNERByFile, file content:{}", sb.toString());

			if (StringUtils.isEmpty(sb)) {
				return generateMapResult(filePath, true, NER_FILE_IS_EMPTY, "文件内容为空", "");
			}
			// 采用默认的slashTags输出风格返回ner结果
			String ner = nerClassifier.classifyToString(sb.toString());

			log.debug("doNERByFile,ner data:{}", ner);
			if (StringUtils.isEmpty(ner)) {
				return generateMapResult(filePath, true, NER_EMPTY, "NER结果为空！", ner);
			}

			// 将ner结果解析为json格式
			Map<String, Object> nerMap = CoreNLPNERUtil.parseNerBySlashTags(ner);
			ner = CoreNLPNERUtil.gson.toJson(nerMap, Map.class);
			// 将识别结果写入文件结果文件当中（默认以源文件名+.json后缀命名）
			String outputPath = filePath + CoreNLPNERUtil.NER_DEFAULT_OUTPUT_FILE_EXTENTION;
			boolean isSuc = writeNERToFS(ner, outputPath);

			if (isSuc) {
				return generateMapResult(filePath, true, SUCCESSS, "命名实体识别成功！", outputPath);
			} else {
				return generateMapResult(filePath, true, NER_FILE_WRITE_ERR, "命名实体识别成功！但是写入目标文件出错",
						CoreNLPNERUtil.parseNerBySlashTags(ner.toString()));
			}
		} catch (Exception e) {
			throw new CoreNLPException("doNERByFile error.", e);
		} finally {
			IOUtils.closeIgnoringExceptions(bf);
		}
	}

	@Override
	public List<Map<String, Object>> doNERByFilelist(List<String> filelist) throws CoreNLPException {

		List<Map<String, Object>> ret = new ArrayList<>();

		if (filelist == null || filelist.isEmpty()) {
			ret.add(generateMapResult(filelist, true, NER_FILELIST_NOT_EXIST, "文件列表不存在或者为空", ""));
			return ret;
		}

		for (String file : filelist) {
			try {
				ret.add(doNERByFile(file));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				ret.add(generateMapResult(file, true, NER_ERROR, e.getMessage(), ""));
			}
		}

		return ret;
	}

}
