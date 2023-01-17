package com.dfssi.dataplatform.analysis.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ServiceModelEntity {

    private String modelId;
    private String name;
    private String modelType;
    private String description;
    private String modelConf;
    private String status;
    private String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    private String updateUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;
    private int isValid;

    public ServiceModelEntity() {
    }

    public ServiceModelEntity(String modelId,
                              String name,
                              String modelType,
                              String description,
                              String modelConf,
                              String status,
                              String createUser,
                              String createDate,
                              String updateUser,
                              String updateDate,
                              int isValid) {
        this.modelId = modelId;
        this.name = name;
        this.modelType = modelType;
        this.description = description;
        this.modelConf = modelConf;
        this.status = status;
        this.createUser = createUser;
        this.createDate = createDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.isValid = isValid;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelConf() {
        return modelConf;
    }

    public void setModelConf(String modelConf) {
        this.modelConf = modelConf;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }
}
