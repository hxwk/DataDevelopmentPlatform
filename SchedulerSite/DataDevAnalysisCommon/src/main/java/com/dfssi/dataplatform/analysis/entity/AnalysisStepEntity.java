package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

//@Alias("AnalysisStepEntity")
public class AnalysisStepEntity {

    private String id;
    private String modelId;
    private String name;
    private AnalysisStepTypeEntity stepType;
    private String buildType;
    private String mainClass;
    private long guiX;
    private long guiY;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;
    private List<AnalysisStepAttrEntity> attrs;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnalysisStepTypeEntity getStepType() {
        return stepType;
    }

    public void setStepType(AnalysisStepTypeEntity stepType) {
        this.stepType = stepType;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public long getGuiX() {
        return guiX;
    }

    public void setGuiX(long guiX) {
        this.guiX = guiX;
    }

    public long getGuiY() {
        return guiY;
    }

    public void setGuiY(long guiY) {
        this.guiY = guiY;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<AnalysisStepAttrEntity> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AnalysisStepAttrEntity> attrs) {
        this.attrs = attrs;
    }
}
