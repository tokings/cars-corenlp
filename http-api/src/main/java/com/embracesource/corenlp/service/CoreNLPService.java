package com.embracesource.corenlp.service;

import java.util.List;
import java.util.Map;

import com.embracesource.corenlp.exception.CoreNLPException;

/**
 * 
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月10日 下午4:50:23
 * @copyright	http://www.embracesource.com
 */
public interface CoreNLPService {
	
	/**
	 * 对数据做命名实体识别
	 * @param source
	 * @param respDataType 
	 * 		  	可选值："slashTags", "xml", "inlineXML", "tsv", or "tabbedEntities"
	 * @return
	 * @throws CoreNLPException
	 */
	Map<String, Object> doNER(Object source, String respDataType) throws CoreNLPException;
	
	/**
	 * 对数据做命名实体识别
	 * 
	 * @param sources
	 * @param respDataType
	 * 		  	可选值："slashTags", "xml", "inlineXML", "tsv", or "tabbedEntities"
	 * @return
	 * @throws CoreNLPException
	 */
	List<Map<String, Object>> doNER(List<Object> sources, String respDataType) throws CoreNLPException;
	

	/**
	 * 对数据做命名实体识别
	 * 
	 * @param sources
	 * @return
	 * @throws CoreNLPException
	 */
	List<Map<String, Object>> doNER(List<Object> sources) throws CoreNLPException;

	/**
	 * 对数据做命名实体识别
	 * 
	 * @param source
	 * @return
	 * @throws CoreNLPException
	 */
	Map<String, Object> doNER(Object source) throws CoreNLPException;
	
	/**
	 * 对一个文件内容做命名实体识别
	 * @param filePath
	 * @return
	 * @throws CoreNLPException
	 */
	Map<String, Object> doNERByFile(String filePath) throws CoreNLPException;

	/**
	 * 对文件列表内容做命名实体识别
	 * @param filelist
	 * @return
	 * @throws CoreNLPException
	 */
	List<Map<String, Object>> doNERByFilelist(List<String> filelist) throws CoreNLPException;

	/**
	 * 
	 * @param source
	 * @param withSource
	 * @param statusCode
	 * @param msg
	 * @param ret
	 * @return
	 */
	Map<String, Object> generateMapResult(Object source, Boolean withSource, String statusCode, String msg, Object ret);
}
