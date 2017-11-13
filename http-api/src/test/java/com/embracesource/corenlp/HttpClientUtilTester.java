package com.embracesource.corenlp;

import org.junit.Test;

import com.embracesource.corenlp.util.HttpClientUtil;

public class HttpClientUtilTester {

	@Test
	public void testByPostEntity() {

		String targetURL = "http://192.168.1.122:8011/rest/postentity/ner/json";
		String body = "我们 都是 中国 人 ！";

		String ret = HttpClientUtil.sendHttpPostJson(targetURL, body);
		
		System.out.println(ret);
	}
	
	@Test
	public void testByPostParams() {
		
		String targetURL = "http://192.168.1.122:8011/rest/postparam/ner/json";
		String body = "source=我们 都是 中国 人 ！";
		
		String ret = HttpClientUtil.sendHttpPost(targetURL, body);
		
		System.out.println(ret);
	}
}
