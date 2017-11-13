package com.embracesource.corenlp.controller.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.embracesource.corenlp.entity.User;

/**
 * 
 * @author	tokings.tang@embracesource.com
 * @date	2017年11月10日 下午4:21:09
 * @copyright	http://www.embracesource.com
 */
@Controller
public class IndexController {

	@RequestMapping("/index")
	public ModelAndView index(ModelAndView mv) {
		mv.addObject("info", "test data");
		mv.setViewName("index/index");
		return mv;
	}
	
	@RequestMapping("/test")
	public String test(Map<String, Object> data) {
		data.put("test", "test info");
		return "test/test";
	}
	
	@RequestMapping("/index1")
	public ModelAndView index1(ModelAndView mv) {
		mv.addObject("info", "info1 data1");
		mv.setViewName("index/index1");
		return mv;
	}
	
	@RequestMapping("/test1")
	public String tes1t(Map<String, Object> data) {
		data.put("test", "test1 info");
		return "test/test1";
	}
	
	@ResponseBody
	@RequestMapping("/json")
	public Map<String, Object> json() {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		ret.put("name", "test");
		ret.put("id", UUID.randomUUID().toString());
		ret.put("gender", "male");
		ret.put("age", 111);
		ret.put("date", new Date());
		
		return ret;
	}
	
	@ResponseBody
	@RequestMapping("/user")
	public User user() {

		User user = new User("test", "female", 111, new Date(), UUID.randomUUID().toString());
		
		return user;
	}
}
