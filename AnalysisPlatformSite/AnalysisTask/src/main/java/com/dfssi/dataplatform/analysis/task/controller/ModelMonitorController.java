package com.dfssi.dataplatform.analysis.task.controller;

import com.dfssi.dataplatform.analysis.common.constant.Constants;
import com.dfssi.dataplatform.analysis.common.util.Exceptions;
import com.dfssi.dataplatform.analysis.common.util.ResponseUtils;
import com.dfssi.dataplatform.analysis.task.service.ModelMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "model/monitor")
@Api(tags = {"任务监控相关接口"})
@CrossOrigin
public class ModelMonitorController extends AbstractController {

    @Autowired
    private ModelMonitorService modelMonitorService;

    @ResponseBody
    @RequestMapping(value = "updateModelStatus", method = RequestMethod.POST)
    @ApiOperation(value = "更新任务运行状态")
    public Object updateModelStatus() {
        try {
            logger.info(Constants.LOG_TAG_MODEL_ANALYSIS + "Update model status.");
            modelMonitorService.updateModelStatus();
            return ResponseUtils.buildSuccessResult("Update model status successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_ANALYSIS + "Failed to update model status.\n", t);
            return ResponseUtils.buildFailResult("Failed to update model status.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getOozieTaskInfo/{modelId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取oozie任务运行信息")
    public Object getOozieTaskInfo(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_ANALYSIS + "Get oozie task info.");
            modelMonitorService.updateModelStatus();
            return ResponseUtils.buildSuccessResult("Get oozie task info successfully.",
                    modelMonitorService.getOozieTaskInfo(modelId));
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_ANALYSIS + "Failed to get oozie task info.\n", t);
            return ResponseUtils.buildFailResult("Failed to get oozie task info.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getYarnTaskInfo/{oozieId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取yarn任务运行信息")
    public Object getYarnTaskInfo(@PathVariable("oozieId") String oozieId) throws Exception {
        try {
            logger.info(Constants.LOG_TAG_MODEL_ANALYSIS + "Get yarn task info.");
            modelMonitorService.updateModelStatus();
            return ResponseUtils.buildSuccessResult("Get yarn task info successfully.",
                    modelMonitorService.getYarnTaskInfo(oozieId));
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_ANALYSIS + "Failed to get yarn task info.\n", t);
            return ResponseUtils.buildFailResult("Failed to get yarn task info.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getOozieTaskLog/{oozieId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取任务日志")
    public Object getOozieTaskLog(@PathVariable("oozieId") String oozieId) throws Exception {
        try {
            logger.info(Constants.LOG_TAG_MODEL_ANALYSIS + "Get oozie task log.");
            modelMonitorService.updateModelStatus();
            return ResponseUtils.buildSuccessResult("Get oozie task log successfully.",
                    modelMonitorService.getOozieTaskLog(oozieId));
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_ANALYSIS + "Failed to get oozie task log.\n", t);
            return ResponseUtils.buildFailResult("Failed to get oozie task log.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "saveEmailAlertConfig", method = RequestMethod.GET)
    @ApiOperation(value = "保存任务邮件告警配置信息")
    public Object saveEmailAlertConfig(@RequestParam("modelId") String modelId,
                                       @RequestParam("userIds") String userIds,
                                       @RequestParam("ruleIds") String ruleIds) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_ANALYSIS + "Save email alert config.");
            modelMonitorService.saveEmailAlertConfig(modelId, userIds, ruleIds);
            return ResponseUtils.buildSuccessResult("Save email alert config successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_ANALYSIS + "Failed to save email alert config.\n", t);
            return ResponseUtils.buildFailResult("Failed to save email alert config.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getEmailAlertRecord/{modelId}", method = RequestMethod.GET)
    @ApiOperation(value = "查看任务邮件告警记录")
    public Object getEmailAlertRecord(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_ANALYSIS + "Save email alert record.");
            return ResponseUtils.buildSuccessResult("Save email alert record successfully.",
                    modelMonitorService.getEmailAlertRecord(modelId));
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_ANALYSIS + "Failed to save email alert record.\n", t);
            return ResponseUtils.buildFailResult("Failed to save email alert record.",
                    Exceptions.getStackTraceAsString(t));
        }
    }
}
