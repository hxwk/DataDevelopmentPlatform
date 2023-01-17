package com.dfssi.dataplatform.ide.service.web;

import com.dfssi.dataplatform.analysis.entity.AnalysisModelEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisResourceEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisStepTypeEntity;
import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.common.controller.ResponseObj;
import com.dfssi.dataplatform.ide.service.restful.IAnalysisModelFeign;
import com.dfssi.dataplatform.ide.service.service.DataServiceLeftMenuItems;
import com.dfssi.dataplatform.ide.service.service.PageServiceLeftMenuItems;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "service")
public class ServiceModelController extends AbstractServiceIDEController {
    public static final String MODEL_TYPE_SERVICE_PAGE = "SERVICE_PAGE";
    public static final String MODEL_TYPE_SERVICE_DATA = "SERVICE_DATA";
    public static final String STEP_TYPE_PAGE = "PAGE";
    public static final String STEP_TYPE_DATA = "DATA";

    @Autowired
    private IAnalysisModelFeign iAnalysisModelFeign;
//    private final static String LOG_TAG_MODEL_SERVICE = "[Service Model IDE]";
//    @Autowired
//    private ServiceModelService serviceModelService;
//
//    @ResponseBody
//    @RequestMapping(value = "page/leftitems", method = RequestMethod.GET)
//    public Object getPageMenuItems(HttpServletRequest req, HttpServletResponse response) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Get page left menu items.");
//
//            responseObj.setData(this.serviceModelService.getPageLeftMenus());
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get page left menu items.", Exceptions
//                    .getStackTraceAsString(t));
//
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get page left menu Items.\n", t);
//        }
//
//        return responseObj;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "data/leftitems", method = RequestMethod.GET)
//    public Object getDataMenuItems(HttpServletRequest req, HttpServletResponse response) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Get data left menu items.");
//
//            responseObj.setData(this.serviceModelService.getDataLeftMenus());
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get data left menu items.", Exceptions
//                    .getStackTraceAsString(t));
//
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get data left menu Items.\n", t);
//        }
//
//        return responseObj;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "page/listmodels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
//    public Object listAllPageModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx")
//            int pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime,
//                                    String status) {
//        ResponseObj responseObj = null;
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "List all page models.");
//
//            responseObj = this.listAllModels(pageIdx, pageSize, ServiceModelService.MODEL_TYPE_SERVICE_PAGE,
//                    modelName, startTime, endTime, status);
//        } catch (Throwable t) {
//            responseObj = ResponseObj.createResponseObj();
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all page models.", Exceptions
//                    .getStackTraceAsString(t));
//
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to list all page models.\n", t);
//        }
//
//        return responseObj;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "data/listmodels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
//    public Object listAllDataModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx")
//            int pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime,
//                                    String status) {
//        ResponseObj responseObj = null;
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "List all data models.");
//
//            responseObj = this.listAllModels(pageIdx, pageSize, ServiceModelService.MODEL_TYPE_SERVICE_DATA,
//                    modelName, startTime, endTime, status);
//        } catch (Throwable t) {
//            responseObj = ResponseObj.createResponseObj();
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all data models.", Exceptions
//                    .getStackTraceAsString(t));
//
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to list all data models.\n", t);
//        }
//
//        return responseObj;
//    }
//
//    private ResponseObj listAllModels(int pageIdx, int pageSize, String modeType, String modelName, Long startTime,
//                                      Long endTime, String status) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        List<AnalysisModelEntity> modelEntities = this.serviceModelService.listAllModels(pageIdx, pageSize, modeType,
//                modelName, startTime, endTime, status);
//        long total = ((Page) modelEntities).getTotal();
//        responseObj.setTotal(total);
//        responseObj.setData(modelEntities);
//        responseObj.setSuccessCode();
//        responseObj.setMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
//
//        return responseObj;
//    }

    private final static String LOG_TAG_MODEL_SERVICE = "[Service Model IDE]";

    @ResponseBody
    @RequestMapping(value = "page/leftitems", method = RequestMethod.GET)
    public Object getPageMenuItems(HttpServletRequest req, HttpServletResponse response) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "Get page left menu items.");

            responseObj.setData(this.getPageLeftMenus());
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get page left menu items.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get page left menu Items.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "data/leftitems", method = RequestMethod.GET)
    public Object getDataMenuItems(HttpServletRequest req, HttpServletResponse response) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "Get data left menu items.");

            responseObj.setData(this.getDataLeftMenus());
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get data left menu items.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get data left menu Items.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "page/listmodels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
    public Object listAllPageModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx")
            int pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime,
                                    String status, String field, String orderType) {
        ResponseObj responseObj = null;
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "List all page models.");

            responseObj = this.listAllModels(pageIdx, pageSize, MODEL_TYPE_SERVICE_PAGE,
                    modelName, startTime, endTime, status, field, orderType);
        } catch (Throwable t) {
            responseObj = ResponseObj.createResponseObj();
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all page models.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to list all page models.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "data/listmodels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
    public Object listAllDataModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx")
            int pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime,
                                    String status, String field, String orderType) {
        ResponseObj responseObj = null;
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "List all data models.");

            responseObj = this.listAllModels(pageIdx, pageSize, MODEL_TYPE_SERVICE_DATA,
                    modelName, startTime, endTime, status, field, orderType);
        } catch (Throwable t) {
            responseObj = ResponseObj.createResponseObj();
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all data models.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to list all data models.\n", t);
        }

        return responseObj;
    }

    //保存配置
    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "拼接好的任务json配置报文")
    })
    public Object saveModel(HttpServletRequest req, HttpServletResponse response, Model model, @RequestBody String
            request) {
        System.out.println("request:"+request);
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "Save model.\n" + request);

            com.dfssi.dataplatform.analysis.model.Model analysisModel = iAnalysisModelFeign.saveModel(com.dfssi
                    .dataplatform.analysis.model.Model.buildFromJson(request));
            responseObj.buildSuccessMsg("Save model successfully.");
            responseObj.addKeyVal("modelId", analysisModel.getId());
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to save model.", Exceptions.getStackTraceAsString
                    (t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to save model.\n", t);
        }

        return responseObj;
    }

    //从任务列表中删除任务
    @ResponseBody
    @RequestMapping(value = "delete/{modelId}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "任务配置的modelId", dataType = "String", paramType = "path")
    })
    public Object deleteModel(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "Delete model. modelId=" + modelId);

            iAnalysisModelFeign.deleteModel(modelId);
            responseObj.buildSuccessMsg("Delete mode successfully.");
            responseObj.addKeyVal("modelId", modelId);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to delete model. modelId=" + modelId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to delete model. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    //获取任务配置
    @ResponseBody
    @RequestMapping(value = "get/{modelId}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "任务配置的modelId", dataType = "String", paramType = "path")
    })
    public Object getModel(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "Get model. modelId=" + modelId);

            com.dfssi.dataplatform.analysis.model.Model analysisModel = iAnalysisModelFeign.getModel(modelId);
            if (analysisModel == null) {
                responseObj.buildFailMsg(ResponseObj.CODE_CONTENT_NOT_EXIST, "Model does not exists.", null);
            } else {
                responseObj.buildSuccessMsg("Get model successfully.");
                responseObj.setData(analysisModel);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get model. modelId=" + modelId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get model. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getnewid", method = RequestMethod.GET)
    public Object getNewId(HttpServletRequest req, HttpServletResponse response, Model model) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "Get step id.");
            String newId = com.dfssi.dataplatform.analysis.model.Model.generateId();

            responseObj.buildSuccessMsg("Generate ID successfully.");
            responseObj.addKeyVal("newId", newId);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to generate ID." + "\n", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to generate ID." + "\n", t);
        }

        return responseObj;
    }

    private ResponseObj listAllModels(int pageIdx, int pageSize, String modeType, String modelName, Long startTime,
                                      Long endTime, String status, String field, String orderType) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        List<AnalysisModelEntity> modelEntities = this.iAnalysisModelFeign.listAllModels(pageIdx, pageSize, modeType,
                modelName, startTime, endTime, status, field, orderType);
        long total = ((Page) modelEntities).getTotal();
        responseObj.setTotal(total);
        responseObj.setData(modelEntities);
        responseObj.setSuccessCode();
        responseObj.setMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);

        return responseObj;
    }

    public PageServiceLeftMenuItems getPageLeftMenus() {
        PageServiceLeftMenuItems leftMenuItems = new PageServiceLeftMenuItems();

        List<AnalysisResourceEntity> dataResources = iAnalysisModelFeign.listAllSources();
        for (AnalysisResourceEntity drce : dataResources) {
            leftMenuItems.addDataResourceItem(drce);
        }

        List<AnalysisStepTypeEntity> list = iAnalysisModelFeign.getAllStepTypes(STEP_TYPE_PAGE);
        for (AnalysisStepTypeEntity aste : list) {
            if ("bicomp".equalsIgnoreCase(aste.getType())) {
                leftMenuItems.addBICompItem(aste);
            }
        }

        return leftMenuItems;
    }

    public DataServiceLeftMenuItems getDataLeftMenus() {
        DataServiceLeftMenuItems leftMenuItems = new DataServiceLeftMenuItems();

        List<DataResourceConfEntity> dataResources = iAnalysisModelFeign.getAllDataResource();
        for (DataResourceConfEntity drce : dataResources) {
            leftMenuItems.addDataResourceItem(drce);
        }

        List<AnalysisStepTypeEntity> list = iAnalysisModelFeign.getAllStepTypes(STEP_TYPE_DATA);
        for (AnalysisStepTypeEntity aste : list) {
            if ("share".equalsIgnoreCase(aste.getType())) {
                leftMenuItems.addShareItem(aste);
            }
        }

        return leftMenuItems;
    }

    public List<AnalysisResourceEntity> getResourceConfs() {
        PageServiceLeftMenuItems leftMenuItems = new PageServiceLeftMenuItems();

        List<AnalysisResourceEntity> dataResources = iAnalysisModelFeign.listAllSources();
        for (AnalysisResourceEntity drce : dataResources) {
            leftMenuItems.addDataResourceItem(drce);
        }

        List<AnalysisStepTypeEntity> list = iAnalysisModelFeign.getAllStepTypes(STEP_TYPE_PAGE);
        for (AnalysisStepTypeEntity aste : list) {
            if ("bicomp".equalsIgnoreCase(aste.getType())) {
                leftMenuItems.addBICompItem(aste);
            }
        }

        return leftMenuItems;
    }
}
