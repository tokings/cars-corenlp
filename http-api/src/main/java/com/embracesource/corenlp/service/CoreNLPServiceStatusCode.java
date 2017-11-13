package com.embracesource.corenlp.service;

/**
 * CoreNLP结果状态码
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月13日 上午10:36:52
 * @copyright	http://www.embracesource.com
 */
public interface CoreNLPServiceStatusCode {

	String SUCCESSS = "0";
	
	String NER_ERROR = "1001";
	
	String NER_EMPTY = "1002";
	
	String NER_PARAM_ERR = "1003";

	String NER_FILE_NOT_EXIST = "2001";
	
	String NER_FILE_IS_DIR = "2002";
	
	String NER_FILELIST_NOT_EXIST = "2003";
	
	String NER_FILE_IS_EMPTY = "2004";

	String NER_FILE_WRITE_ERR = "2005";
	
}
