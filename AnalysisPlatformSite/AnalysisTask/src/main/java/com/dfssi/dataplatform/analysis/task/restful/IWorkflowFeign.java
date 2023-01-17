package com.dfssi.dataplatform.analysis.task.restful;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "ide-workflow", fallback = WorkflowHystrix.class)
public interface IWorkflowFeign {

    @RequestMapping(value = "/workflow/deployModel", method = RequestMethod.POST)
    public Object deployModel(Map map);

    @RequestMapping(value = "/workflow/undeployModel", method = RequestMethod.POST)
    public Object undeployModel(Map map);

    @RequestMapping(value = "/workflow/startModel", method = RequestMethod.POST)
    public Object startModel(Map map);

    @RequestMapping(value = "/workflow/suspendModel", method = RequestMethod.POST)
    public Object suspendModel(Map map);

    @RequestMapping(value = "/workflow/resumeModel", method = RequestMethod.POST)
    public Object resumeModel(Map map);

    @RequestMapping(value = "/workflow/killModel", method = RequestMethod.POST)
    public Object killModel(Map map);

}
