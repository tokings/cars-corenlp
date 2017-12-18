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

public class WordSpliter {
	
	static final String CHARSET_ENCODE = "UTF-8";
	static final File OUTPUT_FILE_PATH = new File("C:/Users/admin/Desktop/txt_withdir_split/2017/201701");
	static final File INPUT_FILE_PATH = new File("E:/共致开源/文本分析/客户现场/txt_withdir/2017/201701");
	private static final String SPLIT_CHAR = " ";
	private static final String DEFAULT_NE = "O";

	public static void main(String[] args) {

		System.out.println("开始处理！");

		try {
			if(OUTPUT_FILE_PATH.exists()) {
				OUTPUT_FILE_PATH.delete();
			} else {
				OUTPUT_FILE_PATH.mkdirs();
			}
			
			if(INPUT_FILE_PATH.isDirectory()) {
				parseFile(INPUT_FILE_PATH.listFiles());
			} else {
				parseFile(INPUT_FILE_PATH);
			}
			
			System.out.println("处理完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void parseFile(File[] files) {
		for(File file : files) {
			if(file.isFile()) {
				parseFile(file);
			} else if(file.isDirectory()) {
				parseFile(file.listFiles());
			}
		}
	}
	
	static void parseFile(File file) {

		BufferedReader br = null;
		PrintWriter pw = null;
		
		try {
			File outFile = new File(OUTPUT_FILE_PATH, file.getName());
			
			if(outFile.exists()) outFile.delete();
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET_ENCODE));
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), CHARSET_ENCODE)));
			StringBuffer buf = new StringBuffer();
			String tmp = null;
			
			while ((tmp = br.readLine()) != null) {
				buf.append(tmp);
			}
			
			String text = buf.toString();
			char ch = ' ';
			
			for (int i = 0; i < text.length(); i++) {
				ch = text.charAt(i);
				if(ch == ' ' || ch == '\t' || ch == '\f' || ch == '　' || ch == '	') continue; // 	去除空白字符、两个space空白、tab制表符、换页符
				// 如果是汉语中句子结尾标点符号，则在后面追加一个换行符
				if(ch == '。' || ch == '！' || ch == '？' || ch == '…') {
					pw.println(ch + SPLIT_CHAR + DEFAULT_NE);
					pw.println();
				} else {
					pw.println(ch + SPLIT_CHAR + DEFAULT_NE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pw);
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
