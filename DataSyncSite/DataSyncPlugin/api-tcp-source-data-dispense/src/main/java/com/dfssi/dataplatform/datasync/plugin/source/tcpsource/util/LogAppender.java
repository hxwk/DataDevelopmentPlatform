package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

/**
 * 自定义LogAppender  实现级别输出到不同文件
 * @author Hannibal
 *
 */
public class LogAppender extends DailyRollingFileAppender {
	
	@Override
    public boolean isAsSevereAsThreshold(Priority priority) {
          //只判断是否相等，而不判断优先级     
        return this.getThreshold().equals(priority);    
    } 

}
