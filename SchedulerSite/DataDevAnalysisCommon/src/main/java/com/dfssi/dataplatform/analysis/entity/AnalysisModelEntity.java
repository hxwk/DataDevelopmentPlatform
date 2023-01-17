package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

//@Alias("AnalysisModelEntity")
public class AnalysisModelEntity {

    private String id;
    private String name;
    private AnalysisModelTypeEntity modelType;
    private String description;
    private String cronExp;
    private String createUser;
    private String updateUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;
    private String status;
    private String sparkOpts;

    private String timezone;
    private String coordStart;
    private String coordEnd;

    private String batchDurationSecond;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnalysisModelTypeEntity getModelType() {
        return modelType;
    }

    public void setModelType(AnalysisModelTypeEntity modelType) {
        this.modelType = modelType;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSparkOpts() {
        return sparkOpts;
    }

    public void setSparkOpts(String sparkOpts) {
        this.sparkOpts = sparkOpts;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCoordStart() {
        return coordStart;
    }

    public void setCoordStart(String coordStart) {
        this.coordStart = coordStart;
    }

    public String getCoordEnd() {
        return coordEnd;
    }

    public void setCoordEnd(String coordEnd) {
        this.coordEnd = coordEnd;
    }

    public String getBatchDurationSecond() {
        return batchDurationSecond;
    }

    public void setBatchDurationSecond(String batchDurationSecond) {
        this.batchDurationSecond = batchDurationSecond;
    }
}
