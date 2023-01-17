package com.dfssi.dataplatform.analysis.task.restful;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowHystrix implements IWorkflowFeign{

    @Override
    public Object deployModel(Map map) {
        return new Exception("获取任务调度微服务调用失败，请检查微服务是否开启");
    }

    @Override
    public Object undeployModel(Map map) {
        return new Exception("获取任务调度微服务调用失败，请检查微服务是否开启");
    }

    @Override
    public Object startModel(Map map) {
        return new Exception("获取任务调度微服务调用失败，请检查微服务是否开启");
    }

    @Override
    public Object suspendModel(Map map) {
        return new Exception("获取任务调度微服务调用失败，请检查微服务是否开启");
    }

    @Override
    public Object resumeModel(Map map) {
        return new Exception("获取任务调度微服务调用失败，请检查微服务是否开启");
    }

    @Override
    public Object killModel(Map map) {
        return new Exception("获取任务调度微服务调用失败，请检查微服务是否开启");
    }
}
