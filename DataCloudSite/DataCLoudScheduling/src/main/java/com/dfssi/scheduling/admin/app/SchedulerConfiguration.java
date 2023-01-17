package com.dfssi.scheduling.admin.app;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * 任务调度初始化
 * Created by yanghs on 2018/8/23.
 */
@Configuration
public class SchedulerConfiguration {

    private DataSource datasource;

    public SchedulerConfiguration(DataSource datasource){
        this.datasource=datasource;
    }

    @Bean(name = "scheduler")
    public SchedulerFactoryBean quartzScheduler() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean=new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(datasource);
        schedulerFactoryBean.setAutoStartup(true);//自动启动
        schedulerFactoryBean.setStartupDelay(20);//延时启动，应用启动成功后在启动
        schedulerFactoryBean.setOverwriteExistingJobs(true);//覆盖DB中JOB：true、以数据库中已经存在的为准：false
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        return schedulerFactoryBean;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
