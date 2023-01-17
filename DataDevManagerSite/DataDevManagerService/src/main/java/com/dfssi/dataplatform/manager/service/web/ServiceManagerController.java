package com.dfssi.dataplatform.manager.service.web;

import com.dfssi.dataplatform.analysis.entity.AnalysisModelEntity;
import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.common.controller.AbstractIDEController;
import com.dfssi.dataplatform.common.controller.ResponseObj;
import com.dfssi.dataplatform.manager.service.service.ServiceManagerService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "manager/service")
public class ServiceManagerController extends AbstractIDEController {

    private final static String LOG_TAG_SERVICE_MANAGER = "[Service Manager]";

    @Autowired
    private ServiceManagerService serviceManagerService;

    @ResponseBody
    @RequestMapping(value = "listmodels/{pageIdx}/{pageSize}")
    public Object listAllModels(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx") int
            pageIdx, @PathVariable("pageSize") int pageSize, String modelName, Long startTime, Long endTime, String
            status) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_SERVICE_MANAGER + "List all models.");

            List<AnalysisModelEntity> modelEntities = this.serviceManagerService.listAllServiceModels(pageIdx,
                    pageSize, modelName, startTime, endTime, status);
            long total = ((Page) modelEntities).getTotal();
            responseObj.setTotal(total);
            responseObj.setData(modelEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all models.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_SERVICE_MANAGER + "Failed to list all models.\n", t);
        }

        return responseObj;
    }

}
