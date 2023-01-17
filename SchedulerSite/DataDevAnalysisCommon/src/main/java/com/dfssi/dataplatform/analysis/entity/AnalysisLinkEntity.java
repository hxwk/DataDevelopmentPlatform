package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Date;

//@Alias("AnalysisLinkEntity")
public class AnalysisLinkEntity {

    private String id;
    private String modelId;
    private String modelStepFromId;
    private String modelStepFromPos;
    private String modelStepToId;
    private String modelStepToPos;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelStepFromId() {
        return modelStepFromId;
    }

    public void setModelStepFromId(String modelStepFromId) {
        this.modelStepFromId = modelStepFromId;
    }

    public String getModelStepFromPos() {
        return modelStepFromPos;
    }

    public void setModelStepFromPos(String modelStepFromPos) {
        this.modelStepFromPos = modelStepFromPos;
    }

    public String getModelStepToId() {
        return modelStepToId;
    }

    public void setModelStepToId(String modelStepToId) {
        this.modelStepToId = modelStepToId;
    }

    public String getModelStepToPos() {
        return modelStepToPos;
    }

    public void setModelStepToPos(String modelStepToPos) {
        this.modelStepToPos = modelStepToPos;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Timestamp lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
