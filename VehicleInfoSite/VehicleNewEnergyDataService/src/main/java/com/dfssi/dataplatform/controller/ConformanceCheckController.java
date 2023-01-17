package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.service.ConformanceCheckService;
import com.dfssi.dataplatform.service.ProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 * 符合性检测数据接口
 *
 * @author bin.Y
 * @version 2018/4/13 9:35
 */
@Controller
@RequestMapping("/conformance")
@Api(tags = {"新能源平台符合性检测"})
public class ConformanceCheckController {
    @Autowired
    private ProcessService processService;
    @Autowired
    private ConformanceCheckService conformanceCheckService;

    @ApiOperation(value = "符合性检测开始")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json", value = "检测报文"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/checkInfoReceive", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, String> checkInfoReceive(@RequestParam("json") String json) throws Exception {
        System.out.println("----------checkInfoReceive");
        HashMap<String, String> resultMap = processService.processCheckInfo(json);
        return resultMap;
    }

    @ApiOperation(value = "符合性检测结果查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", value = "车企ID"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/getCheckResult", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Map<String, String>> getCheckResult(@RequestParam("companyId") String companyId) throws Exception {
        Map<String, Map<String, String>> resultMap = conformanceCheckService.getCheckResult(companyId);
        return resultMap;
    }

}
