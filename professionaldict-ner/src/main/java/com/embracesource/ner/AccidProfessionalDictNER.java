package com.embracesource.ner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事故专业词典辅助命名实体标注
 * 
 * @author tokings.tang@embracesource.com
 * @date 2017年11月23日 下午4:13:02
 * @copyright http://www.embracesource.com
 */
public class AccidProfessionalDictNER {

	/** 参数配置文件 */
	public static final String CONFIG_FILE = "config.properties";
	/** 每行记录切割正则表达式 */
	public static String SPLIT_REG = "\t";
	/** 编码格式 */
	public static String ENCODING = "UTF-8";
	/** 命名实体识别结果字段间隔字符 */
	public static String NER_FIELD_CHARSPACE = "\t";
	/** 命名实体识别输出文件后缀 */
	public static String NER_OUTFILE_EXTENTION = ".ner";

	private static Properties props = new Properties();

	/** 命名实体最小模糊匹配字符长度 */
	private static int minFuzzyMatchCharLength = 0;
	/** 命名实体匹配最小编辑距离 */
	private static int minEditDistance = 0;
	/** 专业词典文件路径 */
	private static String[] professionalDictsPath = null;
	/** 待命名实体识别文件路径 */
	private static String[] nerFilesPath = null;

	/** 专业词典集 */
	private static Map<String, NameEntityType> professionalDicts = new ConcurrentHashMap<>();

	/** 通过最小模糊匹配处理后的专业词典集 
	private static Map<String, NameEntityType> minFuzzyMatchProfessionalDicts = new ConcurrentHashMap<>();
	*/
	/** 待命名实体识别集 */
	private static Map<File, List<String[]>> nerFileMap = new ConcurrentHashMap<>();

	/**
	 * 初始化专业词典
	 * 
	 * @param professionalDictPaths
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void initDicts(String... professionalDictPaths) {

		if (professionalDictPaths == null || professionalDictPaths.length < 1)
			return;

		for (String filePath : professionalDictPaths) {
			File file = new File(filePath);
			if (!file.exists()) {
				continue;
			}

			initDicts(file);
		}
	}

	/**
	 * 初始化词典
	 * 
	 * @param files
	 */
	private static void initDicts(File... files) {
		if (files == null || files.length < 1)
			return;

		for (File file : files) {
			if (file.isDirectory()) {
				initDicts(file.listFiles());
			}

			if (file.isFile() && file.canRead()) {
				System.out.println("开始加载词典：" + file.getAbsolutePath());
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
					initDicts(br);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					close(br);
				}
			}
		}
	}

	/**
	 * 通过数据流初始化专业词典
	 * 
	 * @param br
	 * @throws IOException
	 */
	private static void initDicts(BufferedReader br) throws IOException {
		if (br == null)
			return;
		String line = null;
		while ((line = br.readLine()) != null) {
			// 词典数据不为空
			if (line != null && !"".equals(line.trim())) {
				String[] dicts = line.split(SPLIT_REG);
				if (dicts.length > 1 && !Util.isNull(dicts[0]) && !Util.isNull(dicts[1])) {
					NameEntityType net = NameEntityType.valueOf(dicts[1]);
					if (professionalDicts.containsKey(dicts[0])) {
						NameEntityType oldNet = professionalDicts.get(dicts[0]);
						System.out.println("字段实体类型重复：key：" + dicts[0] + ",旧值：" + oldNet + "新值：" + net);
						// 如果优先级高，则替换旧值
						if (net.compareTo(oldNet) < 0) {
							System.out.println("新值优先级更高，替换词典");
							professionalDicts.put(dicts[0], net);
						}
					} else {
						// professionalDicts不存在直接put
						professionalDicts.put(dicts[0], net);
					}
				} else {
					System.out.println("字典数据不符合规范：" + line);
				}
			}
		}
	}

	/**
	 * 通过专业词典初始化最小模糊匹配专业词典
	private static void initMinFuzzyMatchDicts() {
		// 并发处理
		professionalDicts.entrySet().forEach(entry -> {
			String word = entry.getKey();
			NameEntityType net = entry.getValue();
			Set<String> subWords = Util.getAllSubStr(word, minFuzzyMatchCharLength);

			subWords.forEach(subWord -> {
				if (minFuzzyMatchProfessionalDicts.containsKey(subWord)) {
					NameEntityType oldNet = minFuzzyMatchProfessionalDicts.get(subWord);
					System.out.println("字段实体类型重复：key：" + subWord + ",旧值：" + oldNet + "新值：" + net);
					// 如果优先级高，则替换旧值
					if (net.compareTo(oldNet) < 0) {
						System.out.println("新值优先级更高，替换词典");
						minFuzzyMatchProfessionalDicts.put(subWord, net);
					}
				} else {
					// minFuzzyMatchProfessionalDicts不存在直接put
					minFuzzyMatchProfessionalDicts.put(subWord, net);
				}
			});
		});
	}
	 */

	/**
	 * 关闭数据流
	 * 
	 * @param io
	 */
	private static void close(Closeable io) {
		try {
			if (io != null) {
				io.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			io = null;
		}
	}

	/**
	 * 命名实体标注
	 * 
	 * @param nerFiles
	 *            待标记文档 <br>
	 *            说明： <br>
	 *            1,匹配程度优先级：完全匹配 > 子串匹配 > 最小编辑距离
	 */
	private static void doNer() {
		// 并发处理
		nerFileMap.entrySet().forEach(entry -> {
			// 命名实体识别结果输出文件
			File nerOutFile = new File(entry.getKey().getAbsolutePath() + NER_OUTFILE_EXTENTION);
			// 如果文件存在先删除原始文件
			if (nerOutFile.exists())
				nerOutFile.delete();

			Iterator<String[]> iterator = entry.getValue().iterator();
			BufferedWriter bw = null;
			PrintWriter pw = null;

			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nerOutFile), ENCODING));
				pw = new PrintWriter(bw);
				while (iterator.hasNext()) {
					String[] fields = iterator.next();
					StringBuffer buf = new StringBuffer();
					// 将原始数据追加到每行
					Arrays.asList(fields).forEach(field -> buf.append(field).append(NER_FIELD_CHARSPACE));
					String ne = "O";
					// 如果一行字段不为空（即不是空行），并且第一个待识别的词不为空（包括null（不区分大小写），“”，“ ”）
					if (fields.length > 0 && !Util.isNull(fields[0])) {
						ne = generateNameEntity(fields[0]);
					}

					// 将命名实体识别结果追加到每行结尾
					buf.append(ne);
					// 将结果写入到文件当中
					pw.println(buf.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(pw);
				close(bw);
			}
		});
	}

	/**
	 * 生成词的命名实体
	 * <br>匹配程度优先级：完全匹配 > 子串匹配 > 最小编辑距离
	 * @param word
	 *            待处理词
	 * @return 词的命名实体
	 */
	private static String generateNameEntity(String word) {
		// 完全匹配
		if(professionalDicts.containsKey(word)) {
			return professionalDicts.get(word).name();
		}
		// 子串匹配
		final TreeMap<Integer, NameEntityType> tmpTreeMap = new TreeMap<>();
		Set<String> subWords = Util.getAllSubStr(word, minFuzzyMatchCharLength);
		subWords.forEach(subWord -> {
			if(professionalDicts.containsKey(subWord)) {
				tmpTreeMap.put(subWord.length(), professionalDicts.get(subWord));
			}
		});
		// 如果最小模糊匹配有结果
		if(! tmpTreeMap.isEmpty()) {
			// 通过treeMap排序，最后一个k-v的长度最长，取匹配最长的一个子串的命名实体
			return tmpTreeMap.get(tmpTreeMap.lastKey()).name();
		}

		// 最小编辑距离匹配
		tmpTreeMap.clear();
		professionalDicts.entrySet().forEach(entry -> {
			Integer key = Util.getMinEditDistance(word, entry.getKey());
			if(key == null || key > minEditDistance) {
//				System.out.println("最小编辑距离不符合要求或为空," + key + "," + word + "," + entry.getKey());
				return;
			}
			tmpTreeMap.put(key, entry.getValue());
		});
		
		// 如果符合最小编辑距离，则返回匹配的最小编辑距离对应词的命名实体
		if(! tmpTreeMap.isEmpty() && minEditDistance <= tmpTreeMap.firstKey()) {
			return tmpTreeMap.get(tmpTreeMap.firstKey()).name();
		}
		
		// 没有匹配到命名实体，则默认返回O（其他实体）
		return "O";
	}

	/**
	 * 初始化配置参数
	 * 
	 * @param configFile
	 * @throws Exception
	 */
	private static void initParams() throws Exception {
		props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE));
		if (props != null) {
			ENCODING = props.getProperty("encoding", ENCODING);
			SPLIT_REG = props.getProperty("dict.split.reg", SPLIT_REG);
			NER_FIELD_CHARSPACE = props.getProperty("ner.append.charspace", NER_FIELD_CHARSPACE);
			NER_OUTFILE_EXTENTION = props.getProperty("ner.outfile.extention", NER_OUTFILE_EXTENTION);
		}
	}

	public static void main(String[] args) throws Exception {

		if (!validArgs(args))
			return;

		System.out.println("--------------------开始加载参数配置--------------------");
		initParams();
		System.out.println("配置参数：");
		props.forEach((k, v) -> System.out.println(k + "," + v));

		System.out.println();
		System.out.println("--------------------开始初始化专业词典--------------------");
		initDicts(professionalDictsPath);
		System.out.println("专业词典大小：" + professionalDicts.size());

//		System.out.println();
//		System.out.println("--------------------开始初始化最小模糊匹配专业词典--------------------");
//		initMinFuzzyMatchDicts();
//		System.out.println("最小模糊匹配专业词典大小：" + minFuzzyMatchProfessionalDicts.size());

		System.out.println();
		System.out.println("--------------------开始解析待命名实体识别文件--------------------");
		parseNerFiles(nerFilesPath);
		System.out.println("解析待命名实体识别文件完成。文件数目：" + nerFileMap.size());

		System.out.println();
		System.out.println("--------------------开始进行NER--------------------");
		long nerStart = System.currentTimeMillis();
		doNer();

		System.out.println();
		System.out.println("--------------------NER结果--------------------");
		System.out.println("NER结束。耗时：" + (System.currentTimeMillis() - nerStart) + "ms");
	}

	/**
	 * 解析待命名实体识别文件
	 * 
	 * @param nerFilesPath
	 */
	private static void parseNerFiles(String... nerFilesPath) {
		// 并发处理
		Arrays.asList(nerFilesPath).forEach(file -> {
			File nerFile = new File(file);

			if (nerFile.isDirectory()) {
				parseNerFiles(nerFile.listFiles());
			} else if (nerFile.exists() && nerFile.canRead()) {
				parseNerFiles(nerFile);
			} else {
				System.out.println(nerFile + "不正确");
			}
		});
	}

	/**
	 * 解析待命名实体识别文件
	 * 
	 * @param listFiles
	 */
	private static void parseNerFiles(File... listFiles) {
		// 并发处理
		Arrays.asList(listFiles).forEach(nerFile -> {
			if (nerFile.isDirectory()) {
				parseNerFiles(nerFile.listFiles());
			} else if (nerFile.exists() && nerFile.canRead()) {
				parseNerFiles(nerFile);
			} else {
				System.out.println(nerFile + "不正确");
			}
		});
	}

	/**
	 * 解析待命名实体识别文件
	 * 
	 * @param nerFile
	 */
	private static void parseNerFiles(File nerFile) {
		BufferedReader br = null;
		List<String[]> ners = new ArrayList<String[]>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(nerFile), ENCODING));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] fileds = line.split(SPLIT_REG);
				if (fileds.length > 0) {
					ners.add(fileds);
				} else {
					System.out.println("待识别数据不符合规范:" + line);
				}
			}

			// 待识别数据集不为空
			if (!ners.isEmpty()) {
				nerFileMap.put(nerFile, ners);
				System.out.println("待识别文件：" + nerFile + ",待识别词数量：" + ners.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
	}

	/**
	 * 验证参数是否正确
	 * 
	 * @param args
	 * @return
	 */
	private static boolean validArgs(String[] args) {

		try {
			if (args.length < 4) {
				System.out.println("参数输入不正确，请重新输入！");
				System.out.println("示例：java -jar xxx com.embracesource.ner.AccidProfessionalDictNER"
						+ " minFuzzyMatchCharLength minEditDistance professionalDictsPath nerFilesPath");
				System.out.println("参数说明如下：");
				System.out.println("minFuzzyMatchCharLength:" + "最小模糊匹配字符长度");
				System.out.println("minEditDistance:" + "最小编辑距离");
				System.out.println("professionalDictsPath:" + "专业词典文件路径，可以是目录，多个以“英文逗号”隔开");
				System.out.println("nerFilesPath:" + "待命名实体识别的文件路径，可以是目录，多个以“英文逗号”隔开");
				return false;
			}

			minFuzzyMatchCharLength = Integer.parseInt(args[0]);
			minEditDistance = Integer.parseInt(args[1]);
			professionalDictsPath = args[2].split("\\s*,\\s*");
			nerFilesPath = args[3].split("\\s*,\\s*");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
