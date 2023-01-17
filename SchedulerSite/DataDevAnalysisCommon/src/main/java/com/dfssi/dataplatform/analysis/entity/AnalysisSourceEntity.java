package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

//@Alias("AnalysisSourceEntity")
public class AnalysisSourceEntity extends AbstractAnalysisEntity {

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

    @Override
    public long nextIndex() {
        return 0;
    }
}
