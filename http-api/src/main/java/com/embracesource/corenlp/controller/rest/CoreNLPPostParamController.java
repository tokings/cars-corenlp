package com.embracesource.corenlp.controller.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.embracesource.corenlp.service.CoreNLPService;

/**
 * 
 * @author tokings.tang@embracesource.com
 * @date 2017年11月10日 下午4:21:09
 * @copyright http://www.embracesource.com
 */
@Controller
@RequestMapping("/api")
public class CoreNLPPostParamController {

	Logger log = LoggerFactory.getLogger(CoreNLPPostParamController.class);

	@Resource
	private CoreNLPService coreNLPService;

	public void setCoreNLPService(CoreNLPService coreNLPService) {
		this.coreNLPService = coreNLPService;
	}

	@RequestMapping("/ner")
	public ModelAndView nerForHtml(Map<String, Object> params) {

		log.info("req :" + params);

		ModelAndView mav = new ModelAndView("ner/result");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("map-key", "key value info");

		mav.addObject("test", "ner info");

		mav.addObject("ret");

		mav.addAllObjects(data);

		return mav;
	}

	@ResponseBody
	@RequestMapping("/ner/json/parseparam")
	public Map<String, Object> nerForJson(@RequestParam(name = "source", required = true) String params) {

		return coreNLPService.doNER(params);
	}

	@ResponseBody
	@RequestMapping("/ner/xml/parseparam")
	public Map<String, Object> nerForXML(@RequestParam(name = "source", required = true) String source) {
		return coreNLPService.doNER(source, "xml");
	}

	@ResponseBody
	@RequestMapping("/ner/tsv/parseparam")
	public Map<String, Object> nerForTSV(@RequestParam(name = "source", required = true) String source) {
		return coreNLPService.doNER(source, "tsv");
	}

	@ResponseBody
	@RequestMapping("/ner/tabbedEntities/parseparam")
	public Map<String, Object> nerForTabbedEntities(@RequestParam(name = "source", required = true) String source) {
		return coreNLPService.doNER(source, "tabbedEntities");
	}

	@ResponseBody
	@RequestMapping("/ner/slashTags/parseparam")
	public Map<String, Object> nerForSlashTags(@RequestParam(name = "source", required = true) String source) {
		return coreNLPService.doNER(source, "slashTags");
	}
}
