package com.embracesource.corenlp.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 测试使用
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月10日 下午4:39:15
 * @copyright	http://www.embracesource.com
 */
public class User implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String gender;
	private int age;
	private Date date;
	private String id;

	public User() {
	}
	
	public User(String name, String gender, int age, Date date, String id) {
		this.name = name;
		this.gender = gender;
		this.age = age;
		this.date = date;
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
