package com.dfssi.dataplatform.devmanager.analysistask.mvc.entity;

import com.dfssi.dataplatform.devmanager.analysistask.mvc.base.BaseVO;
import org.apache.ibatis.type.Alias;

@Alias("analysisModelEntity")
public class AnalysisModelEntity extends BaseVO {

    private String id;
    private String name;
    private AnalysisModelTypeEntity modelType;
    private String description;
    private String cronExp;
    private String status;

    private String startTime;
    private String endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnalysisModelTypeEntity getModelType() {
        return modelType;
    }

    public void setModelType(AnalysisModelTypeEntity modelType) {
        this.modelType = modelType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
