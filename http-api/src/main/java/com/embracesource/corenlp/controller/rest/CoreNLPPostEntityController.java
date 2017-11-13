package com.embracesource.corenlp.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.embracesource.corenlp.service.CoreNLPService;
import com.embracesource.corenlp.service.CoreNLPServiceStatusCode;
import com.embracesource.corenlp.util.CoreNLPNERUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 以Post方式提交Entity方式获取CoreNLP处理结果
 * @author tokings.tang@embracesource.com
 * @date 2017年11月10日 下午4:21:09
 * @copyright http://www.embracesource.com
 */
@Controller
@RequestMapping("/api")
public class CoreNLPPostEntityController {

	private static final Logger log = LoggerFactory.getLogger(CoreNLPPostEntityController.class);

	@Resource
	private CoreNLPService coreNLPService;

	public void setCoreNLPService(CoreNLPService coreNLPService) {
		this.coreNLPService = coreNLPService;
	}

	@ResponseBody
	@RequestMapping("/ner/json/parsestr")
	public Map<String, Object> nerForJson(@RequestBody String source) {
		
		log.debug("/ner/json/parsestr, req:{}", source);

		long start = System.currentTimeMillis();
		Map<String, Object> ret = new HashMap<>();
		List<Object> retList = new ArrayList<>();
		JsonElement jsonEle = CoreNLPNERUtil.jsonParser.parse(source);

		if (jsonEle != null && jsonEle.isJsonObject()) {
			JsonObject jsonObj = jsonEle.getAsJsonObject();
			JsonElement srcEle = jsonObj.get(CoreNLPNERUtil.HTTP_REQ_JSON_ROOT_NAME);

			if (srcEle != null && srcEle.isJsonArray()) {
				JsonArray srcArr = srcEle.getAsJsonArray();

				srcArr.forEach(srcJson -> {
					String src = srcJson.getAsString();
					retList.add(coreNLPService.doNER(src));
				});

			} else {
				log.error("source tag is null or not correct!");
				retList.add(coreNLPService.generateMapResult(source, true, CoreNLPServiceStatusCode.NER_PARAM_ERR, "解析请求参数出错", ""));
			}
		} else {
			log.error("input not correct");
			retList.add(coreNLPService.generateMapResult(source, true, CoreNLPServiceStatusCode.NER_PARAM_ERR, "解析请求参数出错", ""));
		}

		ret.put(CoreNLPNERUtil.HTTP_RESP_JSON_ROOT_NAME, retList);
		
		log.debug("/ner/json/parsestr finished, resp:{}.", ret);
		log.info("/ner/json/parsestr finished, used:{} ms.", (System.currentTimeMillis() - start));
		return ret;
	}

	@ResponseBody
	@RequestMapping("/ner/xml/parsestr")
	public Map<String, Object> nerForXML(@RequestBody String source) {
		return coreNLPService.doNER(source, "xml");
	}

	@ResponseBody
	@RequestMapping("/ner/tsv/parsestr")
	public Map<String, Object> nerForTSV(@RequestBody String source) {
		return coreNLPService.doNER(source, "tsv");
	}

	@ResponseBody
	@RequestMapping("/ner/tabbedEntities/parsestr")
	public Map<String, Object> nerForTabbedEntities(@RequestBody String source) {
		return coreNLPService.doNER(source, "tabbedEntities");
	}

	@ResponseBody
	@RequestMapping("/ner/slashTags/parsestr")
	public Map<String, Object> nerForSlashTags(@RequestBody String source) {
		return coreNLPService.doNER(source, "slashTags");
	}
}
