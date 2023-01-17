package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.PeriodicReportDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehiclePeriodicReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * 车辆实时信息周期上报
 * Created by yanghs on 2018/5/8.
 */
@Controller
@RequestMapping(value = "/periodicReport")
@Api(value = "VehiclePeriodicReportController", description = "车辆实时信息周期上报")
public class VehiclePeriodicReportController {

    @Autowired
    private IVehiclePeriodicReportService iVehiclePeriodicReportService;


    /**
     * 查询车辆周期上报信息
     * @param param
     * @return
     */
    @RequestMapping(value="/findPeriodicReportInfo",method= RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询车辆周期上报信息")
    @LogAudit
    public ResponseObj findPeriodicReportInfo(@RequestBody String param){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        boolean validParam=false;
        String vid=null;
        responseObj.setData(new HashMap());
        try {
            JSONObject json = JSONObject.parseObject(param);
            vid = json.get("vid")+"";
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            if (StringUtils.isEmpty(vid)){
                responseObj.setStatus(ResponseObj.CODE_FAIL_P,Constants.FAIL,"参数必传");
                return responseObj;
            }
            PeriodicReportDTO periodicReportDTO=iVehiclePeriodicReportService.findPeriodicReportInfo(vid);
            if(periodicReportDTO!=null){
                responseObj.setData(periodicReportDTO);
            }
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_P,Constants.FAIL,"参数解析失败");
        }
        return responseObj;
    }
}
