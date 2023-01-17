package com.dfssi.dataplatform.analysis.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MonitorCoordinatorEntity {

    private int id;
    private String modelId;
    private String oozieTaskId;
    private String name;
    private String status;
    private String cronExp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String coordStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String coordEnd;
    private String timezone;

    public MonitorCoordinatorEntity() {
    }

    public MonitorCoordinatorEntity(String modelId,
                                    String oozieTaskId,
                                    String name,
                                    String status,
                                    String cronExp,
                                    String coordStart,
                                    String coordEnd,
                                    String timezone) {
        this.modelId = modelId;
        this.oozieTaskId = oozieTaskId;
        this.name = name;
        this.status = status;
        this.cronExp = cronExp;
        this.coordStart = coordStart;
        this.coordEnd = coordEnd;
        this.timezone = timezone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getCoordStart() {
        return coordStart;
    }

    public void setCoordStart(String coordStart) {
        this.coordStart = coordStart;
    }

    public String getCoordEnd() {
        return coordEnd;
    }

    public void setCoordEnd(String coordEnd) {
        this.coordEnd = coordEnd;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
