package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;

/**
 * Created by Hannibal on 2018-03-07.
 */
public class TaskInfo {

    private String taskId;

    private ChannelProcessor channelProcessor;

    private static TaskInfo instance = null;

    static {
        instance = new TaskInfo();
    }

    private TaskInfo() {

    }

    public static TaskInfo getInstance() {
        return instance;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public ChannelProcessor getChannelProcessor() {
        return channelProcessor;
    }

    public void setChannelProcessor(ChannelProcessor channelProcessor) {
        this.channelProcessor = channelProcessor;
    }
}
