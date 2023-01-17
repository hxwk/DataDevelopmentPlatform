package com.dfssi.dataplatform.analysis.task.controller;

import com.dfssi.dataplatform.analysis.common.constant.Constants;
import com.dfssi.dataplatform.analysis.common.util.Exceptions;
import com.dfssi.dataplatform.analysis.common.util.ResponseUtils;
import com.dfssi.dataplatform.analysis.task.service.ModelMonitorService;
import com.dfssi.dataplatform.analysis.task.service.ModelWorkflowService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(value = "model/workflow")
@Api(tags = {"任务调度相关接口"})
@CrossOrigin
public class ModelWorkflowController extends AbstractController {

    @Autowired
    private ModelWorkflowService modelWorkflowService;
    @Autowired
    private ModelMonitorService modelMonitorService;

    @ResponseBody
    @RequestMapping(value = "deployModel/{modelId}", method = RequestMethod.POST)
    public Object deployModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Deploy model. modelId=" + modelId);
            return modelWorkflowService.deployModel(modelId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to deploy model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to deploy model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "undeployModel/{modelId}", method = RequestMethod.POST)
    public Object undeployModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Undeploy model. modelId=" + modelId);
            return modelWorkflowService.undeployModel(modelId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to undeploy model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to undeploy model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "startModel/{modelId}", method = RequestMethod.POST)
    public Object startModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Start model. modelId=" + modelId);
            if (!modelMonitorService.isMemoryEnough(modelId)) {
                return ResponseUtils.buildFailResult("Failed to start model. modelId=" + modelId + ", memory is not enough");
            }
            String oozieId = modelWorkflowService.startModel(modelId);
            modelMonitorService.updateModelStatusWhenStart(modelId, oozieId);
            return ResponseUtils.buildSuccessResult("Start model. modelId=" + modelId + " successfully.", oozieId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to start model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to start model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "suspendModel/{modelId}", method = RequestMethod.POST)
    public Object suspendModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Suspend model. modelId=" + modelId);
            return modelWorkflowService.suspendModel(modelId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to suspend model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to suspend model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "resumeModel/{modelId}", method = RequestMethod.POST)
    public Object resumeModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Resume model. modelId=" + modelId);
            return modelWorkflowService.resumeModel(modelId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to resume model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to resume model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "killModel/{modelId}", method = RequestMethod.POST)
    public Object killModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Kill model. modelId=" + modelId);
            return modelWorkflowService.killModel(modelId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to kill model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to kill model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

}
