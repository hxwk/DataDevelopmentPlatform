package com.dfssi.dataplatform.analysis.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MonitorYarnEntity {

    private String applicationId;
    private String modelId;
    private String oozieTaskId;
    private String name;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String finishTime;
    private int runContainers;
    private int memory;
    private int virtualCores;
    private float progress;

    public MonitorYarnEntity() {
    }

    public MonitorYarnEntity(String applicationId,
                             String modelId,
                             String oozieTaskId,
                             String name,
                             String status,
                             String startTime,
                             String finishTime,
                             int runContainers,
                             int memory,
                             int virtualCores,
                             float progress) {
        this.applicationId = applicationId;
        this.modelId = modelId;
        this.oozieTaskId = oozieTaskId;
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.runContainers = runContainers;
        this.memory = memory;
        this.virtualCores = virtualCores;
        this.progress = progress;
    }

    public String getActionId() {
        return applicationId;
    }

    public void setActionId(String actionId) {
        this.applicationId = actionId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getOozieTaskId() {
        return oozieTaskId;
    }

    public void setOozieTaskId(String oozieTaskId) {
        this.oozieTaskId = oozieTaskId;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public int getRunContainers() {
        return runContainers;
    }

    public void setRunContainers(int runContainers) {
        this.runContainers = runContainers;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getVirtualCores() {
        return virtualCores;
    }

    public void setVirtualCores(int virtualCores) {
        this.virtualCores = virtualCores;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
