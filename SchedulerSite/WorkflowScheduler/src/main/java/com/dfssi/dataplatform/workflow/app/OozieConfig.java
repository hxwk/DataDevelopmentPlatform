package com.dfssi.dataplatform.workflow.app;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("oozieConfig")
@PropertySource("config/oozie.properties")
@ConfigurationProperties(prefix = "oozie")
public class OozieConfig implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private String oozieUrl;
    private String yarnHistoryServer;
    private String nameNode;
    private String jobTracker;
    private String offlineSparkActionClass;
    private String streamingSparkActionClass;
    private String sparkActionjar;
    //任务依赖包路径
    private String libpath;
    private String sparkRootPath;
    private String sparkMaster;
    private String offlineSparkOpts;
    private String streamingSparkOpts;
    private String sparkOpts;
    private String integrateSparkActionClass;
    private String externalSparkActionClass;
    private String hdfsUserName;
    // yarn rest url
    private String yarnRestUrl;

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

    public String getOfflineSparkActionClass() {
        return offlineSparkActionClass;
    }

    public void setOfflineSparkActionClass(String offlineSparkActionClass) {
        this.offlineSparkActionClass = offlineSparkActionClass;
    }

    public String getSparkActionjar() {
        return sparkActionjar;
    }

    public void setSparkActionjar(String sparkActionjar) {
        this.sparkActionjar = sparkActionjar;
    }

    public String getSparkRootPath() {
        return sparkRootPath;
    }

    public void setSparkRootPath(String sparkRootPath) {
        this.sparkRootPath = sparkRootPath;
    }

    public String getSparkMaster() {
        return sparkMaster;
    }

    public void setSparkMaster(String sparkMaster) {
        this.sparkMaster = sparkMaster;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static OozieConfig getInstance() {
        return (OozieConfig) applicationContext.getBean("oozieConfig");
    }

    public String getOfflineSparkOpts() {
        return offlineSparkOpts;
    }

    public void setOfflineSparkOpts(String offlineSparkOpts) {
        this.offlineSparkOpts = offlineSparkOpts;
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

    public String getStreamingSparkActionClass() {
        return streamingSparkActionClass;
    }

    public String getIntegrateSparkActionClass() {
        return integrateSparkActionClass;
    }

    public String getExternalSparkActionClass() {
        return externalSparkActionClass;
    }
    public void setExternalSparkActionClass(String externalSparkActionClass) {
        this.externalSparkActionClass = externalSparkActionClass;
    }
    public void setStreamingSparkActionClass(String streamingSparkActionClass) {
        this.streamingSparkActionClass = streamingSparkActionClass;
    }

    public String getStreamingSparkOpts() {
        return streamingSparkOpts;
    }
    public String getSparkOpts() {
        return sparkOpts;
    }
    public void setSparkOpts(String sparkOpts) {
        this.sparkOpts = sparkOpts;
    }
    public void setStreamingSparkOpts(String streamingSparkOpts) {
        this.streamingSparkOpts = streamingSparkOpts;
    }

    public String getYarnHistoryServer() {
        return yarnHistoryServer;
    }

    public void setYarnHistoryServer(String yarnHistoryServer) {
        this.yarnHistoryServer = yarnHistoryServer;
    }

    public void setIntegrateSparkActionClass(String integrateSparkActionClass) {
        this.integrateSparkActionClass = integrateSparkActionClass;
    }

    public String getLibpath() {
        return libpath;
    }

    public void setLibpath(String libpath) {
        this.libpath = libpath;
    }

    public String getYarnRestUrl() {
        return yarnRestUrl;
    }

    public void setYarnRestUrl(String yarnRestUrl) {
        this.yarnRestUrl = yarnRestUrl;
    }
}

