package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IVehicleFaultService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ResponseUtil;
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
 * 车辆故障管理
 * Created by yanghs on 2018/9/12.
 */
@Api(tags = "车辆故障管理", description="故障查询相关接口")
@RestController
@RequestMapping("/vehicleFault/")
public class VehicleFaultController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IVehicleFaultService vehicleFaultService;

    /**
     *  车辆故障查询
     * @return
     */
    @ApiOperation(value = "车辆故障查询数据查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆VID, 多个用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "stopTime", value = "结束日期时间戳", dataType = "long",  paramType = "query"),
            @ApiImplicitParam(name = "faultType", value = "故障类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "spn", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "fmi", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sa", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "faultDetails", method = {RequestMethod.GET})
    @ResponseBody
    @LogAudit
    public Object queryfault(String vid,
                             Long startTime,
                             Long stopTime,
                             String faultType,
                             Integer spn,
                             Integer fmi,
                             Integer sa,
                             @RequestParam(defaultValue = "gcj02") String posType,
                             @RequestParam(defaultValue = "1") int pageNow,
                             @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleFaultService.queryFault(vid, startTime, stopTime, faultType, spn, fmi, sa, posType, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("车辆故障查询数据查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

    /**
     *  车辆故障查询
     * @return
     */
    @ApiOperation(value = "实时车辆故障查询数据查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆VID, 多个用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "faultType", value = "故障类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "spn", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "fmi", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sa", value = "告警类型, 多个用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "realTimeVehicleFaultDetails", method = {RequestMethod.GET})
    @ResponseBody
    @LogAudit
    public Object queryRealTimeFault(String vid,
                                     String faultType,
                                     Integer spn,
                                     Integer fmi,
                                     Integer sa,
                                     @RequestParam(defaultValue = "gcj02") String posType,
                                     @RequestParam(defaultValue = "1") int pageNow,
                                     @RequestParam(defaultValue = "10") int pageSize){

        Map<String, Object> res;
        try {
            Map<String, Object> map = vehicleFaultService.queryRealTimeFault(vid,  faultType, spn, fmi, sa, posType, pageNow, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("实时车辆故障查询数据查询失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }
}
