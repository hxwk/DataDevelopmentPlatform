package com.dfssi.dataplatform.analysis.service.controller;

import com.dfssi.dataplatform.analysis.common.constant.Constants;
import com.dfssi.dataplatform.analysis.common.util.Exceptions;
import com.dfssi.dataplatform.analysis.common.util.ResponseUtils;
import com.dfssi.dataplatform.analysis.service.entity.ResourceConfEntity;
import com.dfssi.dataplatform.analysis.service.entity.ServiceModelEntity;
import com.dfssi.dataplatform.analysis.service.service.ServiceModelService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/11 16:46
 */
@Controller
@RequestMapping(value = "service")
@Api(tags = {"服务资源控制器"})
@CrossOrigin
public class ServiceModelController extends AbstractController {

    @Autowired
    private ServiceModelService serviceModelService;

    @ResponseBody
    @RequestMapping(value = "getPageMenuItems", method = RequestMethod.GET)
    @ApiOperation(value = "获取页面服务侧拉栏")
    public Object getPageMenuItems() {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "Get page left menu items.");
            return ResponseUtils.buildSuccessResult("Get page left menu items successfully.",
                    serviceModelService.getPageMenuItems());
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to get page left menu Items.\n", t);
            return ResponseUtils.buildFailResult("Failed to get page left menu Items.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getDataMenuItems", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据服务侧拉栏")
    public Object getDataMenuItems() {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "Get data left menu items.");
            return ResponseUtils.buildSuccessResult("Get data left menu items successfully.",
                    serviceModelService.getDataMenuItems());
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to get data left menu Items.\n", t);
            return ResponseUtils.buildFailResult("Failed to get data left menu Items.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "listModels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "查询任务列表")
    public Object listModels(@PathVariable("pageIdx") int pageIdx,
                             @PathVariable("pageSize") int pageSize,
                             String modelName,
                             String modelType,
                             Long startTime,
                             Long endTime,
                             String status,
                             String field,
                             String orderType) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "List models.");
            List<ServiceModelEntity> modelEntities = serviceModelService.listModels(pageIdx,
                    pageSize, modelName, modelType, startTime, endTime, status, field, orderType);
            long total = ((Page) modelEntities).getTotal();
            return ResponseUtils.buildSuccessResult(total, "List models successfully.", modelEntities);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to list models.\n", t);
            return ResponseUtils.buildFailResult("Failed to list models.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "saveModel", method = RequestMethod.POST)
    @ApiOperation(value = "保存任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelJson", value = "拼接好的任务json配置报文")
    })
    public Object saveModel(@RequestBody String modelJson) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "Save model.\n" + modelJson);
            serviceModelService.saveModel(modelJson);
            return ResponseUtils.buildSuccessResult("Save model successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to save model.\n", t);
            return ResponseUtils.buildFailResult("Failed to save model.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getServiceModel/{modelId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取任务配置信息")
    public Object getServiceModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "get service model. modelId=" + modelId + ".");
            return ResponseUtils.buildSuccessResult("Get service model. modelId=" + modelId + " successfully.",
                    serviceModelService.getServiceModel(modelId));
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to get service model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to get service model. modelId=" + modelId + " info",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "deleteServiceModel/{modelId}", method = RequestMethod.POST)
    @ApiOperation(value = "从任务列表中删除任务")
    public Object deleteServiceModel(@PathVariable("modelId") String modelId) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "Delete service model. modelId=" + modelId);
            serviceModelService.deleteServiceModel(modelId);
            return ResponseUtils.buildSuccessResult("Delete service model. modelId=" + modelId + " successfully.");
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to delete service model. modelId=" + modelId + "\n", t);
            return ResponseUtils.buildFailResult("Failed to delete service model. modelId=" + modelId,
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "generateNewId", method = RequestMethod.GET)
    @ApiOperation(value = "生成任务相关Id")
    public Object generateNewId() {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "Generate new id.");
            return ResponseUtils.buildSuccessResult("Generate new id successfully.",
                    serviceModelService.generateNewId());
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to generate new id.\n", t);
            return ResponseUtils.buildFailResult("Failed to generate new id.",
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ApiOperation(value = "获取某条数据资源信息")
    @RequestMapping(value = "getResourceConf", method = {RequestMethod.GET})
    @ResponseBody
    public Object getResourceConf(String resourceId){
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE + "Get resource conf.");
            return ResponseUtils.buildSuccessResult("Get resource conf successfully.",
                    serviceModelService.getResourceConf(resourceId));
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to get resource conf.\n", t);
            return ResponseUtils.buildFailResult("Failed to get resource conf.",
                    Exceptions.getStackTraceAsString(t));
        }
    }
}
