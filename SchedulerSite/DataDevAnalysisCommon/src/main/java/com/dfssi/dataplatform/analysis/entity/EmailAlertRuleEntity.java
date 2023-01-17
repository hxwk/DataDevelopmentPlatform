package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

//@Alias("EmailAlertRuleEntity")
public class EmailAlertRuleEntity {

    private static volatile long index = 0;

    private String ruleId;
    private String name;
    private String status;
    private int isValid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;
    private String createUser;
    private String updateUser;

    public EmailAlertRuleEntity() {
    }

    public EmailAlertRuleEntity(String name,
                                String status,
                                int isValid,
                                String createDate,
                                String updateDate,
                                String createUser,
                                String updateUser) {
        this.init();
        this.name = name;
        this.status = status;
        this.isValid = isValid;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createUser = createUser;
        this.updateUser = updateUser;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
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

    public long nextIndex() {
        if (index == 9999) {
            index = 0;
            return index;
        } else {
            return index++;
        }
    }

    private void init() {
        long index = nextIndex();
        String indexStr = StringUtils.right("000000" + index, 6);
        Date date = new Date();
        this.ruleId = DateFormatUtils.format(date, "yyyyMMddHHmmss") + indexStr;
    }
}
