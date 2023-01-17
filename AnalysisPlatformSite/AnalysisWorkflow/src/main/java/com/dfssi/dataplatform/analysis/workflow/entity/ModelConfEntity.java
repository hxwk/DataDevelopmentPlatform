package com.dfssi.dataplatform.analysis.workflow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ModelConfEntity {

    private String modelId;
    private String modelType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastModifiedDate;
    private String modelConf;

    public ModelConfEntity() {
    }

    public ModelConfEntity(String modelId, String modelType, String lastModifiedDate, String modelConf) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.lastModifiedDate = lastModifiedDate;
        this.modelConf = modelConf;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getModelConf() {
        return modelConf;
    }

    public void setModelConf(String modelConf) {
        this.modelConf = modelConf;
    }
}
