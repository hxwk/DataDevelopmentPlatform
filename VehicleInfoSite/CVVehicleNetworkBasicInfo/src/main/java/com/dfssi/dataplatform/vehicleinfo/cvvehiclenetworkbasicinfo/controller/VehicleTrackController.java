package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleStatusDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.DateUtil;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleStatusService;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleTrackService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆轨迹信息
 * Created by yanghs on 2018/5/8.
 */
@Controller
@RequestMapping(value = "/vehicleTrack")
public class VehicleTrackController {
    @Autowired
    IVehicleTrackService iTrackService;

    @Autowired
    private IVehicleStatusService iVehicleStatusService;

    @ApiOperation(value = "查询车辆当前位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆编号", required = true, defaultValue = "7c4c631ef0bc444684d4d406be17668e", paramType = "query", dataType = "String") })
    @RequestMapping(value = "/currentLocation", method = RequestMethod.GET)
    @ResponseBody
    @LogAudit
    public ResponseObj getCurrentLocation(@RequestParam(value = "vid", required = false, defaultValue = "") String vid)
            throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        Map map= iTrackService.getCurrentLocationsByVid(vid);
        responseObj.setData(map);
        responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        return responseObj;
    }

    @ApiOperation(value = "查询车辆轨迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "车辆编号", required = true, defaultValue = "7c4c631ef0bc444684d4d406be17668e", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "starttime", value = "开始时间", required = true, defaultValue = "2018-2-3 15:59:00", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "interval", value = "时间间隔", required = true, defaultValue = "10", paramType = "query"),
            @ApiImplicitParam(name = "endtime", value = "结束时间", required = true, defaultValue = "2018-2-6 13:5:49", paramType = "query", dataType = "String") })
    @RequestMapping(value = "/track", method = RequestMethod.POST)
    @ResponseBody
    @LogAudit
    public ResponseObj getTrack(@RequestBody String vehicle) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        boolean validParam=false;
        List<String> vehicleList=null;
        String vid=null;
        String starttime=null;
        String endtime=null;
        int interval=10;
        try {
            JSONObject json = JSONObject.parseObject(vehicle);
            vid = json.get("vid") + "";
            starttime = json.get("starttime") + "";
            endtime = json.get("endtime") + "";
            try {
                interval = Integer.valueOf(json.get("interval")+"");
            } catch (NumberFormatException e) {
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败，时间间隔只能为整数");
                return responseObj;
            }
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam){
            if(StringUtils.isEmpty(starttime)||StringUtils.isEmpty(endtime)||StringUtils.isEmpty(vid)){
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "starttime、endtime、vid、interval必传");
                return responseObj;
            }
            List<Map> list =iTrackService.getTrackByVid(vid, DateUtil.getOneDayStartTimeStamp(starttime),
                    DateUtil.getOneDayStartTimeStamp(endtime), interval * 60000);
            HashMap map = new HashMap();
            map.put(Constants.RECORDS, list);
            map.put(Constants.TOTAL,list.size());
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }

        return responseObj;
    }

    @ApiOperation(value = "批量查询车辆当前位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vids", value = "车辆id数组", required = true) })
    @RequestMapping(value = "/currentLocationList", method = RequestMethod.POST)
    @ResponseBody
    @LogAudit
    public ResponseObj getCurrentLocationList(@RequestBody String vids)
            throws Exception {
        boolean validParam=false;
        List<String> vehicleList=null;
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        try {
            JSONObject json = JSONObject.parseObject(vids);
            vehicleList = json.parseArray(json.get("vids") + "", String.class);
            if(vehicleList==null){
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "vids不能为空");
                return responseObj;
            }
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            List<Map> list=  iTrackService.getCurrentLocationList(vehicleList);
            HashMap map = new HashMap();
            map.put(Constants.RECORDS, list);
            map.put(Constants.TOTAL,list.size());
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }
        return responseObj;
    }

    @ApiOperation(value = "批量查询车辆最新位置信息以及在线状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vids", value = "车辆id数组", required = true) })
    @RequestMapping(value = "/getLocationStatusList", method = RequestMethod.POST)
    @ResponseBody
    @LogAudit
    public ResponseObj getLocationStatusList(@RequestBody String vids)
            throws Exception {
        boolean validParam=false;
        List<String> vehicleList=null;
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        try {
            JSONObject json = JSONObject.parseObject(vids);
            vehicleList = json.parseArray(json.get("vids") + "", String.class);
            if(vehicleList==null){
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "vids不能为空");
                return responseObj;
            }
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            List<Map> list=  iTrackService.getCurrentLocationList(vehicleList);
            List<VehicleStatusDTO> statusList=iVehicleStatusService.findStatusInfo(vehicleList);
            for (int i=0;i<list.size();i++){
                for (int j=0;j<statusList.size();j++){
                    if(list.get(i).get("vid").equals(statusList.get(j).getVid())){
                        list.get(i).put("status",statusList.get(j).getStatus());
                    }else{
                        list.get(i).put("status",Constants.NO);
                    }
                }
                vehicleList.remove(list.get(i).get("vid"));
            }
            if (vehicleList.size()>0){
                for (int i=0;i<vehicleList.size();i++){
                    HashMap tempMap=new HashMap();
                    tempMap.put("vid",vehicleList.get(i));
                    tempMap.put("gpsTime",null);
                    tempMap.put("lon",null);
                    tempMap.put("lat",null);
                    tempMap.put("status",Constants.NO);
                    list.add(tempMap);
                }
            }

            HashMap map = new HashMap();
            map.put(Constants.RECORDS, list);
            map.put(Constants.TOTAL,list.size());
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }
        return responseObj;
    }
}
