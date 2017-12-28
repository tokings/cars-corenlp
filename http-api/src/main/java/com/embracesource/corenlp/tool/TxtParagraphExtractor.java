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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TxtParagraphExtractor {
	static String outFileSuffix = ".out";
	static final String ENCOD1ING = "utf-8";
	static String inFilePath = "txt/917";
//	static String inFilePath = "txt/test";
	static String outFilePath = "out";
	
	static Pattern paragraphStartPtn = Pattern.compile("^[一二三四五六七八九十|0-9]+\\s*[、 ，\\t].+");
	static Pattern reasonPtn1 = Pattern.compile("[一二三四五六七八九十]+.{0,2}(原因分析|故障分析|故障原因|原因|调查结论).{0,4}");
	// 有些原因段落是用了word序号生成的，获取的段落内容没有带序号
	static Pattern reasonPtn2 = Pattern.compile("[^一二三四五六七八九十]{0,4}(原因分析|故障分析|故障原因|原因|调查结论).{0,4}");

	public static void main(String[] args) throws Exception {
		
		System.out.println("start...");
		
		File outFile = new File(outFilePath, "txt" + outFileSuffix);
		
		if(outFile.exists()) {
			outFile.delete();
		}
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
		
		List<Map<String, Object>> list = new ArrayList<>();
		File inFile = new File(inFilePath);
		if(inFile.isDirectory()) {
			for(File file : inFile.listFiles()) {
				Map<String, Object> ret = extract(file);
				list.add(ret);
			}
		}
		
		pw.println("==========================================================================================");
		pw.println("开始打印事故标题、名称、原因");
		pw.println("==========================================================================================");
		for(Map<String, Object> map : list) {
			pw.println("fileName===" + map.get("fileName").toString().trim());
			pw.println("title===" + map.get("title").toString().trim());
			pw.println("accidentName===" + map.get("accidentName").toString().trim());
			pw.println("accidentReason===" + map.get("accidentReason").toString().trim());
			pw.println("--------------------------------------------------------------------------------");
			pw.println();
		}
		
		pw.println("==========================================================================================");
		pw.println("开始打印事故原因");
		pw.println("==========================================================================================");
		for(Map<String, Object> map : list) {
			Object reason = map.get("accidentReason");
			if(reason != null && ! "".equals(reason.toString().trim())) {
				pw.println("事故原因：" + reason.toString().trim());
			} else {
//				pw.println("文本内容：" + map.get("doc").toString().trim());
			}
		}
		
		pw.flush();
		pw.close();
		
		System.out.println("finished.");
	}
	
	static Map<String, Object> extract(File inFile) {
//		System.out.println("\n" + inFile.getName());
		Map<String, Object> ret = new HashMap<String, Object>();
		List<String> paragraphs = new ArrayList<String>();
		BufferedReader br = null;
		String[] sentenceEndSymbols = new String[]{"。", "！", "？", "…"};
		
		String tmp = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "utf-8"));
			while((tmp = br.readLine()) != null) {
				paragraphs.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
		
		try {
			String[] suffixes = new String[] { "报告", "说明", "情况", "处置", "汇报", "调查", "分析", };
			int i = 0;
			boolean titleEnd = false;
			boolean reasonStart = false;
			boolean reasonEnd = false;
			StringBuffer title = new StringBuffer();
			StringBuffer reason = new StringBuffer();
			StringBuffer doc = new StringBuffer();
			
			for(String para : paragraphs) {
				// 去除空白行
				if("".equals(para.trim())) continue;
				
				doc.append(para.replaceAll(" +", ""));
				// 标题抽取
				if(! titleEnd && i < 2) { 
					for(String suffix : suffixes) {
						if(para.trim().contains(suffix)) {
							title.append(para);
							titleEnd = true;
							break;
						}
					}
				}
				// 标题解析成txt之后最多只占用两行
				if(! titleEnd && i == 0) { 
					title.append(para.trim());
				} else if(! titleEnd && i == 1) {
					title.append(para.trim());
					titleEnd = true;
				}
				
				// 原因抽取
				if(paragraphStartPtn.matcher(para.trim()).matches()) {
					if(reasonStart) {
						reasonStart = false;
						reasonEnd = true;
					} else if(reasonPtn1.matcher(para.trim()).matches() || reasonPtn2.matcher(para.trim()).matches()) {
						reasonStart = true;
					}
				} else if(reasonStart && !reasonEnd) {
					reason.append(para);
				}
				
				i++;
			}
			// 如果没有匹配到原因，直接使用整个文档作为输出
//			if(reason.length() < 1) {
//				reason = doc;
//			}
			
			String accidentName = "";
			if (title.length() > 100) {
				accidentName = NameRegularExtractor.extractName(inFile.getName().trim().replaceAll("[(\\.docx)|(\\.doc)]", ""));
			} else {
				accidentName = NameRegularExtractor.extractName(title.toString().trim());
			}
			
			ret.put("fileName", inFile.getName());
			ret.put("title", title);
			ret.put("accidentName", accidentName);
			ret.put("accidentReason", reason.toString().trim());
			ret.put("doc", doc.toString().trim());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			close(br);
		}
		
		return ret;
	}

	private static void close(Closeable io) {
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
