package com.dfssi.dataplatform.manager.monitor.task.entity;


import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.List;

@Alias("MonitorTaskEntity")
public class MonitorTaskEntity extends AbstractEntity {

    public static final String TASK_TYPE_OOZIE = "OOZIE";
    public static final String TASK_TYPE_OFFLINE = "OFFLINE";
    public static final String TASK_TYPE_STREAMING = "STREAMING";

    private String id;
    private String name;
    private String taskType;
    private String createUser;
    private String description;
    private Date createTime;
    private Date lastModifiedDate;
    private List<MonitorTaskAttrEntity> attrs;

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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<MonitorTaskAttrEntity> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<MonitorTaskAttrEntity> attrs) {
        this.attrs = attrs;
    }
}
