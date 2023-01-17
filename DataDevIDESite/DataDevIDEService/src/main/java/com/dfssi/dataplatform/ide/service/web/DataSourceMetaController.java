package com.dfssi.dataplatform.ide.service.web;

import com.dfssi.dataplatform.analysis.entity.AnalysisResourceConfEntity;
import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.common.controller.ResponseObj;
import com.dfssi.dataplatform.ide.service.restful.IAnalysisModelFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description:
 *
 * @author pengwk
 * @version 2018/7/4 9:46
 */
@Api(tags = {"页面服务资源控制器"})
@RestController
@RequestMapping(value="/datasource/")
public class DataSourceMetaController {

    private final Logger logger = LoggerFactory.getLogger(DataSourceMetaController.class);
    private long searchInterval = 5 * 60 * 1000L;

    @Autowired
    private IAnalysisModelFeign iAnalysisModelFeign;

    @ApiOperation(value = "列举某条数据资源信息")
    @RequestMapping(value = "get", method = {RequestMethod.GET})
    @ResponseBody
    public Object get(HttpServletRequest request, HttpServletResponse response, String resourceId){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            List<AnalysisResourceConfEntity> resourceConfEntities = iAnalysisModelFeign.getResourceConf(resourceId);
            responseObj.setData(resourceConfEntities);
            responseObj.buildSuccessMsg("Get resource conf successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get resource resource conf.", Exceptions
                    .getStackTraceAsString(t));
        }

        return responseObj;
    }

    @ApiOperation(value = "删除某条数据资源")
    @RequestMapping(value = "delect", method = {RequestMethod.POST})
    @ResponseBody
    public Object delectDataResource(HttpServletRequest request, HttpServletResponse response, String resourceId){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            iAnalysisModelFeign.deleteByResourceId(resourceId);
            iAnalysisModelFeign.deleteByResourceId(resourceId);
            responseObj.buildSuccessMsg("Delect resource data successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to delect resource data.", Exceptions
                    .getStackTraceAsString(t));
        }

        return responseObj;
    }
}

