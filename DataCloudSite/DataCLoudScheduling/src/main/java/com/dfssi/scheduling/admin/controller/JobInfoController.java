package com.dfssi.scheduling.admin.controller;

import com.dfssi.scheduling.admin.core.enums.ExecutorFailStrategyEnum;
import com.dfssi.scheduling.admin.core.model.XxlJobGroup;
import com.dfssi.scheduling.admin.core.model.XxlJobInfo;
import com.dfssi.scheduling.admin.core.route.ExecutorRouteStrategyEnum;
import com.dfssi.scheduling.admin.dao.XxlJobGroupDao;
import com.dfssi.scheduling.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	private static final Logger logger = LoggerFactory.getLogger(JobInfoController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobService xxlJobService;


	@RequestMapping
	public String index(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	// 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	// 阻塞处理策略-字典
		model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());		// 失败处理策略-字典

		// 任务组
		List<XxlJobGroup> jobGroupList =  xxlJobGroupDao.findAll();
		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}

	//查询任务接口
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, String jobDesc, String executorHandler, String filterTime) {
		logger.info("开始执行/pageList查询任务接口");
		//sql查询使用了limit语法默认statr从0开始  但是前段传参第一条记录是从1开始计算
		if(start > 0){
			start = start -1;
		}
		return xxlJobService.pageList(start, length, jobGroup, jobDesc, executorHandler, filterTime);
	}

	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ReturnT<String> add(@RequestBody XxlJobInfo jobInfo) {
		logger.info("开始执行/add新增任务接口");
		return xxlJobService.add(jobInfo);
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(@RequestBody XxlJobInfo jobInfo) {
		logger.info("开始执行/update修改任务接口");
		return xxlJobService.update(jobInfo);
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id) {
		logger.info("开始执行/remove删除任务接口");
		return xxlJobService.remove(id);
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(int id) {
		logger.info("开始执行/pause暂停任务接口");
		return xxlJobService.pause(id);
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<String> resume(int id) {
		logger.info("开始执行/resume恢复任务接口");
		return xxlJobService.resume(id);
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(int id) {
		logger.info("开始执行/trigger执行任务接口");
		return xxlJobService.triggerJob(id);
	}
	
}
