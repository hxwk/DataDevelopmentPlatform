package com.dfssi.dataplatform.ide.analysis.web;

import com.dfssi.dataplatform.analysis.entity.AnalysisModelEntity;
import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.common.controller.ResponseObj;
import com.dfssi.dataplatform.ide.analysis.restful.IAnalysisModelFeign;
import com.dfssi.dataplatform.ide.analysis.restful.IWorkflowFeign;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/18 10:55
 */
@Controller
@RequestMapping(value = "task/taskManager")
public class TaskManagerController  extends AbstractAnalysisIDEController  {
    private final static String LOG_TAG_MODEL_ANALYSIS = "[Analysis Model IDE]";

    public static final String MODEL_TYPE_ANALYSIS = "ANALYSIS";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAnalysisModelFeign iAnalysisModelFeign;

    @Autowired
    private IWorkflowFeign iWorkflowFeign;
    //任务启动
    @ResponseBody
    @RequestMapping(value = "start/{modelId}", method = RequestMethod.GET)
    public Object startTask(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("modelId", modelId);
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Startup task. modelId=" + modelId);
            this.deployModel(modelId);
            responseObj.buildSuccessMsg("Startup task successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to startup task. modelId=" + modelId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_MODEL_ANALYSIS + "Failed to startup task. modelId=" + modelId + "\n", t);
        }

        return responseObj;
    }

    //从任务列表中删除任务，需要先停止任务
    @ResponseBody
    @RequestMapping(value = "delete/{modelId}", method = RequestMethod.GET)
    public Object deleteModel(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "Delete model. modelId=" + modelId);

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

    //查询任务列表
    @ResponseBody
    @RequestMapping(value = "listmodels/{pageIdx}/{pageSize}", method = RequestMethod.GET)
    public Object listAllModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx") int
            pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime, String
                                        status) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_MODEL_ANALYSIS + "List all models.");

            List<AnalysisModelEntity> modelEntities = this.listAllAnalysisModels(pageIdx,
                    pageSize, modelName, startTime, endTime, status);
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

    public String deployModel(String modelId) throws IOException {
        com.dfssi.dataplatform.analysis.model.Model model = iAnalysisModelFeign.getModel(modelId);
        Map<String, Object> params = new HashMap();
        params.put("request", objectMapper.writeValueAsString(model));

        return iWorkflowFeign.explordeddeploy(params);
    }
    public List<AnalysisModelEntity> listAllAnalysisModels(int pageIdx, int pageSize, String modelName, Long
            startTime, Long endTime, String status) {
        return iAnalysisModelFeign.listAllModels(pageIdx, pageSize, MODEL_TYPE_ANALYSIS, modelName, startTime, endTime, status, "create_time", "desc");
    }}
