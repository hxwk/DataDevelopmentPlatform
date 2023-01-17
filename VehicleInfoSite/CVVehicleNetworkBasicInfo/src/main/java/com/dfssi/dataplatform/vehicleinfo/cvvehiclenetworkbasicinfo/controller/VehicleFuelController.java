package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelCountByDaysEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.VehicleFuelService;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.util.ResponseUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/14 19:46
 */
@Controller
@RequestMapping(value = "/vehicleFuel/")
@Api(value = "VehicleFuelController", description = "车辆油耗统计分析信息控制器")
public class VehicleFuelController {

    private final Logger logger = LoggerFactory.getLogger(VehicleFuelController.class);

    @Autowired
    private VehicleFuelService vehicleFuelService;

    @ApiOperation(value = "油耗时间统计-按天、vid分组统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆唯一标识，多个使用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "starttime", value = "统计开始日期时间戳", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endtime", value = "统计结束日期时间戳", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "int", defaultValue="1", paramType = "query"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "int", defaultValue="10", paramType = "query")
    })
    @RequestMapping(value = "count/fuelByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object countFuelByDay(String vid,
                                 @RequestParam long starttime,
                                 @RequestParam long endtime,
                                 @RequestParam(defaultValue = "1") int pagenum,
                                 @RequestParam(defaultValue = "10") int pagesize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        param.put("starttime", starttime);
        param.put("endtime", endtime);
        param.put("pagenum", pagenum);
        param.put("pagesize", pagesize);
        logger.info(String.format("查询油耗时间统计信息，参数如下：\n\t %s", param));

        ResponseObj responseObj;
        try {
            Map<String, Object> data = vehicleFuelService.countFuelByDay(vid, starttime, endtime, pagenum, pagesize);
           responseObj = ResponseUtil.success(data);

        } catch (Exception e) {
            logger.error("查询油耗时间统计信息失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }

        return responseObj;
    }

    @ApiOperation(value = "油耗时间统计-分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆唯一标识，多个使用英文逗号分隔", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "starttime", value = "统计开始日期时间戳", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endtime", value = "统计结束日期时间戳", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "count/fuelAnalysisInDays", method = {RequestMethod.GET})
    @ResponseBody
    public Object countFuelAnalysisInDays(@RequestParam String vid,
                                          @RequestParam long starttime,
                                          @RequestParam long endtime){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        param.put("starttime", starttime);
        param.put("endtime", endtime);
        logger.info(String.format("查询油耗时间统计分析信息，参数如下：\n\t %s", param));

        ResponseObj responseObj;
        try {
            Map<String, Object> data = vehicleFuelService.countFuelAnalysisInDays(vid, starttime, endtime);
            responseObj = ResponseUtil.success(data);

        } catch (Exception e) {
            logger.error("查询油耗时间统计分析信息失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }

        return responseObj;
    }

    @ApiOperation(value = "油耗时间统计-汇总指定时间范围内的车辆的油耗(L)、里程(km)、运行时长(h)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆唯一标识，多个使用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startDay", value = "统计开始日期，格式：yyyy-MM-dd", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endDay", value = "统计结束日期，格式：yyyy-MM-dd", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "count/fuelAnalysisByDays", method = {RequestMethod.GET})
    @ResponseBody
    public Object countFuelAnalysisByDays(String vid,
                                          @RequestParam String startDay,
                                          @RequestParam String endDay){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        param.put("startDay", startDay);
        param.put("endDay", endDay);
        logger.info(String.format("汇总指定时间范围内的车辆的油耗、里程、运行时长信息，参数如下：\n\t %s", param));

        ResponseObj responseObj;
        try {
            startDay = startDay.replaceAll("-", "");
            endDay = endDay.replaceAll("-", "");
            List<VehicleFuelCountByDaysEntity> data = vehicleFuelService.countFuelAnalysisByDays(vid, startDay, endDay);
            responseObj = ResponseUtil.success(data.size(), data);

        } catch (Exception e) {
            logger.error("汇总指定时间范围内的车辆的油耗、里程、运行时长信息失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }

        return responseObj;
    }

    @ApiOperation(value = "最新状态查询-查询指定车辆的当前总里程(KM),总油耗(L),当前车速(km/h)以及GPS时间(ms)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆唯一标识，多个使用英文逗号分隔",  dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "search/latestStatusByVid", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchLatestStatusByVid(String vid){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        logger.info(String.format("最新状态查询-查询指定车辆的当前总里程(KM),总油耗(L),当前车速(km/h)以及GPS时间(ms)，参数如下：\n\t %s", param));

        ResponseObj responseObj;
        try {
            List<Map<String, Object>> data = vehicleFuelService.searchLatestStatusByVid(vid);
            responseObj = ResponseUtil.success(data.size(), data);
        } catch (Exception e) {
            logger.error("最新状态查询-查询指定车辆的当前总里程(KM),总油耗(L),当前车速(km/h)以及GPS时间(ms)失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }

        return responseObj;
    }


    @ApiOperation(value = "油耗时间统计-按指定类型分组统计 未完成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "item", value = "分组统计项", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vals", value = "分组统计项中的过滤值， 多个使用英文逗号分隔。",
                    required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "starttime", value = "统计开始日期时间戳", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endtime", value = "统计结束日期时间戳", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "count/fuelByItem", method = {RequestMethod.GET})
    @ResponseBody
    public Object countFuelByItem(@RequestParam String item,
                                  String vals,
                                  @RequestParam long starttime,
                                  @RequestParam long endtime){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("item", item);
        param.put("vals", vals);
        param.put("starttime", starttime);
        param.put("endtime", endtime);
        logger.info(String.format("查询按指定类型分组统计油耗信息，参数如下：\n\t %s", param));

        ResponseObj responseObj;
        try {
            Map<String, Object> data = vehicleFuelService.countFuelByItem(item, vals, starttime, endtime);
            responseObj = ResponseUtil.success(data);

        } catch (Exception e) {
            logger.error("查询按指定类型分组统计油耗失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }

        return responseObj;
    }



}
