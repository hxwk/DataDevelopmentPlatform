package com.dfssi.dataplatform.datasync.common.platform.entity;

import java.util.List;

/**
 * Created by HSF on 2017/12/4.
 * 任务定义
 */

public class TaskEntity {
    private String taskId; //任务id
    private String clientId; //客户端id
    private TaskType type; //任务类型
    private TaskAction action; //任务动作
    private String taskType; //任务类型名称
    private String taskAction; //任务动作名称
    private String channelType; //通道类型，memChannel or kafkaChannel
    private TaskDataSource taskDataSource; //数据源
    private List<TaskDataDestination> taskDataDestinations; //数据目的地
    private List<TaskDataCleanTransform> taskDataCleanTransforms; //清洗转换

      public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public TaskAction getAction() {
        return action;
    }

    public void setAction(TaskAction action) {
        this.action = action;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.type = TaskType.valueOf(taskType.toUpperCase());
        this.taskType = taskType;
    }

    public String getTaskAction() {
        return taskAction;
    }

    public void setTaskAction(String taskAction) {
        this.action = TaskAction.valueOf(taskAction.toUpperCase());
        this.taskAction = taskAction;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public TaskDataSource getTaskDataSource() {
        return taskDataSource;
    }

    public void setTaskDataSource(TaskDataSource taskDataSource) {
        this.taskDataSource = taskDataSource;
    }

    public List<TaskDataDestination> getTaskDataDestinations() {
        return taskDataDestinations;
    }

    public void setTaskDataDestinations(List<TaskDataDestination> taskDataDestinations) {
        this.taskDataDestinations = taskDataDestinations;
    }

    public List<TaskDataCleanTransform> getTaskDataCleanTransforms() {
        return taskDataCleanTransforms;
    }

    public void setTaskDataCleanTransforms(List<TaskDataCleanTransform> taskDataCleanTransforms) {
        this.taskDataCleanTransforms = taskDataCleanTransforms;
    }
}
