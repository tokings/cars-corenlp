package com.embracesource.corenlp;

import java.io.IOException;

import org.springframework.util.StringUtils;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class TestNER {

	private static String str = "李明 去 吃饭 ， 告诉 李强 一声 ， 叫他 不要 傻等 习近平 。 我 爱 北京 天安门 ， 天安门 向 太阳 升 ！ 伟大 领袖 毛主席 ， 指引 我们 向前 冲 ！";
	private static AbstractSequenceClassifier<CoreLabel> ner;

	static {
		String serializedClassifier = "classifiers/chinese.misc.distsim.crf.ser.gz"; // chinese.misc.distsim.crf.ser.gz
		try {
			ner = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sent
	 * @param outputFormat
	 *            "slashTags", "xml", "inlineXML", "tsv", or "tabbedEntities"
	 * @return
	 */
	public static String doNer(String sent, String outputFormat) {
		if(StringUtils.isEmpty(outputFormat)) {
			return ner.classifyToString(sent);
		}else {
			return ner.classifyToString(sent, outputFormat, true);
		}
	}

	public static void main(String[] args) {
		
		String ret = str;
		String[] nerArr = null;
		System.out.println(ret);
		
		System.out.println("default--------------------------------");
		ret = doNer(str, "");
		System.out.println("length:" + ret.length() + ",ret:" + ret);
		
		System.out.println("slashTags--------------------------------");
		ret = doNer(str, "slashTags");
		System.out.println("length:" + ret.length() + ",ret:" + ret);
		nerArr = ret.split("\\s+");
		System.out.println("slashTags ner detail:");
		int i = 0;
		for(String ner : nerArr) {
			i ++ ;
			String[] nerDetailArr = ner.split("/+");
			if(nerDetailArr.length > 1) {
				System.out.println(i + "->" + nerDetailArr[0] + ":" + nerDetailArr[1]);
			}else {
				System.out.println(i + "->" + nerDetailArr[0]);
			}
		}
		
		System.out.println("xml--------------------------------");
		ret = doNer(str, "xml");
		System.out.println("length:" + ret.length() + ",ret:" + ret);
		
		System.out.println("inlineXML--------------------------------");
		ret = doNer(str, "inlineXML");
		System.out.println("length:" + ret.length() + ",ret:" + ret);
		
		System.out.println("tsv--------------------------------");
		ret = doNer(str, "tsv");
		System.out.println("length:" + ret.length() + ",ret:" + ret);
		nerArr = ret.split(System.getProperty("line.separator"));
		System.out.println("tsv ner detail:");
		int j = 0;
		for(String ner : nerArr) {
			j ++ ;
			String[] nerDetailArr = ner.split("\t+");
			if(nerDetailArr.length > 1) {
				System.out.println(j + "->" + nerDetailArr[0] + ":" + nerDetailArr[1]);
			}else {
				System.out.println(j + "->" + nerDetailArr[0]);
			}
		}
		
		System.out.println("tabbedEntities--------------------------------");
		ret = doNer(str, "tabbedEntities");
		System.out.println("length:" + ret.length() + ",ret:" + ret);
		
		System.out.println("--------------------------------");
	}

}
