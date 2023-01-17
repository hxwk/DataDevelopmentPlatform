package com.dfssi.scheduling.admin.controller;

import com.dfssi.scheduling.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/taskStatistical")
public class IndexController {

	@Resource
	private XxlJobService xxlJobService;

    @RequestMapping("/chartInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> chartInfo(String startDate, String endDate) throws ParseException {
        ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(DateUtils.parseDate(startDate,"yyyy-MM-dd HH:mm:ss"), DateUtils.parseDate(endDate,"yyyy-MM-dd HH:mm:ss"));
        return chartInfo;
    }
	

}
