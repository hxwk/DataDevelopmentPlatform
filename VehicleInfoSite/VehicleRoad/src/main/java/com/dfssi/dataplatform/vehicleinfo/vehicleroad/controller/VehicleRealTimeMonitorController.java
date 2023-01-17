package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.VehicleRealTimeMonitorService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ResponseUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *   车辆实时监控
 * @author LiXiaoCong
 * @version 2018/9/21 11:24
 */
@Api(tags = "车辆实时监控", description="实时监控相关的数据查询接口")
@RestController
@RequestMapping("/vehicleMonitor")
public class VehicleRealTimeMonitorController {
    private final Logger logger = LoggerFactory.getLogger(VehicleRealTimeMonitorController.class);

    @Autowired
    private VehicleRealTimeMonitorService vehicleRealTimeMonitorService;

    @ApiOperation(value = "查询指定车辆的最新数据", notes = "gcj02: 高德坐标； bd09： 百度坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09")
    })
    @RequestMapping(value = "latestDataByVid", method = {RequestMethod.GET})
    @ResponseBody
    public Object getLatestDataByVid(@RequestParam String vid,
                                     String columns,
                                     @RequestParam(defaultValue = "gcj02") String posType){

        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        param.put("columns", columns);
        param.put("posType", posType);
        logger.info(String.format("查询指定车辆的最新数据，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }
            Map<String, Object> map = vehicleRealTimeMonitorService.getLatestDataByVid(vid, columnSet, posType);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询指定车辆的最新数据失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "查询车辆的最新数据", notes = "gcj02: 高德坐标； bd09： 百度坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid, 多个则以英文逗号分隔", dataType = "string",  paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "latestData", method = {RequestMethod.GET})
    @ResponseBody
    public Object getLatestData(String vid,
                               String columns,
                               @RequestParam(defaultValue = "gcj02") String posType,
                               @RequestParam(defaultValue = "1") int pageNow,
                               @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        param.put("columns", columns);
        param.put("posType", posType);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询车辆的最新数据，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }
            Map<String, Object> map = vehicleRealTimeMonitorService.getLatestData(vid, columnSet, posType, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询车辆的最新数据失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "查询固定时间范围里指定车辆的抽样行驶轨迹", notes = "gcj02: 高德坐标； bd09： 百度坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆VID", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stepLength", value = "步长", dataType = "int", defaultValue = "5", paramType = "query"),
            @ApiImplicitParam(name = "maxRows", value = "返回最大记录数", dataType = "int", defaultValue = "10000", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09")
    })
    @RequestMapping(value = "samplingTrackLocations", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchSamplingTrackLocations(@RequestParam String vid,
                                               @RequestParam Long startTime,
                                               @RequestParam Long endTime,
                                               @RequestParam(defaultValue = "5") Integer stepLength,
                                               @RequestParam(defaultValue = "10000") int maxRows,
                                               @RequestParam(defaultValue = "gcj02") String posType){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();

        param.put("vid", vid);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("stepLength", stepLength);
        param.put("posType", posType);
        logger.info(String.format("查询固定时间范围里指定车辆的抽样行驶轨迹，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

            List<Map<String, Object>> list = vehicleRealTimeMonitorService
                    .searchSamplingTrackLocations(vid, startTime, endTime, stepLength, posType, maxRows);
            res = ResponseUtil.success(list.size(), list);
        } catch (Exception e) {
            logger.error("查询固定时间范围里指定车辆的抽样行驶轨迹失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }


    @ApiOperation(value = "查询车辆的最新状态", notes = "status: 0：不在线，1：在线， 2：停止，3：行驶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "车辆状态", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "latestStatus", method = {RequestMethod.GET})
    @ResponseBody
    public Object getLatestStatus(String vid,
                                  Integer status,
                                  @RequestParam(defaultValue = "1") int pageNow,
                                  @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> param = Maps.newHashMap();
        param.put("vid", vid);
        param.put("status", status);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询车辆的最新状态，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleRealTimeMonitorService.getLatestStatus(vid, status, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询指定车辆的最新数据失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

}
