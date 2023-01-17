package com.dfssi.dataplatform.chargingPile.controller;

import com.dfssi.dataplatform.chargingPile.service.ExternalOperatorService;
import com.dfssi.dataplatform.common.RedisPoolManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Description 所有和外部交互的接口
 *
 * @author bin.Y
 * @version 2018/5/29 15:51
 */
@Api(tags = {"充电桩外部交互接口"})
@Controller
@RequestMapping(value = "/external")
public class ExternalOperatorController {
    @Autowired
    private ExternalOperatorService externalOperatorService;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation(value = "充电站信息变化推送，地方平台实现，运营商调用。  ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationInfo", value = "站点信息报文"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/notificationStationInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object notificationStationInfo(HttpServletRequest request,@RequestBody String stationInfo) throws  Exception {
        System.out.println("~~~~~~~~~~~~~~~~~~enter notificationStationInfo~~~~~~~~~~~~~~~~");
        System.out.println(stationInfo.toString());
        Object result = externalOperatorService.insertNotificationStationInfo(request,stationInfo);
        return result;
    }


    @ApiOperation(value = "查询充电站信息并入库，运营商实现，地方平台调用。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "LastQueryTime", value = "上次查询时间"/*, dataType = "String", paramType = "query"*/),
            @ApiImplicitParam(name = "PageNo", value = "查询页码"/*, dataType = "String", paramType = "query"*/),
            @ApiImplicitParam(name = "PageSize", value = "每页数量"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/queryStationsInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object queryStationsInfo(@RequestParam("lastQueryTime") String lastQueryTime, @RequestParam("pageNo") String pageNo, @RequestParam("pageSize") String pageSize, @RequestParam("operatorId") String operatorId) throws Exception {
        Object obj=externalOperatorService.queryStationsInfo(lastQueryTime,pageNo,pageSize,operatorId);
//        return "有疑问,上次查询时间指的是啥";
        return obj;
    }


    @ApiOperation(value = "设备状态变化推送，地方平台实现，运营商调用。  ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ConnectorStatusInfo", value = "充电设备接口状态报文"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/notificationStationStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object  notificationStationStatus (HttpServletRequest request,@RequestBody String ConnectorStatusInfo) throws Exception {
        logger.info("接收接口的最新状态:"+ConnectorStatusInfo);
        Object object = externalOperatorService.notificationStationStatus(request,ConnectorStatusInfo);
        return object;
    }


    @ApiOperation(value = " 设备接口状态查询，运营商实现，地方平台调用。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "StationIDs ", value = "充电桩ID列表"/*, dataType = "String", paramType = "query"*/),
    })
    @RequestMapping(value = "/queryStationStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object queryStationStatus(@RequestParam("StationIDs") String[] StationIDs, @RequestParam("operatorId") String operatorId) throws Exception {
        Object obj = externalOperatorService.queryStationStatus(StationIDs,operatorId);
        return obj;
    }


    @ApiOperation(value = " 查询统计信息 ，运营商实现，地方平台调用。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "StationID", value = "充电站ID"/*, dataType = "String", paramType = "query"*/),
            @ApiImplicitParam(name = "StartTime", value = "统计开始时间"/*, dataType = "String", paramType = "query"*/),
            @ApiImplicitParam(name = "EndTime", value = "统计结束时间"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/queryStationStats", method = RequestMethod.POST)
    @ResponseBody
    public Object queryStationStats(@RequestParam("StationID") String StationID,
                                    @RequestParam("StartTime") String StartTime,
                                    @RequestParam("EndTime") String EndTime) throws Exception {
        Object obj = externalOperatorService.queryStationStats(StationID,StartTime,EndTime);
        return obj;
    }

    @ApiOperation(value = "订单信息推送，地方平台实现，运营商调用。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "OrderInfo", value = "订单信息"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/notificationOrderInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object  notificationOrderInfo (HttpServletRequest request,@RequestBody String OrderInfo) throws Exception {
        logger.info("接收推送的订单信息:"+OrderInfo);
        Object object = externalOperatorService.notificationOrderInfo(request,OrderInfo);
        return object;
    }
    @ApiOperation(value = "获取token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json", value = "消息主体"/*, dataType = "String", paramType = "query"*/)
    })
    @RequestMapping(value = "/query_token", method = RequestMethod.POST)
    @ResponseBody
    public Object queryToken(HttpServletRequest request,@RequestBody String json) throws Exception{
        logger.info("获取token:"+json);
        return externalOperatorService.queryToken(json);
    }
}
