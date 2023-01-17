package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.annotation.LogAudit;
import com.dfssi.dataplatform.cache.result.FeatureAnalysisResultCache;
import com.dfssi.dataplatform.service.FeatureAnalysisService;
import com.dfssi.dataplatform.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Description:
 *   特征分析子专题 控制器
 * @author LiXiaoCong
 * @version 2018/7/23 19:06
 */
@Api(tags = {"新能源特征分析子专题查询控制器"})
@RestController
@RequestMapping(value="/feature/analysis/")
public class FeatureAnalysisController {
    private final Logger logger = LoggerFactory.getLogger(FeatureAnalysisController.class);

    @Autowired
    private FeatureAnalysisService featureAnalysisService;

    @Autowired
    private FeatureAnalysisResultCache FeatureAnalysisResultCache;

    @LogAudit
    @ApiOperation(value = "电动汽车用户出行基本信息", notes = "1: 纯电动BEV, 2:混动PHEV")
    @RequestMapping(value = "basicInformation", method = {RequestMethod.GET})
    @ResponseBody
    public Object basicInformation(){
        Map<String, Object> res;
        try {
            Object o = FeatureAnalysisResultCache.basicInformation();
            res = ResponseUtil.success(o);
        } catch (Exception e) {
            logger.error("查询电动汽车用户出行基本信息失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }
        return res;
    }

    @LogAudit
    @ApiOperation(value = "单次行驶时长统计", notes = "1: 纯电动BEV, 2:混动PHEV, \n" +
            "时长步长为1h，范围计算公式为：[n * 1h, (n + 1) * 1h)， 例如：'0': 0 表示时长级别为0的有0个，即运行时长在[0, 1h)的车辆数为0。\n" +
            "一共存在13个级别， 最后一个级别为12，表示[12, ), 即运行时长不小于12小时的车辆数目统计。")
    @RequestMapping(value = "tripTimeLevelCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object tripTimeLevelCount(){
        Map<String, Object> res;
        try {
            Object o = FeatureAnalysisResultCache.tripTimeLevelCount();
            res = ResponseUtil.success(o);
        } catch (Exception e) {
            logger.error("查询单次行驶时长统计失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }
        return res;
    }

    @LogAudit
    @ApiOperation(value = "单日行驶时长统计", notes = "1: 纯电动BEV, 2:混动PHEV, \n" +
            "时长步长为1h，范围计算公式为：[n * 1h, (n + 1) * 1h)， 例如：'0': 0 表示时长级别为0的有0个，即运行时长在[0, 1h)的车辆数为0。\n" +
            "一共存在13个级别， 最后一个级别为12，表示[12, ), 即运行时长不小于12小时的车辆数目统计。")
    @RequestMapping(value = "dayTimeLevelCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object dayTimeLevelCount(){
        Map<String, Object> res;
        try {
            Object o = FeatureAnalysisResultCache.dayTimeLevelCount();
            res = ResponseUtil.success(o);
        } catch (Exception e) {
            logger.error("查询单日行驶时长统计失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }
        return res;
    }

    @LogAudit
    @ApiOperation(value = " 驾驶开始soc统计",  notes = "1: 纯电动BEV, 2:混动PHEV, \n" +
            "soc步长为10%，范围计算公式为：[n * 10%, (n + 1) * 10%)， 例如：'0': 0 表示soc级别为0的有0个，即驾驶开始时soc在[0, 10%)的车辆数为0。\n" +
            "一共存在10个级别， 最后一个级别为9，表示[90%, 100%], 即驾驶开始时soc在[90%, 100%]的车辆数。")
    @RequestMapping(value = "tripStartSocCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object tripStartSocCount(){
        Map<String, Object> res;
        try {
            Object o = FeatureAnalysisResultCache.tripStartSocCount();
            res = ResponseUtil.success(o);
        } catch (Exception e) {
            logger.error("查询驾驶开始soc统计失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }
        return res;
    }

    @LogAudit
    @ApiOperation(value = " 驾驶结束soc统计", notes = "1: 纯电动BEV, 2:混动PHEV, \n" +
            "soc步长为10%，范围计算公式为：[n * 10%, (n + 1) * 10%)， 例如：'0': 0 表示soc级别为0的有0个，即驾驶结束时soc在[0, 10%)的车辆数为0。\n" +
            "一共存在10个级别， 最后一个级别为9，表示[90%, 100%], 即驾驶结束时soc在[90%, 100%]的车辆数。")
    @RequestMapping(value = "tripStopSocCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object tripStopSocCount(){
        Map<String, Object> res;
        try {
            Object o = FeatureAnalysisResultCache.tripStopSocCount();
            res = ResponseUtil.success(o);
        } catch (Exception e) {
            logger.error("查询驾驶结束soc统计失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }
        return res;
    }

    @LogAudit
    @ApiOperation(value = " 全天行驶时长分布统计",notes = "1: 纯电动BEV, 2:混动PHEV, \n" +
            "时间段步长为3h，范围计算公式为：[n * 3h, (n + 1) * 3h)， 例如：'3': 0 表示时间段级别为0的有0个，即时间段在[09:00, 12:00)的车辆数为0。\n" +
            "一共存在8个级别， 最后一个级别为7，表示[21:00, 23:59)。")
    @RequestMapping(value = "drivingTimeDistributeCount", method = {RequestMethod.GET})
    @ResponseBody
    public Object drivingTimeDistributeCount(){
        Map<String, Object> res;
        try {
            Object o = FeatureAnalysisResultCache.drivingTimeDistributeCount();
            res = ResponseUtil.success(o);
        } catch (Exception e) {
            logger.error("查询全天行驶时长分布统计失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }
        return res;
    }


    @LogAudit
    @ApiOperation(value = " 车辆出省/市情况监控")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "timePoint", value = "时间点", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "outBoundsVehicles", method = {RequestMethod.GET})
    @ResponseBody
    public Object outBoundsVehicles(Long timePoint,
                                    @RequestParam(defaultValue = "1") int pageNow,
                                    @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = featureAnalysisService.outBoundsVehicles(timePoint, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询车辆出省/市情况监控失败。", e);
            res = ResponseUtil.error(e.getMessage());
        }

        return res;
    }


}
