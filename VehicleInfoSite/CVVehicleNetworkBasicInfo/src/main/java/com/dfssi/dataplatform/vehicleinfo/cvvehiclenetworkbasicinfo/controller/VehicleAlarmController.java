package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleAlarmDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleAlarmService;
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
import java.util.List;

/**
 * 车辆报警信息
 * Created by yanghs on 2018/5/8.
 */
@Controller
@RequestMapping(value = "/vehicleAlarm")
@Api(value = "VehicleAlarmController", description = "车辆报警信息")
public class VehicleAlarmController {

    @Autowired
    private IVehicleAlarmService iVehicleAlarmService;


    /**
     * 查询车辆报警信息
     * @param vehicles
     * @return
     */
    @RequestMapping(value="/findAlarmInfo",method= RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询车辆报警信息")
    @LogAudit
    public ResponseObj findAlarmInfo(@RequestBody String vehicles){

        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        boolean validParam=false;
        List<String> vidList=null;
        String startTime =null;
        String endTime =null;
        try {
            JSONObject json = JSONObject.parseObject(vehicles);
            vidList = json.parseArray(json.get("vids")+"", String.class );
            startTime = json.get("startTime")+"";
            endTime = json.get("endTime")+"";
            if (StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)){
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "startTime、endTime参数必传");
                return responseObj;
            }
            validParam = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            List<VehicleAlarmDTO> list=iVehicleAlarmService.findAlarmInfo(vidList,startTime,endTime);
            HashMap map = new HashMap();
            map.put(Constants.RECORDS, list);
            map.put(Constants.TOTAL, list.size());
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{

            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }

        return responseObj;
    }
}
