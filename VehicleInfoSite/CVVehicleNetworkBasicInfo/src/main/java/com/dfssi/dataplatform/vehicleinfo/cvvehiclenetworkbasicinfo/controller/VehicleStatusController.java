package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleStatusDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * 车辆实时状态信息
 * Created by yanghs on 2018/5/8.
 */
@Controller
@RequestMapping(value = "/vehicleStatus")
@Api(value = "VehicleStatusController", description = "车辆实时状态信息")
public class VehicleStatusController {

    @Autowired
    private IVehicleStatusService iVehicleStatusService;


    /**
     * 通过vid查询车辆状态信息
     * @param vids
     * @return
     */
    @RequestMapping(value="/findStatusInfo",method= RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询车辆实时状态信息")
    @LogAudit
    public ResponseObj findStatusInfo(@RequestBody String vids){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        List<String> vidList = null;
        boolean validParam=false;
        try {
            JSONObject json = JSONObject.parseObject(vids);
            vidList = json.parseArray(json.get("vids")+"", String.class);
            validParam=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam){
            if(vidList.size()==0){
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "vids不能为空");
                return responseObj;
            }
            List<VehicleStatusDTO> list= iVehicleStatusService.findStatusInfo(vidList);
            HashMap map = new HashMap();
            map.put(Constants.RECORDS, list);
            map.put(Constants.TOTAL,list.size());
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }

        return responseObj;
    }
}
