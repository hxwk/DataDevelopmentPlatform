package com.dfssi.dataplatform.manager.monitor;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("oozieConfig")
@PropertySource("classpath:oozie.properties")
@ConfigurationProperties(prefix = "oozie")
public class OozieConfig implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private String oozieUrl;
    private String yarnHistoryServer;
    private String nameNode;
    private String jobTracker;
    private String hdfsUserName;
    private String oozieRestApiUrl;

    public String getNameNode() {
        return nameNode;
    }

    public void setNameNode(String nameNode) {
        this.nameNode = nameNode;
    }

    public String getJobTracker() {
        return jobTracker;
    }

    public void setJobTracker(String jobTracker) {
        this.jobTracker = jobTracker;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static OozieConfig getInstance() {
        return (OozieConfig) applicationContext.getBean("oozieConfig");
    }

    public String getHdfsUserName() {
        return hdfsUserName;
    }

    public void setHdfsUserName(String hdfsUserName) {
        this.hdfsUserName = hdfsUserName;
    }

    public String getOozieUrl() {
        return oozieUrl;
    }

    public void setOozieUrl(String oozieUrl) {
        this.oozieUrl = oozieUrl;
    }

    public String getYarnHistoryServer() {
        return yarnHistoryServer;
    }

    public void setYarnHistoryServer(String yarnHistoryServer) {
        this.yarnHistoryServer = yarnHistoryServer;
    }

    public String getOozieRestApiUrl() {
        return oozieRestApiUrl;
    }

    public void setOozieRestApiUrl(String oozieRestApiUrl) {
        this.oozieRestApiUrl = oozieRestApiUrl;
    }
}

