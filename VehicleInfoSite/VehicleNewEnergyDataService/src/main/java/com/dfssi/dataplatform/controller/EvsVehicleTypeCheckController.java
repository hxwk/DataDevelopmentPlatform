package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.service.EvsVehicleTypeCheckService;
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

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/29 15:13
 */
@Api(tags = {"车型联调质量检测查询控制器"})
@RestController
@RequestMapping(value="/vehicle/")
public class EvsVehicleTypeCheckController {

    private final Logger logger = LoggerFactory.getLogger(EvsVehicleTypeCheckController.class);


    @Autowired
    private EvsVehicleTypeCheckService evsVehicleTypeCheckService;


    @ApiOperation(value = "查询车型联调质量检测统计",
            notes = "(reason)错误原因： 0 无数据，1 错误率过高，2 数据缺失， 3 错误率过高和数据缺失均存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprise", value = "车企, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "hatchback", value = "车型, 多个使用英文逗号分隔", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vin", value = "车辆VIN, 多个使用英文逗号分隔", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间戳", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间戳", required = true, dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "vehicleTypeCheck", method = {RequestMethod.GET})
    @ResponseBody
    public Object countDectectResult(String enterprise,
                                     String hatchback,
                                     @RequestParam String vin,
                                     @RequestParam Long startTime,
                                     @RequestParam Long endTime){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprise", enterprise);
        param.put("hatchback", hatchback);
        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        logger.info(String.format("查询车型联调质量检测统计信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Map<String, Object> data = evsVehicleTypeCheckService.countDectectResult(enterprise, hatchback, vin, startTime, endTime);
            res = ResponseUtil.success(data);
        } catch (Exception e) {
            logger.error("查询车型联调质量检测统计信息失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

}
