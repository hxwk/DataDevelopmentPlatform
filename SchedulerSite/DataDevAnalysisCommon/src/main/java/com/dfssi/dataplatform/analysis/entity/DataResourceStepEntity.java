package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DataResourceStepEntity {

    private String showName;
    private String dataresourceId;
    private String dataresourceName;
    private String dataresourceDesc;
    private String dataresourceType;
    private int isValid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    private String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;
    private String updateUser;
    private int sharedStatus;
    private String stepTypeId;
    private String buildType;
    private String mainClass;

    public DataResourceStepEntity(AnalysisSourceEntity drce, String stepTypeId, String buildType, String mainClass) {
        this.showName = drce.getShowName();
        this.dataresourceId = drce.getDataresourceId();
        this.dataresourceName = drce.getDataresourceName();
        this.dataresourceDesc = drce.getDataresourceDesc();
        this.dataresourceType = drce.getDataresourceType();
        this.isValid = drce.getIsValid();
        this.createDate = drce.getCreateDate();
        this.createUser = drce.getCreateUser();
        this.updateDate = drce.getUpdateDate();
        this.updateUser = drce.getUpdateUser();
        this.sharedStatus = drce.getSharedStatus();
        this.stepTypeId = stepTypeId;
        this.buildType = buildType;
        this.mainClass = mainClass;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getDataresourceId() {
        return dataresourceId;
    }

    public void setDataresourceId(String dataresourceId) {
        this.dataresourceId = dataresourceId;
    }

    public String getDataresourceName() {
        return dataresourceName;
    }

    public void setDataresourceName(String dataresourceName) {
        this.dataresourceName = dataresourceName;
    }

    public String getDataresourceDesc() {
        return dataresourceDesc;
    }

    public void setDataresourceDesc(String dataresourceDesc) {
        this.dataresourceDesc = dataresourceDesc;
    }

    public String getDataresourceType() {
        return dataresourceType;
    }

    public void setDataresourceType(String dataresourceType) {
        this.dataresourceType = dataresourceType;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public int getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(int sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    public String getStepTypeId() {
        return stepTypeId;
    }

    public void setStepTypeId(String stepTypeId) {
        this.stepTypeId = stepTypeId;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
}
