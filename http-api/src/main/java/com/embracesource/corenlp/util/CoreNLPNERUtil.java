package com.embracesource.corenlp.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

/**
 * 
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月13日 上午10:12:42
 * @copyright	http://www.embracesource.com
 */
public class CoreNLPNERUtil {

	private static final Logger log = LoggerFactory.getLogger(CoreNLPNERUtil.class);

	/**
	 * 默认编码格式
	 */
	public static final String ENCODE = "utf-8";

	/**
	 * 默认ner结果文件后缀
	 */
	public static final String NER_DEFAULT_OUTPUT_FILE_EXTENTION = ".json";
	
	/**
	 * Http API接口json请求报文根节点名称
	 */
	public static final String HTTP_REQ_JSON_ROOT_NAME = "req";
	
	/**
	 * Http API接口json响应报文根节点名称
	 */
	public static final String HTTP_RESP_JSON_ROOT_NAME = "resp";
	
	/**
	 * Json解析器
	 */
	public static final JsonParser jsonParser = new JsonParser();
	
	/**
	 * Gson处理Json实例
	 */
	public static final Gson gson = new Gson();
	
	/**
	 * 将NER结果解析为K-V键值对
	 * @param nerSource
	 * @return
	 */
	public static Map<String, Object> parseNerBySlashTags(String nerSource) {
		
		if(StringUtils.isEmpty(nerSource)) {
			return Collections.emptyMap();
		}
		
		Map<String, Object> ret = new HashMap<String, Object>();
		// 切分每个NER
		String[] nerArr = nerSource.split("\\s+");
		log.debug("slashTags ner detail:");
		int i = 0;
		for(String ner : nerArr) {
			i ++ ;
			// 切分每个NER的key和value
			String[] nerDetailArr = ner.split("/+");
			if(nerDetailArr.length > 1) {
				log.debug(i + "->" + nerDetailArr[0] + ":" + nerDetailArr[1]);
				ret.put(nerDetailArr[0], nerDetailArr[1]);
			}else {
				log.debug(i + "->" + nerDetailArr[0]);
			}
		}
		
		return ret;
	}
}
