package com.dfssi.dataplatform.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.annotation.LogAudit;
import com.dfssi.dataplatform.service.EvsSnCheckService;
import com.dfssi.dataplatform.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 流水号检测
 * Created by yanghs on 2018/5/30.
 */
@Controller
@RequestMapping("/snCheck")
@Api(tags = {"新能源平台流水号检测"})
public class EvsSnCheckController {
    private final Logger logger = LoggerFactory.getLogger(EvsSnCheckController.class);

    @Autowired
    private EvsSnCheckService evsSnCheckService;


    @ApiOperation(value = "新能源平台流水号检测查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vins", value = "vin数组"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间")
    })
    @RequestMapping(value = "/findSnCheckInfo", method = RequestMethod.POST)
    @ResponseBody
    @LogAudit
    public Object findSnCheckInfo(@RequestBody String vehicles){
        Map<String, Object> res;
        List<String> vinList=null;
        String startTime=null;
        String endTime=null;
        boolean validParam=false;
        try {
            JSONObject json = JSONObject.parseObject(vehicles);
            vinList = json.parseArray(json.get("vins")+"", String.class );
            startTime = json.get("startTime")+"";
            endTime = json.get("endTime")+"";
            if (StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)){
                res= ResponseUtil.error("startTime、endTime参数必传");
                return res;
            }
            validParam = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            try {
                Map<String, Object> data = evsSnCheckService.findSnCheckInfo(vinList, startTime, endTime);
                res = ResponseUtil.success(data);
            } catch (Exception e) {
                logger.error("查询平台流水号检测信息失败。", e);
                res = ResponseUtil.error(e);
            }
        }else {
            res= ResponseUtil.error("参数解析失败");
        }

        return res;
    }
}
