package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

//@Alias("AnalysisResourceEntity")
public class AnalysisResourceEntity extends AbstractAnalysisEntity {

    private static volatile long index = 0;

    private String resourceId;
    private String modelId;
    private String modelStepId;
    private String dataresName;
    private String dataresDescription;
    private String dataresType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    private String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;
    private String updateUser;
    private String isValid;
    private String sharedStatus;

    public AnalysisResourceEntity() {
    }

    public AnalysisResourceEntity(String modelId,
                                  String modelStepId,
                                  String dataresName,
                                  String dataresDesc,
                                  String dataresType) {
        init();
        this.modelId = modelId;
        this.modelStepId = modelStepId;
        this.dataresName = dataresName;
        this.dataresDescription = dataresDesc;
        this.dataresType = dataresType;
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

    public String getDataresDescription() {
        return dataresDescription;
    }

    public void setDataresDescription(String dataresDescription) {
        this.dataresDescription = dataresDescription;
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
        this.resourceId = DateFormatUtils.format(date, "yyyyMMddHHmmss") + indexStr;
        this.createDate = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }
}
