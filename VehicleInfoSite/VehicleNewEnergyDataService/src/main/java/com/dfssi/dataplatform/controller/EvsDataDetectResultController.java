package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.annotation.LogAudit;
import com.dfssi.dataplatform.service.EvsDataDetectResultService;
import com.dfssi.dataplatform.utils.DateUtil;
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
 *   数据质量检测检测结果查询控制器
 * @author LiXiaoCong
 * @version 2018/4/21 11:31
 */
@Api(tags = {"新能源数据质量检测结果查询控制器"})
@RestController
@RequestMapping(value="/detect/count/")
public class EvsDataDetectResultController {
    private final Logger logger = LoggerFactory.getLogger(EvsDataDetectResultController.class);

    @Autowired
    private EvsDataDetectResultService evsDataDetectResultService;

    @ApiOperation(value = "查询车辆当天数据质量信息",
            notes = "数据项标识：00-整车数据，01-驱动电机数据，02-燃料电池，03-发动机数据 04-车辆位置数据，05-极值数据，06-报警数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆VIN", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "time", value = "时间戳", dataType = "long", paramType = "query")
    })
    @RequestMapping(value = "dataQualityInDayByVin", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDataQualityInDay(@RequestParam String vin,
                                        Long time){
        String day;
        if(time == null){
            day = DateUtil.getNow("yyyyMMdd");
        }else{
            day = DateUtil.getDateStr(time, "yyyyMMdd");
        }

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        param.put("day", day);
        logger.info(String.format("查询车辆当天数据质量信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryDataQualityInDay(vin, day);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询车辆故障报警统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }


    @ApiOperation(value = "查询各项数据质量信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "item", value = "数据项标识：00-整车数据，01-驱动电机数据，02-燃料电池，03-发动机数据 04-车辆位置数据，05-极值数据，06-报警数据",
                    dataType = "string", paramType = "query", required = true, allowableValues="00,01,02,03,04,05,06"),
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query")
    })
    @RequestMapping(value = "itemDataQuality", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryItemDataQuality(String item,
                                       String enterprise,
                                       String hatchback,
                                       String vin){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("item", item);
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        logger.info(String.format("查询各项数据质量信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryItemDataQuality(item, enterprise, hatchback, vin);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询各项数据质量信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }


    @ApiOperation(value = "条件查询指定时间范围各项数据质量信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "item", value = "数据项标识：00-整车数据，01-驱动电机数据，02-燃料电池，03-发动机数据 04-车辆位置数据，05-极值数据，06-报警数据, 07-可充电储能装置温度数据,08-可充电储能装置电压数据",
                    dataType = "string", paramType = "query", required = true, allowableValues="00,01,02,03,04,05,06"),
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "itemDataQualityInDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryItemDataQualityInDay(String item,
                                            String enterprise,
                                            String hatchback,
                                            String vin,
                                            Long startTime,
                                            Long endTime){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("item", item);
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        logger.info(String.format("条件查询指定时间范围各项数据质量信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryItemDataQualityInDay(item, enterprise, hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("条件查询指定时间范围各项数据质量信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询数据的逻辑校验统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query")
    })
    @RequestMapping(value = "dataLogicCheck", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDataLogicCheck(String enterprise, String hatchback, String vin) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);

        logger.info(String.format("查询数据的逻辑校验统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryDataLogicCheck(enterprise,
                    hatchback, vin);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询数据的逻辑校验统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询指定时间范围数据的逻辑校验统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "dataLogicCheckInDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDataLogicCheckInDay(String enterprise,
                                           String hatchback,
                                           String vin,
                                           Long startTime,
                                           Long endTime) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        logger.info(String.format("查询指定时间范围数据的逻辑校验统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryDataLogicCheckInDay(enterprise,
                    hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询指定时间范围数据的逻辑校验统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "按天聚合指定条件下的数据质量检测各项统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "detectDataQualityByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDetectDataQualityByDay(String enterprise,
                                           String hatchback,
                                           String vin,
                                           Long startTime,
                                           Long endTime) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        logger.info(String.format("按天聚合指定条件下的数据质量检测各项统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Map<String, Object>> data = evsDataDetectResultService.queryDetectDataQualityByDay(enterprise,
                    hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data.size(), data);
        } catch (Exception e) {
            logger.error("按天聚合指定条件下的数据质量检测各项统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "按天聚合指定条件下的数据质量检测统计信息(包含国标检测、报文、逻辑检测, 按车企车型日期分组)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "detectQualityCountByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDetectQualityCountByDay(String enterprise,
                                           String hatchback,
                                           String vin,
                                           Long startTime,
                                           Long endTime,
                                           @RequestParam(defaultValue = "1") int pageNow,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);

        logger.info(String.format("按天聚合指定条件下的数据质量检测统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
           Map<String, Object> data = evsDataDetectResultService.queryDetectQualityCountByDay(enterprise,
                    hatchback, vin, startTime, endTime, pageNow, pageSize);
            res = ResponseUtil.success( data);
        } catch (Exception e) {
            logger.error("按天聚合指定条件下的数据质量检测统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "固定时间范围下指定条件下的数据质量检测统计信息(包含国标检测、报文、逻辑检测，按车企车型分组)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "detectQualityCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDetectQualityCount(String enterprise,
                                           String hatchback,
                                           String vin,
                                           Long startTime,
                                           Long endTime,
                                          @RequestParam(defaultValue = "1") int pageNow,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);

        logger.info(String.format("固定时间范围下指定条件下的数据质量检测统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryDetectQualityCount(enterprise,
                    hatchback, vin, startTime, endTime, pageNow, pageSize);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("固定时间范围下指定条件下的数据质量检测统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "固定时间范围下指定条件下的数据质量检测统计信息(包含国标检测、报文、逻辑检测，按车辆分组)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "vinDetectQualityCount", method = {RequestMethod.GET})
    @ResponseBody
    @LogAudit()
    public Object queryVinDetectQualityCount(String enterprise,
                                             String hatchback,
                                             String vin,
                                             Long startTime,
                                             Long endTime,
                                             @RequestParam(defaultValue = "1") int pageNow,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryVinDetectQualityCount(enterprise,
                    hatchback, vin, startTime, endTime, pageNow, pageSize);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("固定时间范围下指定条件下的数据质量检测统计信息(包含国标检测、报文、逻辑检测，按车辆分组)失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "固定时间范围下汇总所有数据质量检测统计信息(包含国标检测、报文、逻辑检测)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "detectQualityAllCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDetectQualityAllCount(String enterprise,
                                           String hatchback,
                                           String vin,
                                           Long startTime,
                                           Long endTime) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        logger.info(String.format("固定时间范围下汇总所有数据质量检测统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsDataDetectResultService.queryDetectQualityAllCount(enterprise,
                    hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("固定时间范围下汇总所有数据质量检测统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "按天聚合指定条件下的数据逻辑质量检测信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "detectDataLogicQualityByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDetectDataLogicQualityByDay(String enterprise,
                                                   String hatchback,
                                                   String vin,
                                                   Long startTime,
                                                   Long endTime) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        logger.info(String.format("按天聚合指定条件下的数据逻辑质量检测信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Map<String, Object>> data = evsDataDetectResultService.queryDetectDataLogicQualityByDay(enterprise,
                    hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data.size(), data);
        } catch (Exception e) {
            logger.error("按天聚合指定条件下的数据逻辑质量检测信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "按天聚合指定条件下的报文质量检测信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "detectDataMessageQualityByDay", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDetectMessageQualityByDay(String enterprise,
                                                   String hatchback,
                                                   String vin,
                                                   Long startTime,
                                                   Long endTime) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        logger.info(String.format("按天聚合指定条件下的报文质量检测信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Map<String, Object>> data = evsDataDetectResultService.queryDetectMessageQualityByDay(enterprise,
                    hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data.size(), data);
        } catch (Exception e) {
            logger.error("按天聚合指定条件下的报文质量检测信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }


}
