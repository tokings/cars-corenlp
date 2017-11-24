package com.embracesource.ner;

/**
 * 命名实体类型
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月24日 上午9:27:59
 * @copyright	http://www.embracesource.com
 */
public enum NameEntityType {
	
	/** 事故地点 **/
	locale(1),
	/** 部门 **/
	depart(2),
	/** 事故名称 **/
	name(3),
	/** 定责部门 **/
	depart2(4),
	/** 事故类型 **/
	type(5),
	/** 事故原因 **/
	reason(6),
	/** 事故等级 **/
	grade(7);
	
	/**
	 * 专业词典实体类型优先级
	 */
	private int value;

	/**
	 * 命名实体类型
	 * @param value	专业词典实体类型优先级
	 */
	private NameEntityType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
