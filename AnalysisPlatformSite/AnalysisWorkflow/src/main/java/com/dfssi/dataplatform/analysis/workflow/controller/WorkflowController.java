package com.dfssi.dataplatform.analysis.workflow.controller;

import com.dfssi.dataplatform.analysis.common.constant.Constants;
import com.dfssi.dataplatform.analysis.common.util.Exceptions;
import com.dfssi.dataplatform.analysis.common.util.ResponseUtils;
import com.dfssi.dataplatform.analysis.workflow.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(value = "/workflow")
@Slf4j
public class WorkflowController extends AbstractController {

    @Autowired
    private WorkflowService workflowService;

    @ResponseBody
    @RequestMapping(value = "deployModel", method = RequestMethod.POST)
    public Object deployModel(@RequestBody Map<String, String> map) {
        String modelId = map.get("modelId");
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Deploy model. modelId=" + modelId);
            workflowService.deployModel(modelId);
            return ResponseUtils.buildSuccessResult("Deploy model. modelId=" + modelId + " successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to deploy model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to deploy model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "undeployModel", method = RequestMethod.POST)
    public Object undeployModel(@RequestBody Map<String, String> map) {
        String modelId = map.get("modelId");
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Undeploy model. modelId=" + modelId);
            workflowService.undeployModel(modelId);
            return ResponseUtils.buildSuccessResult("Undeploy model. modelId=" + modelId + " successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to undeploy model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to undeploy model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "startModel", method = RequestMethod.POST)
    public Object startModel(@RequestBody Map<String, String> map) {
        String modelId = map.get("modelId");
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Start model. modelId=" + modelId);
            String oozieId = workflowService.startModel(modelId);
            if (oozieId.contains("fail")) {
                return ResponseUtils.buildFailResult("Failed to start model. modelId=" + modelId);
            }
            return ResponseUtils.buildSuccessResult("Start model. modelId=" + modelId + " successfully.", oozieId);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to start model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to start model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "suspendModel", method = RequestMethod.POST)
    public Object suspendModel(@RequestBody Map<String, String> map) {
        String modelId = map.get("modelId");
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Suspend model. modelId=" + modelId);
            workflowService.suspendModel(modelId);
            return ResponseUtils.buildSuccessResult("Suspend model. modelId=" + modelId + " successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to suspend model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to suspend model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "resumeModel", method = RequestMethod.POST)
    public Object resumeModel(@RequestBody Map<String, String> map) {
        String modelId = map.get("modelId");
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Resume model. modelId=" + modelId);
            workflowService.resumeModel(modelId);
            return ResponseUtils.buildSuccessResult("Resume model. modelId=" + modelId + " successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to resume model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to resume model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "killModel", method = RequestMethod.POST)
    public Object killModel(@RequestBody Map<String, String> map) {
        String modelId = map.get("modelId");
        try {
            logger.info(Constants.LOG_TAG_WORKFLOW + "Kill model. modelId=" + modelId);
            workflowService.killModel(modelId);
            return ResponseUtils.buildSuccessResult("Kill model. modelId=" + modelId + " successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_WORKFLOW + "Failed to kill model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to kill model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }
}
