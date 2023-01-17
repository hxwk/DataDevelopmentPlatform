package com.dfssi.dataplatform.external.common.shedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SheJob extends AbstractJob {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	//开发者实现该接口定义需要执行的任务。JobExecutionContext类提供调度上下文的各种信息  
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//通过上下文获取  
		JobKey jobKey = context.getJobDetail().getKey();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(jobKey + "\n" + df.format(new Date()) + " 开始执行任务...");
		//do more这里可以执行其他需要执行的任务
		ITask task = (ITask) context.getMergedJobDataMap().get("task");
		task.exec();
	}
}
