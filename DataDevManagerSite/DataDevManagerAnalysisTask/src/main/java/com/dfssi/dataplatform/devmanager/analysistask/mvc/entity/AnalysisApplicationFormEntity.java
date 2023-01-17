package com.dfssi.dataplatform.devmanager.analysistask.mvc.entity;

import com.dfssi.dataplatform.devmanager.analysistask.mvc.base.BaseVO;
import org.apache.ibatis.type.Alias;

@Alias("analysisApplicationFormEntity")
public class AnalysisApplicationFormEntity extends BaseVO {

    private String id;
    private String dataresId;
    private Integer verifyStatus;
    private String applicator;
    private String applicationDate;
    private String applicationReason;
    private Integer cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private String approver;
    private String approveTime;
    private String approveOppinion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataresId() {
        return dataresId;
    }

    public void setDataresId(String dataresId) {
        this.dataresId = dataresId;
    }

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getApplicator() {
        return applicator;
    }

    public void setApplicator(String applicator) {
        this.applicator = applicator;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getApplicationReason() {
        return applicationReason;
    }

    public void setApplicationReason(String applicationReason) {
        this.applicationReason = applicationReason;
    }

    public Integer getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Integer cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveOppinion() {
        return approveOppinion;
    }

    public void setApproveOppinion(String approveOppinion) {
        this.approveOppinion = approveOppinion;
    }

    @Override
    public String toString() {
        return "AnalysisApplicationFormEntity{" +
                "id='" + id + '\'' +
                ", dataresId='" + dataresId + '\'' +
                ", verifyStatus=" + verifyStatus +
                ", applicator='" + applicator + '\'' +
                ", applicationDate='" + applicationDate + '\'' +
                ", applicationReason='" + applicationReason + '\'' +
                ", cpuUsage=" + cpuUsage +
                ", memoryUsage=" + memoryUsage +
                ", diskUsage=" + diskUsage +
                ", approver='" + approver + '\'' +
                ", approveTime='" + approveTime + '\'' +
                ", approveOppinion='" + approveOppinion + '\'' +
                '}';
    }
}
