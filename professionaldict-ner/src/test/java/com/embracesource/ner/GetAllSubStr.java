package com.embracesource.ner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GetAllSubStr {


	/**
	 * 获取字符串最小长度的所有顺序组合
	 * @param src	原字符串
	 * @param minLength	最小子串长度
	 */
	public static Set<String> getAllSubStr(String src, int minLength) {
		
		// 不符合规范直接返回空集合
		if(Util.isNull(src) || minLength < 1 || minLength > src.length()) {
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

	public static void main(String[] args) {
		
		Set<String> set = getAllSubStr("我们都是中国人", 2);
		
		set.forEach(str -> System.out.println(str));
		
		System.out.println("ret size:" + set.size());
	}
}
