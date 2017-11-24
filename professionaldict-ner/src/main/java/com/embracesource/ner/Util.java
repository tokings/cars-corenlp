package com.embracesource.ner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Util {

	/**
	 * 判断对象是否为空（NULL、“”，“ ”,null,NULL都为true）
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNull(Object o) {

		if (o == null || "".equals(o) || "null".equalsIgnoreCase(o.toString()) || "".equals(o.toString().trim())) {
			return true;
		}

		return false;
	}

	/**
	 * 获取字符串最小长度的所有顺序组合
	 * 
	 * @param src
	 *            原字符串
	 * @param minLength
	 *            最小子串长度
	 */
	public static Set<String> getAllSubStr(String src, int minLength) {

		// 不符合规范直接返回空集合
		if (Util.isNull(src) || minLength < 1 || minLength > src.length()) {
			return Collections.emptySet();
		}

		Set<String> set = new HashSet<>();
		int maxLength = src.length();

		for (int i = minLength; i <= maxLength; i++) {

			int num = maxLength - i + 1;

			for (int j = 0; j < num; j++) {
				set.add(src.substring(j, i + j));
			}
		}

		return set;
	}

	/**
	 * 获取两个词的最小编辑距离
	 * 
	 * @param wrongWord
	 * @param rightWord
	 * @return
	 */
	public static int getMinEditDistance(String wrongWord, String rightWord) {

		final int m = wrongWord.length();
		final int n = rightWord.length();

		int[][] d = new int[m + 1][n + 1];
		for (int j = 0; j <= n; ++j) {
			d[0][j] = j;
		}
		for (int i = 0; i <= m; ++i) {
			d[i][0] = i;
		}

		for (int i = 1; i <= m; ++i) {
			char ci = wrongWord.charAt(i - 1);
			for (int j = 1; j <= n; ++j) {
				char cj = rightWord.charAt(j - 1);
				if (ci == cj) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					// 等号右边的分别代表 将ci改成cj 错串加cj 错串删ci
					d[i][j] = Math.min(d[i - 1][j - 1] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j] + 1));
				}
			}
		}

		return d[m][n];
	}
}
