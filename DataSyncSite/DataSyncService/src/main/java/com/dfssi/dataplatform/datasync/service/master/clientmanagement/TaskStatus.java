package com.dfssi.dataplatform.datasync.service.master.clientmanagement;

/**
 * Created by HSF on 2017/12/4.
 * 任务状态
 */
public class TaskStatus {
    private String taskId; //任务id
    private  boolean available; //是否可用
    private boolean success;  //任务提交执行状态
    private String errorMsg; //错误消息

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
