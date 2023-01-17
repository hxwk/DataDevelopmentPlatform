package com.dfssi.dataplatform.datasync.plugin.sqlsource.common;

public class KafkaHeader {
    private String taskId;
    private String topic;
    private String sourceType; //JDBC,Other

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
