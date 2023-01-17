package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 10:25
 */
@Api(tags = {"控制器-测试"})
@RestController
@RequestMapping(value="/demo/")
public class DemoController {
    private final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private DemoService demoService;

    @ApiOperation(value = "测试mysql数据库查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "Integer", defaultValue="1", paramType = "query"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "Integer", defaultValue="10", paramType = "query")
    })
    @RequestMapping(value = "list/vehicle", method = {RequestMethod.GET})
    @ResponseBody
    public Object getVehicle(@RequestParam(defaultValue = "1") int pagenum,
                             @RequestParam(defaultValue = "10") int pagesize){
        System.out.println(pagenum + " ** " + pagesize);
        return demoService.getVehicle(pagenum, pagesize);
    }

    @ApiOperation(value = "测试mysql数据库查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "Integer", defaultValue="1", paramType = "query"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "Integer", defaultValue="10", paramType = "query")
    })
    @RequestMapping(value = "list/detecteRules", method = {RequestMethod.GET})
    @ResponseBody
    public Object getDetecteRules(@RequestParam(defaultValue = "1") int pagenum,
                             @RequestParam(defaultValue = "10") int pagesize){
        System.out.println(pagenum + " ** " + pagesize);
        return demoService.getDetecteRules(pagenum, pagesize);
    }
}
