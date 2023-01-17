package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

//@Alias("AnalysisJarEntity")
public class AnalysisJarEntity extends AbstractAnalysisEntity {

    private static volatile long index = 0;

    private String jarId;
    private String jarName;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String uploadDate;
    private String uploadUser;
    private long jarSize;
    private String jarPath;
    private String isValid;
    private String sharedStatus;

    public AnalysisJarEntity() {
    }

    public AnalysisJarEntity(String jarName, String jarPath, String description, long jarSize) {
        init();
        this.jarName = jarName;
        this.description = description;
        this.jarPath = jarPath;
        this.jarSize = jarSize;
        this.isValid = "0";
        this.sharedStatus = "0";
    }

    public String getJarId() {
        return jarId;
    }

    public void setJarId(String jarId) {
        this.jarId = jarId;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public long getJarSize() {
        return jarSize;
    }

    public void setJarSize(long jarSize) {
        this.jarSize = jarSize;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(String sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    @Override
    public long nextIndex() {
        if (index == 9999) {
            index = 0;
            return index;
        } else {
            return index++;
        }
    }

    public void init() {
        long index = nextIndex();
        String indexStr = StringUtils.right("000000" + index, 6);
        Date date = new Date();
        this.jarId = DateFormatUtils.format(date, "yyyyMMddHHmmss") + indexStr;
        this.uploadDate = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }
}
