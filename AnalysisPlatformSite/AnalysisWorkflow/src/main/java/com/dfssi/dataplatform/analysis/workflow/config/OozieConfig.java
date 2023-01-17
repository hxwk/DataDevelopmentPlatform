package com.dfssi.dataplatform.analysis.workflow.config;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("oozieConfig")
@PropertySource("oozie.properties")
@ConfigurationProperties(prefix = "oozie")
public class OozieConfig implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private String oozieUrl;
    private String yarnHistoryServer;
    private String nameNode;
    private String jobTracker;
    private String offlineSparkActionClass;
    private String streamingSparkActionClass;
    private String externalSparkActionClass;
    private String sparkActionjar;
    private String libpath;
    private String sparkRootPath;
    private String defaultSparkOpts;
    private String sparkOpts;
    private String hdfsUserName;
    private String yarnRestUrl;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext = applicationContext;
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

    public String getStreamingSparkActionClass() {
        return streamingSparkActionClass;
    }

    public void setStreamingSparkActionClass(String streamingSparkActionClass) {
        this.streamingSparkActionClass = streamingSparkActionClass;
    }

    public String getExternalSparkActionClass() {
        return externalSparkActionClass;
    }

    public void setExternalSparkActionClass(String externalSparkActionClass) {
        this.externalSparkActionClass = externalSparkActionClass;
    }

    public String getSparkActionjar() {
        return sparkActionjar;
    }

    public void setSparkActionjar(String sparkActionjar) {
        this.sparkActionjar = sparkActionjar;
    }

    public String getLibpath() {
        return libpath;
    }

    public void setLibpath(String libpath) {
        this.libpath = libpath;
    }

    public String getSparkRootPath() {
        return sparkRootPath;
    }

    public void setSparkRootPath(String sparkRootPath) {
        this.sparkRootPath = sparkRootPath;
    }

    public String getDefaultSparkOpts() {
        return defaultSparkOpts;
    }

    public void setDefaultSparkOpts(String defaultSparkOpts) {
        this.defaultSparkOpts = defaultSparkOpts;
    }

    public String getSparkOpts() {
        return sparkOpts;
    }

    public void setSparkOpts(String sparkOpts) {
        this.sparkOpts = sparkOpts;
    }

    public String getHdfsUserName() {
        return hdfsUserName;
    }

    public void setHdfsUserName(String hdfsUserName) {
        this.hdfsUserName = hdfsUserName;
    }

    public String getYarnRestUrl() {
        return yarnRestUrl;
    }

    public void setYarnRestUrl(String yarnRestUrl) {
        this.yarnRestUrl = yarnRestUrl;
    }
}

