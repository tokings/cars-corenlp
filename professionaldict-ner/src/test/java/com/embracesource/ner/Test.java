
package com.embracesource.ner;

public class Test {
	
	public static void main(String[] args) {
		
		String[] ret = "a,b, c , d".split("\\s*,\\s*");
		
		System.out.println(ret.length);
		
		for (int i = 0; i < ret.length; i++) {
			System.out.println(ret[i]);
		}
		
		NameEntityType depart = NameEntityType.depart;
		NameEntityType depart2 = NameEntityType.depart2;
		System.out.println(depart.getValue() + "," + depart2.getValue() + ":" + depart.compareTo(depart2));
		
	}
}
