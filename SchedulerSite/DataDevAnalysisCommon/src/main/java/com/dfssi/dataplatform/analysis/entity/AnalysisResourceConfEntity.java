package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

//@Alias("AnalysisResourceConfEntity")
public class AnalysisResourceConfEntity extends AbstractAnalysisEntity {

    private static volatile long index = 0;

    private String attrId;
    private String resourceId;
    private String paramName;
    private String paramValue;
    private String paramDescription;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;

    public AnalysisResourceConfEntity() {
    }

    public AnalysisResourceConfEntity(String resourceId, String paramName, String paramValue) {
        init();
        this.resourceId = resourceId;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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
        this.attrId = DateFormatUtils.format(date, "yyyyMMddHHmmss") + indexStr;
        this.createDate = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }
}
