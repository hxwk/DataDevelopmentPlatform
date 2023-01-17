package com.dfssi.dataplatform.ide.analysis.web;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.common.controller.ResponseObj;
import com.dfssi.dataplatform.ide.analysis.resource.DBConnectEntity;
import com.dfssi.dataplatform.ide.analysis.resource.DataResource;
import com.dfssi.dataplatform.ide.analysis.restful.IAnalysisModelFeign;
import com.dfssi.dataplatform.ide.analysis.restful.IWorkflowFeign;
import com.dfssi.dataplatform.ide.analysis.service.AnalysisLeftMenuItems;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping(value = "task/analysis")
@Api(tags = {"任务调度相关接口"})
public class AnalysisModelController extends AbstractAnalysisIDEController {

    private final static String LOG_TAG_MODEL_ANALYSIS = "[Analysis Model IDE]";

    public static final String MODEL_TYPE_ANALYSIS = "ANALYSIS";

    public static final String STEP_TYPE_ANALYSIS = "ANALYSIS";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAnalysisModelFeign iAnalysisModelFeign;

    @Autowired
    private IWorkflowFeign iWorkflowFeign;


    @ResponseBody
    @RequestMapping(value = "leftitems", method = RequestMethod.GET)
    @ApiOperation(value = "获取左边数据源、预处理、算法、输出模块")
    public Object getMenuItems(HttpServletRequest req, HttpServletResponse response) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Get left menu items.");

            responseObj.setData(this.getLeftMenus());
            responseObj.buildSuccessMsg("Get left menu items successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get left menu items.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to get menu items.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "listmodels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "查询任务列表")
    public Object listAllModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx") int
            pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime, String
                                        status, String field, String orderType) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "List all models.");
            List<AnalysisModelEntity> modelEntities = this.listAllAnalysisModels(pageIdx,
                    pageSize, modelName, startTime, endTime, status, field, orderType);
            long total = ((Page) modelEntities).getTotal();
            responseObj.setTotal(total);
            responseObj.setData(modelEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all models.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to list all models.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ApiOperation(value = "保存任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "拼接好的任务json配置报文")
    })
    public Object saveModel(HttpServletRequest req, HttpServletResponse response, Model model, @RequestBody String
            request) {
        System.out.println("request:"+request);
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Save model.\n" + request);

            com.dfssi.dataplatform.analysis.model.Model analysisModel = iAnalysisModelFeign.saveModel(com.dfssi
                    .dataplatform.analysis.model.Model.buildFromJson(request));
            responseObj.buildSuccessMsg("Save model successfully.");
            responseObj.addKeyVal("modelId", analysisModel.getId());
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to save model.", Exceptions.getStackTraceAsString
                    (t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to save model.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "delete/{modelId}", method = RequestMethod.POST)
    @ApiOperation(value = "从任务列表中删除任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "任务配置的modelId", dataType = "String", paramType = "path")
    })
    public Object deleteModel(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Delete model. modelId=" + modelId);

            this.delectPreviewData(modelId);//删除该任务预览数据
            this.undeployModel(modelId);//删除任务相关xml文件
            iAnalysisModelFeign.delectMonitorInfo(modelId);//删除任务相关监控信息
            iAnalysisModelFeign.delectEmailAlertInfo(modelId);//删除任务邮件告警信息
            iAnalysisModelFeign.deleteModel(modelId);
            responseObj.buildSuccessMsg("Delete mode successfully.");
            responseObj.addKeyVal("modelId", modelId);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to delete model. modelId=" + modelId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to delete model. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/{modelId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取任务配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "任务配置的modelId", dataType = "String", paramType = "path")
    })
    public Object getModel(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Get model. modelId=" + modelId);

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

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to get model. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getnewid", method = RequestMethod.GET)
    @ApiOperation(value = "生成新Id")
    public Object getStepId(HttpServletRequest req, HttpServletResponse response, Model model) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Get step id.");
            String newId = com.dfssi.dataplatform.analysis.model.Model.generateId();

            responseObj.buildSuccessMsg("Generate ID successfully.");
            responseObj.addKeyVal("newId", newId);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to generate ID." + "\n", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to generate ID." + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "start/{modelId}", method = RequestMethod.GET)
    @ApiOperation(value = "启动任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "任务配置的modelId", dataType = "String", paramType = "path")
    })
    public Object startTask(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("modelId", modelId);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Startup task. modelId=" + modelId);
            this.startTask(modelId);
            responseObj.buildSuccessMsg("Startup task successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to startup task. modelId=" + modelId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to startup task. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "killJob/{modelId}", method = RequestMethod.GET)
    @ApiOperation(value = "停止任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "oozie的任务id", dataType = "String", paramType = "path")
    })
    public Object killJob(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("modelId", modelId);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Kill task. modelId=" + modelId);

            iWorkflowFeign.killJob(modelId);
            responseObj.buildSuccessMsg("Kill task successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to kill task. modelId=" + modelId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to kill task. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/dataPreview", method = RequestMethod.GET)
    @ApiOperation(value = "数据预览通用接口，获取指定数据库n条指定列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colNames", value = "列名，用；号隔开，查询所有列用*号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "number", value = "获取数据条数", dataType = "int", paramType = "query")
    })
    public Object dataPreview(String dataresourceType,
                              String ip,
                              String port,
                              String databaseUsername,
                              String databasePassword,
                              String databaseName,
                              String tableName,
                              String colNames,
                              int number) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            List<LinkedHashMap<String, Object>> data = this.dataPreviewPro(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colNames, number);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Query special column data of different database successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query special column data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query special column data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to Query special column data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/getTableColumnAndTypes", method = RequestMethod.GET)
    @ApiOperation(value = "数据预览通用接口，获取指定数据库字段名及类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
    })
    public Object getTableColumnAndTypes(String dataresourceType,
                              String ip,
                              String port,
                              String databaseUsername,
                              String databasePassword,
                              String databaseName,
                              String tableName) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            Map<String, String> data = this.getTableColumnAndTypesPro(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Get table columns and types successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/getCategoryValue", method = RequestMethod.GET)
    @ApiOperation(value = "类别列数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colName", value = "类别列列名", dataType = "String", paramType = "query"),

    })
    public Object getCategoryValue(String dataresourceType,
                                   String ip,
                                   String port,
                                   String databaseUsername,
                                   String databasePassword,
                                   String databaseName,
                                   String tableName,
                                   String colName) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            Set<Object> data = this.getCategoryValuePro(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colName);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Get table columns and types successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/SourceConf", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据资源配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataresourceId", value = "数据资源Id", dataType = "String", paramType = "query")
    })
    public Object getSourceConf(HttpServletRequest req, HttpServletResponse response, String dataresourceId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceId", dataresourceId);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "数据资源 dataresourceId=" + dataresourceId);
            Map<String, String> data = this.getSourceConf(dataresourceId);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Query source conf successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query source conf dataresourceId=" + dataresourceId, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query source conf dataresourceId=" + dataresourceId,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to Query source conf dataresourceId=" + dataresourceId, t);
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "save/Resource", method = RequestMethod.POST)
    @ApiOperation(value = "输出源注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "拼接好的任务json配置报文")
    })
    public Object saveResource(HttpServletRequest req, HttpServletResponse response, Model model, @RequestBody String
            request) {
        System.out.println("request:"+request);
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Save model.\n" + request);

            com.dfssi.dataplatform.analysis.model.Model analysisModel = iAnalysisModelFeign.saveResource(com.dfssi
                    .dataplatform.analysis.model.Model.buildFromJson(request));
            responseObj.buildSuccessMsg("Save resource successfully.");
            responseObj.addKeyVal("modelId", analysisModel.getId());
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to save resource.", Exceptions.getStackTraceAsString
                    (t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to save resource.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "upload/file", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件到Hdfs")
    public Object uploadFileToHdfs(HttpServletRequest req, HttpServletResponse response,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("description") String description) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        logger.info(LOG_TAG_MODEL_ANALYSIS + "文件上传.\n");
        if (!file.isEmpty()) {
            String jarPath = this.uploadFileToHdfs(file);
            if (null != jarPath) {
                iAnalysisModelFeign.saveJar(jarPath, description, file.getSize());
                responseObj.buildSuccessMsg("Upload file successfully.");
            } else {
                responseObj.buildSuccessMsg("Failed to upload file.");
            }
        } else {
            responseObj.buildSuccessMsg("Failed to upload file.");
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "delect/file", method = RequestMethod.GET)
    @ApiOperation(value = "已上传文件删除")
    public Object delectFileFromHdfs(HttpServletRequest req, HttpServletResponse response,
                          @RequestParam("filePath") String filePath) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        logger.info(LOG_TAG_MODEL_ANALYSIS + "已上传jar包删除.\n");
        if (!filePath.isEmpty()) {
            responseObj.buildSuccessMsg(this.delectFileFromHdfs(filePath));
            iAnalysisModelFeign.delectJar(filePath);
            responseObj.buildSuccessMsg(filePath);
        } else {
            responseObj.buildSuccessMsg("Failed to upload file.");
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getFileInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取已上传文件信息")
    public Object getFileInfo(HttpServletRequest req,
                              HttpServletResponse response) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "获取已上传文件信息.\n");
            List<AnalysisJarEntity> jarEntities = iAnalysisModelFeign.getFileInfo();

            long total = jarEntities.size();
            responseObj.setTotal(total);
            responseObj.setData(jarEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all jars.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to list all jars.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getOozieTaskInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取oozie任务运行信息")
    public Object getOozieTaskInfo(HttpServletRequest req, HttpServletResponse response, String modelId, Boolean b) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "获取任务运行信息.\n");
            this.updateOozieTaskStatus();
            Object object = iAnalysisModelFeign.getOozieTaskInfo(modelId);
            responseObj.setData(object);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get task info.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to get task info.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getYarnTaskInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取yarn任务运行信息")
    public Object getYarnTaskInfo(HttpServletRequest req, HttpServletResponse response, String oozieId, Boolean b) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "获取任务运行信息.\n");
            this.updateOozieTaskStatus();
            Object object = iAnalysisModelFeign.getYarnTaskInfo(oozieId);
            responseObj.setData(object);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get task info.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to get task info.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getOozieTaskLog", method = RequestMethod.GET)
    @ApiOperation(value = "获取任务日志")
    public Object getOozieTaskLog(HttpServletRequest req, HttpServletResponse response, String oozieId) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "获取任务运行信息.\n");
            this.updateOozieTaskStatus();
            Object object = this.getOozieTaskLog(oozieId);
            responseObj.setData(object);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all jars.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to list all jars.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "getJobInfo/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "更新任务运行状态")
    public Object updateJobStatus(HttpServletRequest req, HttpServletResponse response) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String s = iWorkflowFeign.updateOozieTaskStatus();
            responseObj.buildSuccessMsg("Update job oozie task status successfully !");
        } catch (Throwable t) {
            logger.error("Failed to update job oozie task status", t);
            responseObj.buildFailMsg(1,"Failed to update job oozie task status", Exceptions.getStackTraceAsString(t));
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "save/emailAlertConfig", method = RequestMethod.GET)
    @ApiOperation(value = "保存任务邮件告警配置信息")
    public Object saveEmailAlertConfig(HttpServletRequest req, HttpServletResponse response,
                                       @RequestParam("modelId") String modelId,
                                       @RequestParam("userIds") String userIds,
                                       @RequestParam("ruleIds") String ruleIds) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            this.iAnalysisModelFeign.saveEmailAlertConfig(modelId, userIds, ruleIds);
            responseObj.buildSuccessMsg("Save email alert config successfully !");
        } catch (Throwable t) {
            logger.error("Failed to save email alert config", t);
            responseObj.buildFailMsg(1,"Failed to save email alert config", Exceptions.getStackTraceAsString(t));
        }
        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/emailAlertRecord", method = RequestMethod.GET)
    @ApiOperation(value = "查看任务邮件告警记录")
    public Object getEmailAlertRecord(HttpServletRequest req, HttpServletResponse response,
                                       @RequestParam("modelId") String modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            Object object = this.iAnalysisModelFeign.getEmailAlertRecord(modelId);
            responseObj.setData(object);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            logger.error("Failed to save email alert config", t);
            responseObj.buildFailMsg(1,"Failed to save email alert config", Exceptions.getStackTraceAsString(t));
        }
        return responseObj;
    }

    public AnalysisLeftMenuItems getLeftMenus() {
        AnalysisLeftMenuItems leftMenuItems = new AnalysisLeftMenuItems();
        Map<String, Object> params = new HashMap();
        params.put("is_valid", 1);
        List<AnalysisSourceEntity> dataSources = iAnalysisModelFeign.listAllSources(params);
        for (AnalysisSourceEntity drce : dataSources) {
            leftMenuItems.addDataSourceItem(drce);
        }

        List<AnalysisStepTypeEntity> list = iAnalysisModelFeign.getAllStepTypes(STEP_TYPE_ANALYSIS);
        for (AnalysisStepTypeEntity aste : list) {
            if ("Preprocess".equalsIgnoreCase(aste.getType())) {
                leftMenuItems.addPreprocessItem(aste);
            } else if ("Algorithm".equalsIgnoreCase(aste.getType())) {
                leftMenuItems.addAlgorithmItem(aste);
            } else if ("Output".equalsIgnoreCase(aste.getType())) {
                leftMenuItems.addOutputItem(aste);
            }
        }

        return leftMenuItems;
    }

    public List<AnalysisModelEntity> listAllAnalysisModels(int pageIdx, int pageSize, String modelName, Long
            startTime, Long endTime, String status, String field, String orderType) {
        return iAnalysisModelFeign.listAllModels(pageIdx, pageSize, MODEL_TYPE_ANALYSIS, modelName, startTime, endTime, status, field, orderType);
    }

    public String deployModel(String modelId) throws IOException {
        com.dfssi.dataplatform.analysis.model.Model model = iAnalysisModelFeign.getModel(modelId);
        Map<String, Object> params = new HashMap();
        params.put("request", objectMapper.writeValueAsString(model));

        return iWorkflowFeign.explordeddeploy(params);
    }

    public String undeployModel(String modelId) throws IOException {
        Map<String, Object> params = new HashMap();
        params.put("request", objectMapper.writeValueAsString(modelId));

        return iWorkflowFeign.undeploy(params);
    }

    public String startTask(String modelId) throws IOException {
//        iAnalysisModelFeign.getModelByModelId(modelId).getStatus();//无用代码？
        this.deployModel(modelId);
        Map<String, Object> params = new HashMap();
        if (iAnalysisModelFeign.isWorkflowTask(modelId)) {
            return iWorkflowFeign.startoozie(params);
        }
        return iWorkflowFeign.startOozieCoord(params);
    }

    public Object killJob(String modelId) throws IOException {
        return iWorkflowFeign.killJob(modelId);
    }

    public String updateOozieTaskStatus() throws IOException {
        return iWorkflowFeign.updateStatus();
    }

    public Object getOozieTaskLog(String oozieId) throws IOException {
        return iWorkflowFeign.getOozieTaskLog(oozieId);
    }

    //预览数据删除
    public void delectPreviewData(String modelId) throws ClassNotFoundException {
        List<AnalysisStepEntity> steps = iAnalysisModelFeign.getStepsByModelId(modelId);
        DBConnectEntity dbConnectEntity = new DBConnectEntity("hive", "", "",
                "dev_analysis", "", "");
        DataResource dataResource = new DataResource(dbConnectEntity);
        for (AnalysisStepEntity step : steps) {
            if (step.getBuildType().equals("Preprocess") || step.getBuildType().equals("Algorithm")) {
                //对应任务预览数据在任务运行过程中以modelId_stepId为表名存在dev_analysis库中
                String tableName = modelId + "_" + step.getId();
                dataResource.delectTable(tableName);
            }
        }
    }

    //文件上传
    public String uploadFileToHdfs(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename.equalsIgnoreCase("pom.xml")) {
            originalFilename = "upload_" + originalFilename;
        }

        File uploadFile = new File(originalFilename);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uploadFile));

        out.write(file.getBytes());
        out.flush();
        out.close();

        Map<String, Object> params = new HashMap();
        params.put("request", uploadFile.getCanonicalPath());

        String jarPath =  iWorkflowFeign.upload(params);
        uploadFile.delete();
        return jarPath;
    }

    //已上传文件删除
    public String delectFileFromHdfs(String filePath) throws IOException {
        Map<String, Object> params = new HashMap();
        params.put("request", filePath);

        return this.iWorkflowFeign.delect(params);
    }

    //数据预览
    public List<LinkedHashMap<String, Object>> dataPreviewPro(String dataresourceType,
                                                           String host,
                                                           String port,
                                                           String username,
                                                           String password,
                                                           String databaseName,
                                                           String tableName,
                                                           String colNames,
                                                           int number) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);

        return dataResource.getTableData(tableName, colNames, "", number);

    }

    //获取数据库字段名及类型
    public Map<String, String> getTableColumnAndTypesPro(String dataresourceType,
                                                      String host,
                                                      String port,
                                                      String username,
                                                      String password,
                                                      String databaseName,
                                                      String tableName) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);

        return dataResource.getTableColumnAndTypes(tableName);

    }

    //类别列值
    public Set<Object> getCategoryValuePro(String dataresourceType,
                                        String host,
                                        String port,
                                        String username,
                                        String password,
                                        String databaseName,
                                        String tableName,
                                        String colName) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);

        List<LinkedHashMap<String, Object>> maps = dataResource.getTableData(tableName, colName, "", 0);
        Set<Object> record = Sets.newHashSet();
        for (Map<String, Object> map : maps) {
            record.add(map.get(colName));
        }
        return record;
    }

    //获取数据源配置信息
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Map<String, String> getSourceConf(String dataresourceId) {
        List<AnalysisSourceConfEntity> sourceConfs = iAnalysisModelFeign.getSourceConf(dataresourceId);
        AnalysisSourceEntity analysisSourceEntity = iAnalysisModelFeign.getByDataresourceId(dataresourceId);
        Map<String, String> confs = new HashMap<>();
        confs.put("dataresourceType", analysisSourceEntity.getDataresourceType());
        for (AnalysisSourceConfEntity confEntity : sourceConfs) {
            confs.put(confEntity.getParameterName(), confEntity.getParameterValue());
        }
        return confs;
    }
}
