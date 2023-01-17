package com.dfssi.dataplatform.manager.monitor.task.entity;

import java.util.Date;

public class MonitorTaskExecRecordEntity extends AbstractEntity {

    private String id;
    private String taskId;
    private String status;
    private String execAppId;
    private Date createTime;
    private Date endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getExecAppId() {
        return execAppId;
    }

    public void setExecAppId(String execAppId) {
        this.execAppId = execAppId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
