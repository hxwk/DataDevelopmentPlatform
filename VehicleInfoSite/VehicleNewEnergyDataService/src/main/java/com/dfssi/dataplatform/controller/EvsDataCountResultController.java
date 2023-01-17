package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.cache.result.EvsDataCountResultCache;
import com.dfssi.dataplatform.entity.count.MaxOnlineAndRun;
import com.dfssi.dataplatform.entity.count.MileDetail;
import com.dfssi.dataplatform.entity.count.TotalMileAndTime;
import com.dfssi.dataplatform.entity.count.TotalRunningMsg;
import com.dfssi.dataplatform.service.EvsDataCountResultService;
import com.dfssi.dataplatform.utils.ResponseUtil;
import com.google.common.collect.Maps;
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

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 11:32
 */
@Api(tags = {"新能源数据统计结果查询控制器"})
@RestController
@RequestMapping(value="/data/count/")
public class EvsDataCountResultController {
    private final Logger logger = LoggerFactory.getLogger(EvsDataCountResultController.class);

    @Autowired
    private EvsDataCountResultService evsDataCountResultService;

    @Autowired
    private EvsDataCountResultCache evsDataCountResultCache;

    @ApiOperation(value = "查询汇总所有车辆总行驶里程、车辆总行驶时长、总耗电量")
    @RequestMapping(value = "totalMileAndTime", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryTotalMileAndTime(){
        logger.info("查询汇总所有车辆总行驶里程、车辆总行驶时长。");

        Map<String, Object> res;
        TotalMileAndTime totalMileAndTime;
        try {
            totalMileAndTime = evsDataCountResultService.queryTotalMileAndTime();
            res = ResponseUtil.success(totalMileAndTime);
        } catch (Exception e) {
            logger.error("查询汇总所有车辆总行驶里程、车辆总行驶时长失败。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }

    @ApiOperation(value = "查询汇总所有车辆总耗电量")
    @RequestMapping(value = "totalEnergy", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryTotalEnergy(){
        logger.info("查询汇总所有车辆总耗电量。");

        Map<String, Object> res;
        Map<String, Double> totalEnergy;
        try {
            totalEnergy = evsDataCountResultService.queryTotalEnergy();
            res = ResponseUtil.success(totalEnergy);
        } catch (Exception e) {
            logger.error("查询汇总所有车辆总耗电量。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }

    @ApiOperation(value = "查询单一车辆总行驶里程、车辆总行驶时长、总耗电量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆vin, 不支持多个", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "totalMileAndTimeByVin", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryTotalMileAndTimeByVin(@RequestParam String vin){
        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        logger.info(String.format("查询单一车辆总行驶里程、车辆总行驶时长、总耗电量, 参数如下：\n %s", param));

        Map<String, Object> res;
        TotalRunningMsg totalMileAndTime;
        try {
            totalMileAndTime = evsDataCountResultService.queryTotalMileAndTimeByVin(vin);
            res = ResponseUtil.success(totalMileAndTime);
        } catch (Exception e) {
            logger.error("查询单一车辆总行驶里程、车辆总行驶时长、总耗电量失败。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }

    @ApiOperation(value = "查询单一车辆总行驶里程、车辆总行驶时长、总耗电量、核算里程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆vin, 不支持多个", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "totalRunningMsgByVin", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryTotalRunningMsgByVin(@RequestParam String vin){
        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        logger.info(String.format("查询单一车辆总行驶里程、车辆总行驶时长、总耗电量、核算里程, 参数如下：\n %s", param));

        Map<String, Object> res;
        try {
            Object runningMsg = evsDataCountResultCache.gettotalRunningMsg(vin);
            res = ResponseUtil.success(runningMsg);
        } catch (Exception e) {
            logger.error("查询单一车辆总行驶里程、车辆总行驶时长、总耗电量、核算里程失败。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }

    @ApiOperation(value = "查询当前月度平台车辆日在线和运行最大数")
    @RequestMapping(value = "currentMonthMaxOnlineAndRunByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryCurrentMonthMaxOnlineAndRunByDay(){
        //查询日志
        logger.info("查询当前月度平台车辆日在线和运行最大数。");

        Map<String, Object> res;
        List<MaxOnlineAndRun> maxOnlineAndRuns;
        try {
            maxOnlineAndRuns = evsDataCountResultService.queryCurrentMonthMaxOnlineAndRunByDay();
            res = ResponseUtil.success(maxOnlineAndRuns.size(), maxOnlineAndRuns);
        } catch (Exception e) {
            logger.error("查询当前月度平台车辆日在线和运行最大数失败。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }

    @ApiOperation(value = "统计月度平台车辆在线和运行总数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "months", value = "月份: yyyyMM, 多个用英文逗号分隔，默认统计前一个月", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "monthTotalOnlineAndRun", method = {RequestMethod.GET})
    @ResponseBody
    public Object countMonthTotalOnlineAndRun(String months){
        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("months", months);
        logger.info(String.format("统计月度平台车辆在线和运行总数，参数如下：\n\t %s", param));

        Map<String, Object> res;
        List<Object> totalOnlineAndRuns;
        try {
            totalOnlineAndRuns = evsDataCountResultCache.getMonthTotalOnlineAndRunCountResult(months);
            res = ResponseUtil.success(totalOnlineAndRuns.size(), totalOnlineAndRuns);
        } catch (Exception e) {
            logger.error("统计月度平台车辆在线和运行总数失败。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }


    @ApiOperation(value = "查询车辆里程统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆VIN", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "mileDetailByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryMileDetail(@RequestParam String vin,
                                  Long startTime,
                                  Long endTime) {

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        logger.info(String.format("查询车辆里程统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            MileDetail data = evsDataCountResultService.queryMileDetail(vin, startTime, endTime);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询车辆里程统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "条件查询车辆行驶里程信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN列表, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "vehicleMileAndTime", method = {RequestMethod.GET})
    @ResponseBody
    public Object countVehicleMileAndTime(String enterprise,
                                          String hatchback,
                                          String vin,
                                          Long startTime,
                                          Long endTime,
                                          @RequestParam(defaultValue = "1") int pageNow,
                                          @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("条件查询车辆行驶里程信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataCountResultService
                    .countVehicleMileAndTime(enterprise, hatchback, vin, startTime, endTime, pageNow, pageSize);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("条件查询车辆行驶里程信息。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "固定时间范围内按天分组查询车辆故障报警统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN列表, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "long", paramType = "query")
    })
    @RequestMapping(value = "warningCountByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryWarningCountByDay(String enterprise,
                                         String hatchback,
                                         String vin,
                                         Long startTime,
                                         Long endTime){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        logger.info(String.format("固定时间范围内按天分组查询车辆故障报警统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Map<String, Object>> data = evsDataCountResultService
                    .queryWarningCountByDay(enterprise, hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data.size(), data);
        } catch (Exception e) {
            logger.error("固定时间范围内按天分组查询车辆故障报警统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "固定时间范围内查询汇总车辆故障报警统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN列表, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "long", paramType = "query")
    })
    @RequestMapping(value = "warningCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryWarningCount(String enterprise,
                                         String hatchback,
                                         String vin,
                                         Long startTime,
                                         Long endTime){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        logger.info(String.format("固定时间范围内查询汇总车辆故障报警统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataCountResultService
                    .queryWarningCount(enterprise, hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("固定时间范围内查询汇总车辆故障报警统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询汇总车辆故障报警次数统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN列表, 英文逗号分隔", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "warningTotal", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryWarningTotal(String enterprise,
                                    String hatchback,
                                    String vin){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        logger.info(String.format("查询汇总车辆故障报警次数统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataCountResultService
                    .queryWarningCountTotal(enterprise, hatchback, vin);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询汇总车辆故障报警次数统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询在线车辆按车企车型分组统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "onlineCountByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryOnlineCountByDay(String enterprise,
                                        String hatchback,
                                        Long startTime,
                                        Long endTime,
                                        @RequestParam(defaultValue = "1") int pageNow,
                                        @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询在线车辆按车企车型分组统计信息，参数如下：\n\t %s", param));


        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataCountResultService
                    .queryOnlineCountByDay(enterprise, hatchback, startTime, endTime, pageNow, pageSize);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询在线车辆按车企车型分组统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询在线车辆按车企分组统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "onlineCountInEnterpriseByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryOnlineCountInEnterpriseByDay(String enterprise,
                                                    String hatchback,
                                                    Long startTime,
                                                    Long endTime,
                                                    @RequestParam(defaultValue = "1") int pageNow,
                                                    @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询在线车辆按车企分组统计信息，参数如下：\n\t %s", param));


        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataCountResultService
                    .queryOnlineCountInEnterpriseByDay(enterprise, hatchback, startTime, endTime, pageNow, pageSize);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询在线车辆按车企分组统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }


}
