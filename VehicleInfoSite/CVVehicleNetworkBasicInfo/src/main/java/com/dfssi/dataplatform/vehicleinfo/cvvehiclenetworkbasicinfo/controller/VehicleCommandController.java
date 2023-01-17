package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleCommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * 指令下发执行结果
 * Created by yanghs on 2018/5/24.
 */
@Controller
@RequestMapping(value = "/vehicleCommand")
@Api(value = "VehicleCommandController", description = "指令执行结果信息")
public class VehicleCommandController {
    @Autowired
    private IVehicleCommandService iVehicleCommandService;


    /**
     * 查询指令下发执行结果信息
     * @param vid
     * @param msgId
     * @return
     */
    @RequestMapping(value="/findCommandInfo",method= RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询指令下发执行结果信息")
    @LogAudit
    public ResponseObj findCommandInfo(String vid,String msgId){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        List list= iVehicleCommandService.findCommandInfo(vid,msgId);
        HashMap map = new HashMap();
        map.put(Constants.RECORDS, list);
        map.put(Constants.TOTAL,list.size());
        responseObj.setData(map);
        responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        return responseObj;
    }
}
