package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.entity.database.Mileage;
import com.dfssi.dataplatform.service.EvsMileageVerifyService;
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
 *    里程核查相关
 * @author LiXiaoCong
 * @version 2018/5/28 20:26
 */
@Api(tags = {"新能源里程核查控制器"})
@RestController
@RequestMapping(value="/mile/verify/")
public class EvsMileageVerifyController {
    private final Logger logger = LoggerFactory.getLogger(EvsMileageVerifyController.class);

    @Autowired
    private EvsMileageVerifyService evsMileageVerifyService;


    @ApiOperation(value = "指定vin以及时间范围进行里程核查")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆vin,不支持多个", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "starttime", value = "核查开始时间戳", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endtime", value = "核查结束时间错", dataType = "long",  required = true, paramType = "query")
    })
    @RequestMapping(value = "vehicleVerify", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchMileageVerify(@RequestParam String vin,
                                      @RequestParam long starttime,
                                      @RequestParam long endtime){

        Map<String, Object> param = Maps.newHashMap();
        param.put("vin", vin);
        param.put("starttime", starttime);
        param.put("endtime", endtime);
        logger.info(String.format("指定vin以及时间范围进行里程核查信息，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            List<Mileage> maps = evsMileageVerifyService.searchMileageVerify(vin, starttime, endtime);
            res = ResponseUtil.success(maps.size(), maps);
        } catch (Exception e) {
            logger.error("指定vin以及时间范围进行里程核查信息失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }

}
