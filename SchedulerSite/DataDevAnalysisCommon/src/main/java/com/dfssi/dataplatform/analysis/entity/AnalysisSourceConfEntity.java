package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

//@Alias("AnalysisSourceConfEntity")
public class AnalysisSourceConfEntity extends AbstractAnalysisEntity {

    private String dataresourceAccessInfoId;
    private String dataresourceId;
    private String parameterName;
    private String parameterValue;
    private String parameterDesc;
    private String componentType;
    private char isValid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    private String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;
    private String updateUser;

    public String getDataresourceAccessInfoId() {
        return dataresourceAccessInfoId;
    }

    public void setDataresourceAccessInfoId(String dataresourceAccessInfoId) {
        this.dataresourceAccessInfoId = dataresourceAccessInfoId;
    }

    public String getDataresourceId() {
        return dataresourceId;
    }

    public void setDataresourceId(String dataresourceId) {
        this.dataresourceId = dataresourceId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public char getIsValid() {
        return isValid;
    }

    public void setIsValid(char isValid) {
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

    @Override
    public long nextIndex() {
        return 0;
    }
}
