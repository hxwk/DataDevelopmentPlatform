package com.dfssi.dataplatform.metadata.entity;

import java.util.HashMap;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/12 13:12
 */
//@Alias("ServerDataSourceEntity")
public class ServerDataSourceEntity {
    private String modelId;
    private String dataresName;
    private String dataresDesc;
    private String dataresType;
    private HashMap<String,String> params;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getDataresName() {
        return dataresName;
    }

    public void setDataresName(String dataresName) {
        this.dataresName = dataresName;
    }

    public String getDataresDesc() {
        return dataresDesc;
    }

    public void setDataresDesc(String dataresDesc) {
        this.dataresDesc = dataresDesc;
    }

    public String getDataresType() {
        return dataresType;
    }

    public void setDataresType(String dataresType) {
        this.dataresType = dataresType;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}
