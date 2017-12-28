package com.embracesource.corenlp.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * 抽取所有文档中的站
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月28日 下午2:47:10
 * @copyright	http://www.embracesource.com
 */
public class StationExtractor {
	
	static final StanfordCoreNLP CORE_NLP = new StanfordCoreNLP("StanfordCoreNLP-chinese.properties");
	static final Pattern STATION_PTN = Pattern.compile("([^x00-xff]+站)");
	static final String SPLIT_PTN = "[[\\p{P}||\\p{S}]&&[^、\\-\\_——\\+``“”]]+";
	static final File OUTPUT_FILE_PATH = new File("C:/Users/admin/Desktop/station.out");
	static final File INPUT_FILE_PATH = new File("C:/Users/admin/Desktop/accident_txt/indexed");
	static final String CHARSET_ENCODE = "utf-8";

	public static void main(String[] args) {

		PrintWriter pw = null;
		try {
			if(OUTPUT_FILE_PATH.exists()) {
				OUTPUT_FILE_PATH.delete();
			}
			
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE_PATH), CHARSET_ENCODE)));
			
			if(INPUT_FILE_PATH.isDirectory()) {
				parseFile(INPUT_FILE_PATH.listFiles(), pw);
			} else {
				parseFile(INPUT_FILE_PATH, pw);
			}
			
			System.out.println("处理完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pw);
		}
	}

	static void parseFile(File[] files, PrintWriter writer) {
		for(File file : files) {
			if(file.isFile()) {
				parseFile(file, writer);
			} else if(file.isDirectory()) {
				parseFile(file.listFiles(), writer);
			}
		}
	}
	
	static void parseFile(File file, PrintWriter writer) {

		BufferedReader br = null;
		
		try {
			String tmp = null;
			StringBuffer buf = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET_ENCODE));
			
			while ((tmp = br.readLine()) != null) {
				buf.append(tmp);
			}
			
			String text = buf.toString();
			
			Annotation annotation = new Annotation(text);
			CORE_NLP.annotate(annotation);
			List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
			
			for (CoreMap sentence : sentences) {
				String line = sentence.get(TextAnnotation.class);
				String[] strArr = line.split(SPLIT_PTN);
				
				for (String str : strArr) {
					Matcher matcher = STATION_PTN.matcher(str);
					while(matcher.find()) {
						String groupStr = matcher.group();
						writer.println(groupStr);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
	}
	
	static void close(Closeable io) {
		if(io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				io = null;
			}
		}
		
	}
}
