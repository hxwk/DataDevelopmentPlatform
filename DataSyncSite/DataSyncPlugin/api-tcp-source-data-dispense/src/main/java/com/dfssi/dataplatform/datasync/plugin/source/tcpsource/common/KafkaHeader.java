package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common;

public class KafkaHeader {
    private String taskId;
    private String msgid;
    private String topic;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
