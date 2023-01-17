package com.dfssi.dataplatform.analysis.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ResourceEntity{

    private String resourceId;
    private String modelId;
    private String modelStepId;
    private String dataresName;
    private String dataresType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    private String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;
    private String updateUser;
    private int isValid;
    private int sharedStatus;

    public ResourceEntity() {
    }

    public ResourceEntity(String modelId,
                          String modelStepId,
                          String dataresName,
                          String dataresType,
                          String createDate,
                          String createUser,
                          String updateDate,
                          String updateUser,
                          int isValid,
                          int sharedStatus) {
        this.modelId = modelId;
        this.modelStepId = modelStepId;
        this.dataresName = dataresName;
        this.dataresType = dataresType;
        this.createDate = createDate;
        this.createUser = createUser;
        this.updateDate = updateDate;
        this.updateUser = updateUser;
        this.isValid = isValid;
        this.sharedStatus = sharedStatus;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelStepId() {
        return modelStepId;
    }

    public void setModelStepId(String modelStepId) {
        this.modelStepId = modelStepId;
    }

    public String getDataresName() {
        return dataresName;
    }

    public void setDataresName(String dataresName) {
        this.dataresName = dataresName;
    }

    public String getDataresType() {
        return dataresType;
    }

    public void setDataresType(String dataresType) {
        this.dataresType = dataresType;
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

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public int getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(int sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

}
