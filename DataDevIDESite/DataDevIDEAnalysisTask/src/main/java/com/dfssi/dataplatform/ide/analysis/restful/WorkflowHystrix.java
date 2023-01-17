package com.dfssi.dataplatform.ide.analysis.restful;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yanghs on 2018/8/11.
 */
@Component
public class WorkflowHystrix implements IWorkflowFeign{
    @Override
    public Object killJob(String modelId) {
        return null;
    }

    @Override
    public String updateOozieTaskStatus() {
        return null;
    }

    @Override
    public Object getOozieTaskLog(String oozieId) {
        return null;
    }

    @Override
    public String explordeddeploy(Map map) {
        return null;
    }

    @Override
    public String undeploy(Map map) {
        return null;
    }

    @Override
    public String startoozie(Map map) {
        return null;
    }

    @Override
    public String startOozieCoord(Map map) {
        return null;
    }

    @Override
    public String upload(Map map) {
        return null;
    }

    @Override
    public String delect(Map map) {
        return null;
    }

    @Override
    public String updateStatus() {
        return null;
    }
}
