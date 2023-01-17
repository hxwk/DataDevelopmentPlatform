package com.dfssi.dataplatform.devmanage.dataresource.mvc.entity;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseVO;
import org.apache.ibatis.type.Alias;

/**
 */
@Alias("dataResourceAccessEntity")
public class DataResourceAccessEntity extends BaseVO {

    private String dsAccessInfoId;
    private String parameterName;
    private String parameterValue;
    private String dsId;

    public String getDsAccessInfoId() {
        return dsAccessInfoId;
    }

    public void setDsAccessInfoId(String dsAccessInfoId) {
        this.dsAccessInfoId = dsAccessInfoId;
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

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    @Override
    public String toString() {
        return "DataResourceAccessEntity{" +
                "dsAccessInfoId='" + dsAccessInfoId + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", getParameterValue='" + parameterValue + '\'' +
                ", dsId='" + dsId + '\'' +
                '}';
    }
}
