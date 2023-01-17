package com.dfssi.dataplatform.datasync.service.master.clientmanagement;

import java.util.Map;

/**
 * Created by HSF on 2017/12/4.
 * 客户端度量
 */
public class ClientMetric {
    private boolean active; //可用标志位
    private String ipAddr;  //ip地址
    private String rpcPort; //rpc端口号
    private Map<String /*taskID*/,TaskStatus> taskStatusMap;//任务状态

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }

    public Map<String, TaskStatus> getTaskStatusMap() {
        return taskStatusMap;
    }

    public void setTaskStatusMap(Map<String, TaskStatus> taskStatusMap) {
        this.taskStatusMap = taskStatusMap;
    }
}
