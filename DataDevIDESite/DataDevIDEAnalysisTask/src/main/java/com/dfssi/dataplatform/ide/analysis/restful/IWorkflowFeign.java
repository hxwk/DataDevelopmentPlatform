package com.dfssi.dataplatform.ide.analysis.restful;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 任务调度微服务调用
 * Created by yanghs on 2018/8/11.
 */
@FeignClient(value = "ide-workflow", fallback = WorkflowHystrix.class)
public interface IWorkflowFeign {

    @RequestMapping("/workflow/killJob/${modelId}")
    public Object killJob(String modelId);

    @RequestMapping("workflow/getJobInfo/updateStatus")
    public String updateOozieTaskStatus();

    @RequestMapping("workflow/getOozieTaskLog/${modelId}")
    public Object getOozieTaskLog(String oozieId);

    @RequestMapping("workflow/explordeddeploy")
    public String explordeddeploy(Map map);

    @RequestMapping("workflow/undeploy/${modelId}")
    public String undeploy(Map map);

    @RequestMapping("workflow/startoozie/${modelId}")
    public String startoozie(Map map);

    @RequestMapping("workflow/startOozieCoord/${modelId}")
    public String startOozieCoord(Map map);

    @RequestMapping("workflow/upload/file")
    public String upload(Map map);

    @RequestMapping("workflow/delect/file")
    public String delect(Map map);

    @RequestMapping( "workflow/getJobInfo/updateStatus")
    public String updateStatus();
}
