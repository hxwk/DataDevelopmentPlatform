package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.annotation.LogAudit;
import com.dfssi.dataplatform.service.EvsElasticSearchDataService;
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
 * @version 2018/4/26 19:26
 */
@Api(tags = {"新能源数据质量错误详情es查询控制器"})
@RestController
@RequestMapping(value="/detect/detail/")
public class EvsDataElasticSearchController {
    private final Logger logger = LoggerFactory.getLogger(EvsDataElasticSearchController.class);

    @Autowired
    private EvsElasticSearchDataService evsElasticSearchDataService;

    @ApiOperation(value = "查询数据质量错误详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmTypeName", value = "告警类型名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmContent", value = "告警内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "detectErrDetail", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchDetectErrDetail(String enterprises,
                                        String hatchbacks,
                                        String vins,
                                        String alarmTypeName,
                                        String alarmContent,
                                        Long startTime,
                                        Long endTime,
                                        @RequestParam(defaultValue = "1") int pageNow,
                                        @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("alarmTypeName", alarmTypeName);
        param.put("alarmContent", alarmContent);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询数据质量错误详情，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

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
            Map<String, Object> map = evsElasticSearchDataService.searchDetectErrDetail(enterprise, hatchback, vin,
                    alarmTypeName, alarmContent, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询数据质量错误详情失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询数据质量逻辑错误详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmTypeName", value = "告警类型名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmContent", value = "告警内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "logicDetectErrDetail", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchLogicDetectErrDetail(String enterprises,
                                        String hatchbacks,
                                        String vins,
                                        String alarmTypeName,
                                        String alarmContent,
                                        Long startTime,
                                        Long endTime,
                                        @RequestParam(defaultValue = "1") int pageNow,
                                        @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("alarmTypeName", alarmTypeName);
        param.put("alarmContent", alarmContent);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询数据质量逻辑错误详情，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

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
            Map<String, Object> map = evsElasticSearchDataService.searchLogicDetectErrDetail(enterprise, hatchback, vin,
                    alarmTypeName, alarmContent, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询数据质量逻辑错误详情失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询报文错误详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmTypeName", value = "告警类型名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmContent", value = "告警内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "messageErrDetail", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchMessageDetectErrDetail(String enterprises,
                                        String hatchbacks,
                                        String vins,
                                        String alarmTypeName,
                                        String alarmContent,
                                        Long startTime,
                                        Long endTime,
                                        @RequestParam(defaultValue = "1") int pageNow,
                                        @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("alarmTypeName", alarmTypeName);
        param.put("alarmContent", alarmContent);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询报文错误详情，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

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
            Map<String, Object> map = evsElasticSearchDataService.searchMessageDetectErrDetail(enterprise, hatchback, vin,
                    alarmTypeName, alarmContent, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询报文错误详情失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询车辆告警统计详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmLevel", value = "告警级别", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "alarmContent", value = "告警内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "handleStatus", value = "处理状态", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "vehcleAlarms", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchVehcleAlarms(String enterprises,
                                          String hatchbacks,
                                          String vins,
                                          Integer alarmLevel,
                                          String alarmContent,
                                          Integer handleStatus,
                                          Long startTime,
                                          Long endTime,
                                          @RequestParam(defaultValue = "1") int pageNow,
                                          @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("alarmLevel", alarmLevel);
        param.put("alarmContent", alarmContent);
        param.put("handleStatus", handleStatus);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询车辆告警统计详情，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
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
            Map<String, Object> map = evsElasticSearchDataService
                    .searchVehicleAlarms(enterprise, hatchback, vin, alarmLevel,
                            alarmContent, handleStatus, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询车辆告警统计详情失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "车辆告警处理信息批量存入ES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vehcleAlarms", value = "车辆告警处理信息报文")
    })
    @RequestMapping(value = "handleStatus2Es", method = {RequestMethod.POST})
    @ResponseBody
    public Object handleStatus2Es (@RequestBody String vehcleAlarms) {

        Map<String, Object> res;
        try {
            if (vehcleAlarms == null) {
                throw new Exception("要存入的车辆告警处理信息数据为空");
            }

            evsElasticSearchDataService.handleStatus2Es(vehcleAlarms);
            res = ResponseUtil.success("车辆告警处理信息批量存入ES成功。");
        } catch (Exception e) {
            logger.error("车辆告警处理信息批量存入ES失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "按告车辆警级别统计车辆告警详情数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query")
    })
    @RequestMapping(value = "warningCount", method = {RequestMethod.GET})
    @ResponseBody
    @LogAudit
    public Object countWarningTotal(String enterprises,
                                          String hatchbacks,
                                          String vins,
                                          Long startTime,
                                          Long endTime){

        Map<String, Object> res;
        try {
            String[] enterprise = (enterprises != null) ?
                    enterprises.split(",") : null;
            String[] hatchback = (hatchbacks != null) ?
                    hatchbacks.split(",") : null;
            String[] vin = (vins != null) ?
                    vins.split(",") : null;


            Map<String, Object> map = evsElasticSearchDataService
                    .countVehicleAlarmsWithCondition(enterprise, hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("按告车辆警级别统计车辆告警详情数据失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询车辆告警详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "alarmLevel", value = "告警级别", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "alarmContent", value = "告警内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "vehcleAlarmDetail", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchVehcleAlarmDetail(String enterprises,
                                          String hatchbacks,
                                          String vins,
                                          Integer alarmLevel,
                                          String alarmContent,
                                          Long startTime,
                                          Long endTime,
                                          @RequestParam(defaultValue = "1") int pageNow,
                                          @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("alarmLevel", alarmLevel);
        param.put("alarmContent", alarmContent);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询车辆告警详情列表，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
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
            Map<String, Object> map = evsElasticSearchDataService
                    .searchVehicleAlarmDetail(enterprise, hatchback, vin,
                            alarmLevel, alarmContent, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询车辆告警详情列表失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }
}
