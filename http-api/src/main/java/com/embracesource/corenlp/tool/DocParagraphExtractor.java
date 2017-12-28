package com.embracesource.corenlp.tool;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class DocParagraphExtractor {
	static String outFileSuffix = ".out";
	static final String ENCOD1ING = "utf-8";
	static String inFilePath = "doc/917";
	static String outFilePath = "out";
	
	static Pattern paragraphStartPtn = Pattern.compile("^[一二三四五六七八九十]+\\s*[、 ，\\t].+");
	static Pattern reasonPtn1 = Pattern.compile("[一二三四五六七八九十]+.*(原因分析|故障分析|故障原因|原因|调查结论).{0,4}");
	// 有些原因段落是用了word序号生成的，获取的段落内容没有带序号
	static Pattern reasonPtn2 = Pattern.compile(".{0,4}(原因分析|故障分析|故障原因|原因|调查结论).{0,4}");

	public static void main(String[] args) throws Exception {
		
		System.out.println("start...");
		
//		test(); System.exit(0);
		
		File outFile = new File(outFilePath, "doc" + outFileSuffix);
		
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
		
//		for(Map<String, Object> map : list) {
////			pw.println("fileName===" + map.get("fileName").toString().trim());
//			pw.println("title===" + map.get("title").toString().trim());
//			pw.println("accidentName===" + map.get("accidentName").toString().trim());
////			pw.println("accidentReason===" + map.get("accidentReason").toString().trim());
////			pw.println("doc===" + map.get("doc") + "\n");
//			pw.println("--------------------------------------------------------------------------------");
//			pw.println();
//		}
		
		pw.println();
		pw.println();
		pw.println();
		pw.println("==========================================================================================");
		for(Map<String, Object> map : list) {
			Object reason = map.get("accidentReason");
			if(reason != null && ! "".equals(reason.toString().trim())) {
				pw.println("事故原因：" + reason.toString().trim());
				pw.println();
			}
			
		}
		
		pw.flush();
		pw.close();
		
		System.out.println("finished.");
	}
	
	private static void test() {
		
		String[] inFileNames = {
				"（报部）太原局关于12月10日瓦日线弓网故障的调查分析报告.docx",
				"1工务处关于南同蒲K684+450至K684+900下行挂板抢修情况报告[1].doc",
				"7.26太焦线41172防撞停车.docx",
				"7日太兴线56401防撞停车信息.docx",
		};
		
		for (int i = 0; i < inFileNames.length; i++) {

			File inFile = new File(inFilePath, inFileNames[i]);
			File outFile = new File(outFilePath, inFileNames[i] + outFileSuffix);

			if (!inFile.exists()) {
				System.out.println("文件不存在 :" + inFile);
				System.exit(1);
			}

			if (outFile.exists()) {
				outFile.delete();
			}

			Map<String, ?> ret = extract(inFile);
			ret.forEach((key, val) -> System.out.println(key + "===" + val));
		}
	}

	static List<String> extractDocx(File file) {
		// 获取各个段落的文本
		List<String> paraTexts = new ArrayList<String>();
		InputStream is = null;
		XWPFDocument xdoc = null;
		try {
			is = new FileInputStream(file);
			xdoc = new XWPFDocument(is);
			List<XWPFParagraph> paragraphs = xdoc.getParagraphs();
			paragraphs.forEach(paragraph -> paraTexts.add(paragraph.getText()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		close(xdoc);
		close(is);
		
		return paraTexts;
	}

	static List<String> extractDoc(File file) {
		
//		// 获取各个段落的文本
//		String[] paraTexts = new String[]{};
//		InputStream is = null;
//		WordExtractor extractor = null;
//		try {
//			is = new FileInputStream(file);
//			extractor = new WordExtractor(is);
//			paraTexts = extractor.getParagraphText();
//		} catch (Exception e) {
//			System.out.println(file.getName());
//			e.printStackTrace();
//		} finally {
//			close(extractor);
//			close(is);
//		}
//		return Arrays.asList(paraTexts);

		HWPFDocument doc = null;
		InputStream is = null;
		List<String> ret = new ArrayList<>();
		try {
			is = new FileInputStream(file);
			doc = new HWPFDocument(is);
			Range range = doc.getRange();
			// 获取段落数
			int paraNum = range.numParagraphs();
			for (int i = 0; i < paraNum; i++) {
//				System.out.println("段落" + (i + 1) + "：" + range.getParagraph(i).text());
				ret.add(range.getParagraph(i).text());
			}
		} catch (Exception e) {
			System.out.println(file.getAbsolutePath());
			e.printStackTrace();
		} finally {
			close(doc);
		}
		return ret;
	}

	static Map<String, Object> extract(File inFile) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
//		System.out.println("\n" + inFile.getName());

		List<String> paragraphs = new ArrayList<String>();
		
		try {
			if(inFile.getName().endsWith(".docx")) {
				paragraphs = extractDocx(inFile);
			}else if(inFile.getName().endsWith(".doc")) {
				paragraphs = extractDoc(inFile);
			}else {
				System.out.println("文件格式不对：" + inFile.getName());
			}

			List<String> suffixes = new ArrayList<>();
			suffixes.addAll(Arrays.asList(new String[] { "报告", "说明", "情况", "处置", "汇报", "调查", "分析", }));
			
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
//					System.out.println("段落:" + para.trim());
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
//			close(pw);
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
