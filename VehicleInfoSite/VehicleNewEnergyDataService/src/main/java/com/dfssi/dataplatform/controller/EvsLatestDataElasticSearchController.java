package com.dfssi.dataplatform.controller;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.service.EvsLatestDataElasticSearchService;
import com.dfssi.dataplatform.utils.ResponseUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/2 11:29
 */
@Api(tags = {"新能源实时数据es查询控制器"})
@RestController
@RequestMapping(value="/es/latest/")
public class EvsLatestDataElasticSearchController {
    private final Logger logger = LoggerFactory.getLogger(EvsLatestDataElasticSearchController.class);

    @Autowired
    private EvsLatestDataElasticSearchService evsLatestDataElasticSearchService;

    @ApiOperation(value = "统计当前车辆在线情况")
    @RequestMapping(value = "online", method = {RequestMethod.GET})
    @ResponseBody
    public Object countLatestOnlineMsg(){

        logger.info("统计当前车辆在线情况...");

        Map<String, Object> res;
        try {
            Map<String, Object> map = evsLatestDataElasticSearchService.countLatestOnlineMsg();
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("统计当前车辆在线情况失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "统计当前车辆工作状态")
    @RequestMapping(value = "workingStatus", method = {RequestMethod.GET})
    @ResponseBody
    public Object countLatestWorkingstatus(){

        logger.info("统计当前车辆工作状态...");

        Map<String, Object> res;
        try {
            Map<String, Object> map = evsLatestDataElasticSearchService.countLatestWorkingstatus();
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("统计当前车辆工作状态失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "统计当前车企在线车辆topN信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topN", value = "前N条", dataType = "integer", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "onlineTopNVehicleCompany", method = {RequestMethod.GET})
    @ResponseBody
    public Object countOnlineTopNVehicleCompany(@RequestParam(defaultValue = "10") int topN){

        Map<String, Object> param = Maps.newHashMap();
        param.put("topN", topN);
        logger.info(String.format("统计当前车企在线车辆topN信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Map<String, Object>> maps = evsLatestDataElasticSearchService.countOnlineTopNVehicleCompany(topN);
            res = ResponseUtil.success(maps.size(), maps);
        } catch (Exception e) {
            logger.error("统计当前车企在线车辆topN信息失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "统计武汉市区域流动车辆topN信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topN", value = "前N条", dataType = "integer", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "onlineTopNArea", method = {RequestMethod.GET})
    @ResponseBody
    public Object countOnlineTopNArea(@RequestParam(defaultValue = "10") int topN){

        Map<String, Object> param = Maps.newHashMap();
        param.put("topN", topN);
        logger.info(String.format("统计武汉市区域流动车辆topN信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Map<String, Object>> maps = evsLatestDataElasticSearchService.countOnlineTopNArea(topN);
            res = ResponseUtil.success(maps.size(), maps);
        } catch (Exception e) {
            logger.error("统计武汉市区域流动车辆topN信息失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "查询指定车辆的最新数据", notes = "gcj02: 高德坐标； bd09： 百度坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆vin", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09")
    })
    @RequestMapping(value = "latestDataByVin", method = {RequestMethod.GET})
    @ResponseBody
    public Object getLatestDataByVin(@RequestParam String vin,
                                     String columns,
                                     @RequestParam(defaultValue = "gcj02") String posType){

        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        param.put("columns", columns);
        param.put("posType", posType);
        logger.info(String.format("查询指定车辆的最新数据，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }
            Map<String, Object> map = evsLatestDataElasticSearchService.getLatestDataByVin(vin, columnSet, posType);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询指定车辆的最新数据失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "查询指定车辆的最新状态数据", notes = "gcj02: 高德坐标； bd09： 百度坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆vin", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09")
    })
    @RequestMapping(value = "latestVehicleDataByVin", method = {RequestMethod.GET})
    @ResponseBody
    public Object getLatestVehicleDataByVin(@RequestParam String vin,
                                            @RequestParam(defaultValue = "gcj02") String posType){

        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        param.put("posType", posType);
        logger.info(String.format("查询指定车辆的最新状态数据，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Set<String> columnSet = Sets.newHashSet("vehicleStatusCode", "vehicleStatus", "chargingStatusCode",
                    "chargingStatus", "speed", "accumulativeMile", "soc", "latitude", "longitude","collectTime");
            Map<String, Object> map = evsLatestDataElasticSearchService.getLatestDataByVin(vin, columnSet, posType);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询指定车辆的最新状态数据失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "查询车辆的最新位置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "车辆状态", dataType = "String",
                    paramType = "query", allowableValues = "online,running,charging"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "latestVehiclePosData", method = {RequestMethod.GET})
    @ResponseBody
    public Object getLatestVehiclePosData(String status,
                                          @RequestParam(defaultValue = "1") int pageNow,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(defaultValue = "gcj02") String posType){

        Map<String, Object> param = Maps.newHashMap();
        param.put("status", status);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询车辆的最新位置信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            int from = (pageNow - 1) * pageSize;
            if(from + pageSize >= 10000){
                from = 10000 - pageSize;
            }
            Map<String, Object> map = evsLatestDataElasticSearchService.getLatestVehiclePosData(status, from, pageSize, posType);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询车辆的最新位置信息失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "查询车辆最新数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "integer", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "integer", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "latestData", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchLatestData(String enterprises,
                                   String hatchbacks,
                                   String vins,
                                   String columns,
                                   Long startTime,
                                   Long endTime,
                                   @RequestParam(defaultValue = "1") int pageNow,
                                   @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("columns", columns);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询车辆最新数据列表，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }

            String[] enterprise = (enterprises != null) ?
                    enterprises.split(",") : null;
            String[] hatchback = (hatchbacks != null) ?
                    hatchbacks.split(",") : null;
            String[] vin = (vins != null) ?
                    vins.split(",") : null;

            int from = (pageNow - 1) * pageSize;
            Map<String, Object> map = evsLatestDataElasticSearchService.searchLatestData(enterprise,
                    hatchback, vin, columnSet, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询车辆最新数据列表失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询离线车辆数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "offlineDays", value = "离线天数", dataType = "int", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "offlineData", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchOfflineData(String enterprises,
                                   String hatchbacks,
                                   String vins,
                                   String columns,
                                   @RequestParam int offlineDays,
                                   @RequestParam(defaultValue = "1") int pageNow,
                                   @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("columns", columns);
        param.put("offlineDays", offlineDays);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询离线车辆数据列表，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }

            long millis = DateTime.now().minusDays(offlineDays + 1).getMillis();

            String[] enterprise = (enterprises != null) ?
                    enterprises.split(",") : null;
            String[] hatchback = (hatchbacks != null) ?
                    hatchbacks.split(",") : null;
            String[] vin = (vins != null) ?
                    vins.split(",") : null;

            int from = (pageNow - 1) * pageSize;
            if(from + pageSize >= 10000){
                from = 10000 - pageSize;
            }
            Map<String, Object> map = evsLatestDataElasticSearchService.searchLatestData(enterprise,
                    hatchback, vin, columnSet, null, millis, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询离线车辆数据列表失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }


    @ApiOperation(value = "按车型或车企分组统计在线以及运行车辆数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "field", value = "分组统计字段：enterprise-企业，hatchback-车型",
                    dataType = "string", paramType = "query", required = true, allowableValues="enterprise,hatchback"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "integer", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "integer", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "countOnlineAndRunningByField", method = {RequestMethod.GET})
    @ResponseBody
    public Object countOnlineAndRunningByField(String enterprises,
                                               String hatchbacks,
                                               String vins,
                                               @RequestParam(defaultValue = "enterprise") String field,
                                               @RequestParam(defaultValue = "1") int pageNow,
                                               @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("field", field);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("按车型或车企分组统计在线以及运行车辆数，参数如下：\n\t %s", param));

        if("hatchback".equalsIgnoreCase(field)){
            param.put("field", "vehicleType");
        }else{
            param.put("field", "vehicleCompany");
        }

        Map<String, Object> res;
        try {

            int from = (pageNow - 1) * pageSize;
            Map<String, Object> map = evsLatestDataElasticSearchService.countOnlineAndRunningByField(JSON.toJSONString(param), from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("按车型或车企分组统计在线以及运行车辆数失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

}
