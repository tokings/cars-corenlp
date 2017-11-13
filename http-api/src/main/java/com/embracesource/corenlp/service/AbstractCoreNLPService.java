package com.embracesource.corenlp.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.embracesource.corenlp.util.CoreNLPNERUtil;

import edu.stanford.nlp.io.IOUtils;

/**
 * 
 * @author tokings.tang@embracesource.com
 * @date 2017年11月13日 上午10:33:11
 * @copyright http://www.embracesource.com
 */
public abstract class AbstractCoreNLPService implements CoreNLPServiceStatusCode, CoreNLPService {

	private static final Logger log = LoggerFactory.getLogger(AbstractCoreNLPService.class);

	/**
	 * 生成Map结果
	 * 
	 * @param statusCode
	 * @param msg
	 * @param ret
	 * @return
	 */
	protected Map<String, Object> generateMapResultWithoutSource(String statusCode, String msg, Object ret) {

		return generateMapResult(null, false, statusCode, msg, ret);
	}

	/**
	 * 生成Map结果（带原始报文）
	 * 
	 * @param statusCode
	 * @param msg
	 * @param ret
	 * @return
	 */
	@Override
	public Map<String, Object> generateMapResult(Object source, Boolean withSource, String statusCode, String msg, Object ret) {

		Map<String, Object> map = new HashMap<String, Object>();
		if (withSource) {
			map.put("source", source);
		}
		map.put("data", ret);
		map.put("status", statusCode);
		map.put("message", msg);

		log.debug("generateMapResult -> " + map);

		return map;
	}
	
	/**
	 * 将结果写入到目标文件
	 * @param ner
	 * @param filePath
	 * @return
	 */
	protected boolean writeNERToFS(String ner, String filePath) {
		
		boolean ret = false;
		File file = new File(filePath);
		if(file.exists()) {
			file.delete();
		}
		
		try {
			IOUtils.writeStringToFile(ner, filePath, CoreNLPNERUtil.ENCODE);
			ret = true;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
		}
		
		return ret;
	}
}
