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
import java.util.HashMap;
import java.util.Map;

/**
 * 命名实体标注前预处理
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月29日 下午5:21:22
 * @copyright	http://www.embracesource.com
 */
public class NerPreprocess {

	static final String OUT_FILE_EXTENTION = ".ner";
	private static final String CHARSET = "UTF-8";
	private static Map<String, String> dict = new HashMap<String, String>();
	private static File outputPath = null;

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("请输入需要预处理的文件路径以及输出的路径：");
			System.out.println("示例：");
			System.out.println("java -jar xxx.jar com.embracesource.corenlp.NerPreprocess inputPath outputPath");
			System.exit(1);
		}
		
		outputPath = new File(args[1]);
		if(! outputPath.exists()) {
			outputPath.mkdirs();
		} else if(outputPath.isFile()) {
			outputPath.delete();
			outputPath.mkdirs();
		}
		
		readDict();
		System.out.println("字典集大小：" + dict.size());

		File inputPath = new File(args[0]);

		if (!inputPath.exists()) {
			System.out.println("文件不存在：" + inputPath.getAbsolutePath());
			System.exit(2);
		}

		if (inputPath.isFile()) {
			process(inputPath);
		} else if (inputPath.isDirectory()) {
			process(inputPath.listFiles());
		}
		
		System.out.println("处理完毕！");
	}

	private static void readDict() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					Thread.currentThread().getContextClassLoader().getResourceAsStream("dict.txt"), CHARSET));
			String line = null;
			String newLine = null;
			while((line = br.readLine()) != null) {
				String[] dictArr = line.split("\t");
				if(dictArr.length > 1) {
					String type = dictArr[1];
					
					switch (type) {
					case "name":
						newLine = dictArr[0] + "\tn\t" + dictArr[1];
						break;
					case "locale":
						newLine = dictArr[0] + "\tns\t" + dictArr[1];
						break;
					case "reason":
						newLine = dictArr[0] + "\tn\t" + dictArr[1];
						break;
					case "depart":
						newLine = dictArr[0] + "\tn\t" + dictArr[1];
						break;
					case "depart2":
						newLine = dictArr[0] + "\tn\t" + dictArr[1];
						break;
					default:
						newLine = dictArr[0] + "\tx\t" + dictArr[1];
						break;
					}
					
					dict.put(dictArr[0], newLine);
				} else {
					System.out.println("数据字典行不合法：" + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
	}

	private static void process(File file) {
		BufferedReader br = null;
		File outFile = new File(outputPath, file.getName() + OUT_FILE_EXTENTION);
		PrintWriter pw = null;
		String line = null;
		String newLine = null;

		try {
			// 如果存在，先删除
			if(outFile.exists()) {
				outFile.delete();
			}
			
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), CHARSET)));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET));

			while ((line = br.readLine()) != null) {
				String[] arr = line.split("\t");
				
				if(arr.length > 1) {
					// 如果在词典中匹配到的话就替换
					if(dict.containsKey(arr[0])) {
						newLine = dict.get(arr[0]);
					// 如果识别的词性标注为x，且词包含“站”、“线”，且词长度大于2，则标注为地点
					} else if(
							(arr[0].contains("站") || arr[0].contains("线"))
							&& arr[0].length() > 2 && "x".equals(arr[1])) { 
						newLine = line + "\tlocale";
					// 如果识别的词性标注为地点（ns），则标注为地点实体（locale）。可能会有一定的误差，主要看词典是否准确
					} else if("ns".equals(arr[1])) { 
						newLine = line + "\tlocale";
					} else {// 默认为O(other)
						newLine = line + "\tO";
					}
				} else {// 如果数据不合法，直接在后面追加\tO
					// 如果有词性标注，也保留，防止dos系统和linux系统换行符不一致问题出现linux不能识别的特殊空行问题
					if(line.matches(".*\\w$")) { 
						newLine = line + "\tO";
					} else {
						// 直接丢弃
						System.out.println("输入数据不合法，文件：" + file.getName() + "，内容：" + line);
						continue;
					}
				}
				
				pw.println(newLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pw);
			close(br);
		}
	}

	private static void process(File... files) {
		for (File file : files) {
			if (!file.exists()) {
				System.out.println("文件不存在：" + file.getAbsolutePath());
				continue;
			} else if (file.isDirectory()) {
				process(file.listFiles());
			} else {
				process(file);
			}
		}
	}

	static void close(Closeable io) {
		if (io != null) {
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
