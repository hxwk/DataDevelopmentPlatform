package com.dfssi.dataplatform.abs.controller;

import com.dfssi.dataplatform.abs.service.AbsCheckService;
import com.dfssi.dataplatform.abs.utils.ResponseObj;
import com.dfssi.dataplatform.abs.utils.ResponseUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/27 8:47
 */
@Controller
@RequestMapping(value = "AbsCheck")
@Api(value = "AbsCheckController", description = "车辆自动化检测")
@Slf4j
public class AbsCheckController {

    @Autowired
    private AbsCheckService absCheckService;

    @ResponseBody
    @RequestMapping(value = "absCheckTask", method = RequestMethod.POST)
    @ApiOperation(value = "ABS自动检测分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "待检车辆vid号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleType", value = "待检测车辆类型", dataType = "int", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "vehicleStatus", value = "待检车辆状态，为满载或空载", dataType = "int", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "minStartBreakSpeed", value = "制动初速度限制", dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "minBreakLessSpeed", value = "最小减速度", dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "maxBreakDistance", value = "最大制动距离", dataType = "double", paramType = "query")
    })
    public Object absCheckTask(@RequestParam String vid,
                               @RequestParam int vehicleType,
                               @RequestParam int vehicleStatus,
                               @RequestParam double minStartBreakSpeed,
                               @RequestParam double minBreakLessSpeed,
                               @RequestParam double maxBreakDistance) {
        log.info("启动ABS检测任务");
        ResponseObj responseObj;
        try {
            absCheckService.absCheckTask(vid, vehicleType, vehicleStatus, minStartBreakSpeed, minBreakLessSpeed, maxBreakDistance);
            responseObj = ResponseUtil.success();
        } catch (Exception e) {
            log.error("ABS自动检测分析信息结果查询失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }
        return responseObj;
    }


}
