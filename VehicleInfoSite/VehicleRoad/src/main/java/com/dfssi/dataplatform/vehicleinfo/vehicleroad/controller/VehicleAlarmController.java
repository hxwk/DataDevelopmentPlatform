package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaAlarmEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaLinkEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IVehicleAlarmService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 车辆报警管理
 * Created by yanghs on 2018/9/12.
 */
@Api(tags = "车辆报警管理", description="车辆告警查询相关接口")
@RestController
@RequestMapping("/vehicleAlarm/")
public class VehicleAlarmController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IVehicleAlarmService vehicleAlarmService;

    /**
     *  车辆报警查询
     * @return
     */
    @ApiOperation(value = "车辆报警查询数据查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆VID, 多个用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "stopTime", value = "结束日期时间戳", dataType = "long",  paramType = "query"),
            @ApiImplicitParam(name = "alarmType", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "alarmDetails", method = {RequestMethod.GET})
    @ResponseBody
    @LogAudit
    public Object queryalarm(String vid,
                             Long startTime,
                             Long stopTime,
                             @RequestParam(defaultValue = "超速报警,违规停车报警,偏航报警,闲置报警,疲劳驾驶报警")String alarmType,
                             @RequestParam(defaultValue = "gcj02") String posType,
                             @RequestParam(defaultValue = "1") int pageNow,
                             @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleAlarmService.queryalarm(vid, startTime, stopTime, alarmType, posType, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("车辆报警查询数据查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    @ApiOperation(value = "实时车辆报警查询数据查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆VID, 多个用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "alarmType", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "realTimeVehicleAlarmDetails", method = {RequestMethod.GET})
    @ResponseBody
    @LogAudit
    public Object queryRealTimeAlarm(String vid,
                                     @RequestParam(defaultValue = "超速报警,违规停车报警,偏航报警,闲置报警,疲劳驾驶报警")String alarmType,
                                     @RequestParam(defaultValue = "gcj02") String posType,
                                     @RequestParam(defaultValue = "1") int pageNow,
                                     @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleAlarmService.queryRealTimeAlarm(vid, alarmType, posType, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("车辆报警查询数据查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }



    @ApiOperation(value = "添加单个区域", notes="json格式传递数据", produces = "application/json")
    @RequestMapping(value = "addAera", method= RequestMethod.POST)
    @LogAudit
    public Object addAera(@Valid @RequestBody AreaAlarmEntity areaAlarmEntity){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.addArea(areaAlarmEntity);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("添加单个区域失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;

    }

    @ApiOperation(value = "添加多个区域", notes="json格式传递数据", produces = "application/json")
    @RequestMapping(value = "addAeras", method= RequestMethod.POST)
    @LogAudit
    public Object addAeras(@Valid @RequestBody List<AreaAlarmEntity> areaAlarmEntities){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.addAreas(areaAlarmEntities);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("添加多个区域失败。", e);
            res = ResponseUtil.error(e.toString());
        }
        return res;
    }


    @ApiOperation(value = "区域删除")
    @ApiImplicitParam(name = "id", value = "区域ID", dataType = "String", required = true, paramType = "query")
    @RequestMapping(value = "deleteArea", method = {RequestMethod.GET})
    @ResponseBody
    public Object deleteAera(@RequestParam String id){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.deleteArea(id);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("区域删除失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;

    }

    @ApiOperation(value = "更新区域", notes="json格式传递数据", produces = "application/json")
    @RequestMapping(value = "updateAera", method= RequestMethod.POST)
    @LogAudit
    public Object updateAera(@Valid @RequestBody AreaAlarmEntity areaAlarmEntity){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.updatyeArea(areaAlarmEntity);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("更新区域。", e);
            res = ResponseUtil.error(e.toString());
        }
        return res;
    }

    @ApiOperation(value = "区域查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "区域id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "区域名称，支持模糊匹配", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "区域类型，0表示违停区域，1表示偏航区域",
                    dataType = "int", allowableValues= "0,1", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "queryAreas", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryAreas(String id,
                             String name,
                             Integer type,
                             @RequestParam(defaultValue = "1") int pageNow,
                             @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleAlarmService.queryAreas(id, name, type, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("区域查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;

    }



    /** 连接查询相关  */

    @ApiOperation(value = "添加单个连接", notes="json格式传递数据", produces = "application/json")
    @RequestMapping(value = "addLink", method= RequestMethod.POST)
    @LogAudit
    public Object addLink(@Valid @RequestBody AreaLinkEntity linkEntity){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.addLink(linkEntity);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("添加单个连接。", e);
            res = ResponseUtil.error(e.getMessage());
        }

        return res;

    }

    @ApiOperation(value = "添加多个连接", notes="json格式传递数据", produces = "application/json")
    @RequestMapping(value = "addLinks", method= RequestMethod.POST)
    @LogAudit
    public Object addLinks(@Valid @RequestBody List<AreaLinkEntity> linkEntities){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.addLinks(linkEntities);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("添加多个连接失败。", e);
            res = ResponseUtil.error(e.toString());
        }
        return res;
    }


    @ApiOperation(value = "删除连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "连接ID", dataType = "string", required = true, paramType = "query")
    })
    @RequestMapping(value = "deleteLink", method = {RequestMethod.GET})
    @ResponseBody
    public Object deleteLink(@RequestParam String id, @RequestParam String vid){

        Map<String, Object> res;
        try {
            int i = vehicleAlarmService.deleteLink(vid, id);
            res = ResponseUtil.success(i);
        } catch (Exception e) {
            logger.error("连接删除失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;

    }


    @ApiOperation(value = "连接区域查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆vid", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "区域类型，0表示违停区域，1表示偏航区域",
                    dataType = "int", allowableValues= "0,1", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "queryLinkAreas", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryLinkAreas(String vid,
                                 Integer type,
                                 @RequestParam(defaultValue = "1") int pageNow,
                                 @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleAlarmService.queryLinkAreas(vid, type, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("连接区域查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;

    }

    @ApiOperation(value = "连接查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "连接id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vid", value = "车辆Vid", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "areaId", value = "区域ID", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "areaType", value = "区域类型，0表示违停区域，1表示偏航区域",
                    dataType = "int", allowableValues= "0,1", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "queryLinks", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryLinks(String id,
                             String vid,
                             String areaId,
                             Integer areaType,
                             @RequestParam(defaultValue = "1") int pageNow,
                             @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleAlarmService.queryLinks(id, vid, areaId, areaType, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("连接查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;

    }


}
