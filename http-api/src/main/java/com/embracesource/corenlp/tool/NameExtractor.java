package com.embracesource.corenlp.tool;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NameExtractor {
	
	private static final String ENCOD1ING = "utf-8";
	static String inFilePath = "";
	static String outFilePath = "";
	
	public static void main(String[] args) {
		
		File inFile = new File(inFilePath);
		File outFile = new File(outFilePath);
		
		if(! inFile.exists()) {
			System.out.println("inFile is not exist! inFile:" + inFile);
			System.exit(1);
		}
		
		if(outFile.exists()) {
			outFile.delete();
		}
		
		abstractName(inFile, outFile);
	}

	private static void abstractName(File inFile, File outFile) {
		
		BufferedReader br = null;
		PrintWriter pw = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ENCOD1ING));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), ENCOD1ING));
			String line = null;
			
			while((line = br.readLine()) != null) {
				
				if("".equals(line.trim())) continue;
				
				Map<String, Object> ret = abstractName(line);
				
				if(ret.isEmpty()) continue;
				
				for(Entry<String, Object> entry : ret.entrySet()) {
					pw.print(entry.getKey() + ":" + entry.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pw);
			close(br);
		}
		
	}
	
	private static Map<String, Object> abstractName(String line) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		
		return ret;
	}

	private static void close(Closeable io) {
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
