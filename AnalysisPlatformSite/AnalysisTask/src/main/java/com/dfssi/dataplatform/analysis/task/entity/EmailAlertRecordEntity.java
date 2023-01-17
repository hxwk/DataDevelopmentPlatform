package com.dfssi.dataplatform.analysis.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EmailAlertRecordEntity {

    private int recordId;
    private String modelId;
    private int isEmail;
    private int isPhone;
    private int isDispose;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;

    public EmailAlertRecordEntity() {
    }

    public EmailAlertRecordEntity(String modelId,
                                  int isEmail,
                                  int isPhone,
                                  int isDispose,
                                  String message,
                                  String createDate,
                                  String updateDate) {
        this.modelId = modelId;
        this.isEmail = isEmail;
        this.isPhone = isPhone;
        this.isDispose = isDispose;
        this.message = message;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public int getIsEmail() {
        return isEmail;
    }

    public void setIsEmail(int isEmail) {
        this.isEmail = isEmail;
    }

    public int getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(int isPhone) {
        this.isPhone = isPhone;
    }

    public int getIsDispose() {
        return isDispose;
    }

    public void setIsDispose(int isDispose) {
        this.isDispose = isDispose;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

}
