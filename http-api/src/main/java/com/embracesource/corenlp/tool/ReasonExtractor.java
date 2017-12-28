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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//			"[[^\\_‘’“”\"\"\\*#%&\\(\\)\\-]&&[\\pP]]?[(造成)|(导致)]",
public class ReasonExtractor {

	private static String infile = "reason.txt";
	private static final String ENCOD1ING = "utf-8";
	static String inFilePath = "txt";
	static String outFilePath = "out";
	static String outFileSuffix = ".out";
	static String[] regexs = new String[]{
				
//			".*1\\.(.+)(2\\..+)(3\\..+)",
			".*原因.*一是(.+)二是(.+)三是(.+).*",
			".*综上所述，(.+).*",
			".*综上所述(.+).*",
			
			".*由于(.+)造成.*导致.*引起.*",
			".*由于(.+)造成.*导致.*造成.*",
			".*造成.*原因.*由于(.+)导致.*",
			".*因(.+)引起.*导致.*是.*原因.*",
			".*因(.+)导致.*造成.*引起.*",
			".*因(.+)导致.*是.*原因.*",
			".*因(.+)造成.*是.*原因.*",
			".*故(.+)是造成.*原因.*",
			
			".*初步估计(.+)造成.*",
			".*分析为：(.+)造成.*",
			".*分析为:(.+)引起.*",
			".*分析为(.+)造成.*",
			".*分析为(.+)引起.*",
			".*原因是由于(.+)导致.*",
			".*原因为：(.+)导致.*",
			".*原因为(.+)导致.*",
			".*由于(.+)造成.*导致.*",
			".*由于(.+)导致.*造成.*",
			".*由于(.+)导致.*导致.*",
			".*由于(.+)造成.*造成.*",
			".*由于(.+)造成.*",
			".*由于(.+)导致.*",
			"(.+)引起.*是.*原因.*",
			"(.+)引起.*造成.*",
			"(.+)导致.*是.*原因.*",
			".*造成.*原因为(.+).*",
			".*造成.*原因是(.+)导致.*",
			".*造成.*原因是(.+)造成.*",
			".*造成.*原因是(.+).*",
			"(.+)造成.*是.*原因.*",
			"(.+)是造成.*原因.*",
			"(.+)造成.*原因.*",
			"(.+)造成.*导致.*",
			"(.+)造成.*引起.*",
			".*是(.+)造成的.*",
			"(.+)是导致.*原因.*",
			".*为(.+)引起.*",
			".*因(.+)造成.*",
			".*因(.+)导致.*",
			"(.+)导致.*引起.*",
			"(.+)是.*原因.*",
			
			".*由于(.+)",
			"(.+)导致.*",
			"(.+)造成.*",
			"(.+)致使.*",
			".*原因为：(.+)",
			".*判定为(.+)",
			".*判断为(.+)",
			".*分析认为：(.+)",
			".*分析认为(.+)",

	};
	static List<Pattern> ptns = new ArrayList<>();
	static String sentenceSplitReg = "[。！？(……)]";
	
	public static void main(String[] args) {

		System.out.println("start...");
		
//		test();

		init();

		File inFile = new File(inFilePath, infile);
		File outFile = new File(outFilePath, infile + outFileSuffix);

		if (!inFile.exists()) {
			System.out.println("inFile is not exist! inFile:" + inFile);
			System.exit(1);
		}

		if (outFile.exists()) {
			outFile.delete();
		}

		abstractReason(inFile, outFile);
		
		System.out.println("end.");
	}
	
	private static void test() {
		String src = ",.!，，D_NAME。！；‘’”“**dfs  #$%^&()-+1431221\"\"中           国123漢字かどうかのjavaを決定";
		System.out.println(src);
		String str = src.replaceAll("[[^\\_‘’“”\"\"\\*#%&\\(\\)\\-]&&[\\pP]]?", "");
		System.out.println(str);
		System.exit(0);}

	private static void init() {
		for (int i = 0; i < regexs.length; i++) {
			ptns.add(Pattern.compile(regexs[i]));
		}
	}

	private static void abstractReason(File inFile, File outFile) {

		BufferedReader br = null;
		PrintWriter pw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ENCOD1ING));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), ENCOD1ING));
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}

				RegularBean bean = abstractReason(line);
				++ i;
				pw.println();
				pw.println(i + "\n" + bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pw);
			close(br);
		}

	}
	
	private static RegularBean abstractReason(String line) {

		RegularBean bean = new RegularBean(line);
		// 匹配整段原因
		for (int i = 0; i < ptns.size(); i++) {
			Matcher matcher = ptns.get(i).matcher(line);
			if (matcher.matches()) {
				bean.setAllSentenceBean(new SentenceRegularBean(line, ptns.get(i).pattern(), matcher));
				break;
			}
		}
		// 匹配原因段落每个句子
		String[] sentences = line.split(sentenceSplitReg);
		if(sentences.length > 1) {
			for (String sentence : sentences) {
				for (int i = 0; i < ptns.size(); i++) {
					Matcher matcher = ptns.get(i).matcher(sentence);
					if (matcher.matches()) {
						bean.getBeans().add(new SentenceRegularBean(sentence, ptns.get(i).pattern(), matcher));
						break;
					}
				}
			}
		}

		return bean;
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

class RegularBean {

	String src;
	SentenceRegularBean allSentenceBean;
	List<SentenceRegularBean> beans = new ArrayList<>();

	public RegularBean(String src) {
		super();
		this.src = src;
		allSentenceBean = new SentenceRegularBean();
	}

	public SentenceRegularBean getAllSentenceBean() {
		return allSentenceBean;
	}

	public void setAllSentenceBean(SentenceRegularBean allSentenceBean) {
		this.allSentenceBean = allSentenceBean;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public List<SentenceRegularBean> getBeans() {
		return beans;
	}

	public void setBeans(List<SentenceRegularBean> beans) {
		this.beans = beans;
	}

	@Override
	public String toString() {

		StringBuffer beansBuf = new StringBuffer();

		if(allSentenceBean != null) {
			beansBuf.append("全局匹配:")
				.append("\n")
				.append("正则表达式:")
				.append(allSentenceBean.getRegex())
				.append("\n")
				.append("句子:")
				.append(allSentenceBean.getSentence())
				.append("\n")
				.append("匹配结果:")
				.append("\n");
			Matcher matcher = allSentenceBean.getMatcher();
			if(matcher != null) {
				for (int j = 1; j <= matcher.groupCount(); j++) {
					beansBuf.append(matcher.group(j)).append("\n");
				}
			}
		}
		
		beansBuf.append("\n句子匹配:\n");
		int i = 0;
		for (SentenceRegularBean bean : beans) {
			Matcher matcher = bean.getMatcher();
			StringBuffer buf = new StringBuffer();
			buf
				.append("正则表达式:")
				.append(bean.getRegex())
				.append("\n")
				.append("句子:")
				.append(bean.getSentence())
				.append("\n")
				.append("匹配结果:");

			if (matcher != null && matcher.matches()) {
				for (int j = 1; j <= matcher.groupCount(); j++) {
					buf.append(matcher.group(j)).append("\n");
				}
			}

			++i;
			beansBuf.append("第" + i + "次匹配:" + buf.toString()).append("\n");
		}

		return "src：" + src + "\n" + "reason：\n" + beansBuf + "\n";
	}

}

class SentenceRegularBean {

	String regex;
	String sentence;
	Matcher matcher;

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public Matcher getMatcher() {
		return matcher;
	}

	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

	public SentenceRegularBean(String sentence, String regex, Matcher matcher) {
		super();
		this.regex = regex;
		this.sentence = sentence;
		this.matcher = matcher;
	}

	public SentenceRegularBean() {
		super();
	}

}
