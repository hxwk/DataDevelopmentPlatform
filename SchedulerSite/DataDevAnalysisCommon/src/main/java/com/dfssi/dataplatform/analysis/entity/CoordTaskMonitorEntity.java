package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

//@Alias("CoordTaskMonitorEntity")
public class CoordTaskMonitorEntity {

    private static volatile long index = 0;

    private String id;
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

    public CoordTaskMonitorEntity() {
    }

    public CoordTaskMonitorEntity(String modelId,
                                  String oozieTaskId,
                                  String name,
                                  String status,
                                  String cronExp,
                                  String coordStart,
                                  String coordEnd,
                                  String timezone) {
        this.init();
        this.modelId = modelId;
        this.oozieTaskId = oozieTaskId;
        this.name = name;
        this.status = status;
        this.cronExp = cronExp;
        this.coordStart = coordStart;
        this.coordEnd = coordEnd;
        this.timezone = timezone;
    }

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

    public long nextIndex() {
        if (index == 9999) {
            index = 0;
            return index;
        } else {
            return index++;
        }
    }

    private void init() {
        long index = nextIndex();
        String indexStr = StringUtils.right("000000" + index, 6);
        Date date = new Date();
        this.id = DateFormatUtils.format(date, "yyyyMMddHHmmss") + indexStr;
    }

}
