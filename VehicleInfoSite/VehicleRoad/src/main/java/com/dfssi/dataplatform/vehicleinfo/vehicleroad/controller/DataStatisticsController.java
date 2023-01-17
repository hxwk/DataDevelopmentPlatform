package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.HistoryDataQueryEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IDataStatisticsService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ResponseUtil;
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
 * 数据统计分析
 * Created by yanghs on 2018/9/12.
 */
@Api(tags = "数据统计分析", description="历史数据查询以及导出等接口")
@RestController
@RequestMapping("/dataStatistics/")
public class DataStatisticsController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private IDataStatisticsService dataStatisticsService;


    @ApiOperation(value = "历史数据查询或导出", notes="json格式传递数据", produces = "application/json")
    @RequestMapping(value = "historyData", method= RequestMethod.POST)
    @LogAudit
   public Object queryHistoryData(@RequestBody HistoryDataQueryEntity historyDataQueryEntity){

        Map<String, Object> res;
        try {
            if(historyDataQueryEntity.isExport()) {
                Map<String, Object> map = dataStatisticsService.exportHistoryData(historyDataQueryEntity);
                if(map.containsKey("error")){
                    res = ResponseUtil.error(map);
                }else{
                    res = ResponseUtil.success(map);
                }
            }else{
                Map<String, Object> map = dataStatisticsService.queryHistoryData(historyDataQueryEntity);
                res = ResponseUtil.success(map);
            }
        } catch (Exception e) {
            logger.error(String.format("查询或导出历史数据失败，参数： %s", historyDataQueryEntity), e);
            res = ResponseUtil.error(e.toString());
        }
       return res;
   }

    @ApiOperation(value = "参数标准化字段列举")
    @ApiImplicitParam(name = "label", value = "字段中文标签, 支持模糊查询", dataType = "string",  paramType = "query")
    @RequestMapping(value = "allFields", method= RequestMethod.GET)
    @LogAudit
   public Object queryAllFields(String label){

        Map<String, Object> res;
        try {
            List<Map<String, Object>> allFields = dataStatisticsService.findAllFields(label);
            res = ResponseUtil.success(allFields.size(), allFields);

        } catch (Exception e) {
            logger.error("参数标准化字段列举失败", e);
            res = ResponseUtil.error(e.toString());
        }
       return res;
   }



    @ApiOperation(value = "统计信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid，多个使用英文逗号分隔", dataType = "string",  paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳, 毫秒", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stopTime", value = "结束日期时间戳, 毫秒", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "查询统计类型, 按天或按月统计", dataType = "String", allowableValues = "day,month",  defaultValue = "day", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @LogAudit
    @RequestMapping(value = "statistics", method= RequestMethod.GET)
    @ResponseBody
    public Object queryStatistics(String vid,
                                       @RequestParam Long startTime,
                                       @RequestParam Long stopTime,
                                       @RequestParam(defaultValue = "day") String type,
                                       @RequestParam(defaultValue = "1") int pageNow,
                                       @RequestParam(defaultValue = "10") int pageSize){
        Map<String, Object> res;
        try {
            Map<String, Object> map;
            switch (type){
                case "month":
                    map = dataStatisticsService.queryStatisticsByMonth(vid, startTime, stopTime, pageNow, pageSize);
                    break;
                default:
                    map = dataStatisticsService.queryStatisticsByDay(vid, startTime, stopTime, pageNow, pageSize);
            }
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("统计信息查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }
        return res;
    }

    @ApiOperation(value = "行程信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid，多个使用英文逗号分隔", dataType = "string",  paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳, 毫秒", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stopTime", value = "结束日期时间戳, 毫秒", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @LogAudit
    @RequestMapping(value = "trip", method= RequestMethod.GET)
    @ResponseBody
    public Object queryStatisticsTrip(String vid,
                                      @RequestParam Long startTime,
                                      @RequestParam Long stopTime,
                                      @RequestParam(defaultValue = "1") int pageNow,
                                      @RequestParam(defaultValue = "10") int pageSize){
        Map<String, Object> res;
        try {
            Map<String, Object> map = dataStatisticsService.queryStatisticsTrip(vid, startTime, stopTime, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("统计信息查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }
        return res;
    }
}
