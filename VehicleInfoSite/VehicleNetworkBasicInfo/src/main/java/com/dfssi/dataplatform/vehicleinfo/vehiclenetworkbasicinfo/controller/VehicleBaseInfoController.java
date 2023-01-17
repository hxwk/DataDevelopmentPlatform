package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.IVehicleBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 车辆基础信息接入
 *
 * @author yanghs
 * @since 2018-4-2 11:11:32
 */
@Controller
@RequestMapping(value = "/vehicleBaseInfo")
@Api(value = "vehicleBaseInfoController", description = "新能源车辆基础信息接入")
public class VehicleBaseInfoController {

    @Autowired
    private IVehicleBaseInfoService vehicleService;

    /**
     * 车辆基础信息手动录入
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "车辆基本信息单条记录保存", notes = "vin、iccId、vehicleType、userId、vehicleCompany、appId为必填项,车牌号plateNo可选填", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iccId", value = "SIM卡卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleType", value = "车型", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleCompany", value = "车企", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appId", value = "应用id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "plateNo", value = "车牌号", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj saveVehicleBaseInfo(VehicleDTO vehicleDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        Map map = new HashMap();
        map.put("vin", vehicleDTO.getVin());
        responseObj.setData(map);
        String msg = queryValidateData(vehicleDTO);
        msg=msg+validateData(vehicleDTO);
        if (StringUtils.isNotEmpty(msg)) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, msg);
        } else {
            String saveResult = vehicleService.saveVehicleBaseInfo(vehicleDTO);//保存数据到geode
            if (StringUtils.isEmpty(saveResult)) {
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
            } else {
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, saveResult);
            }
        }
        return responseObj;
    }


    /**
     * 车辆基础信息查询
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findVehicleBaseInfo",method= RequestMethod.GET)
    @ApiOperation(value = "查询车辆基本信息", notes = "参数vin、iccId、userId、appId、plateNO,vehicleType,vehicleCompany,其中vin、iccId必传", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iccId", value = "SIM卡卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleType", value = "车型", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleCompany", value = "车企", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appId", value = "应用id", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "plateNo", value = "车牌号", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj findVehicleBaseInfo(VehicleDTO vehicleDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String msg=queryValidateData(vehicleDTO);
        if(StringUtils.isEmpty(msg)){
            int count = vehicleService.findVehicleCount(vehicleDTO);
            List<VehicleDTO> vehicleList = new ArrayList<>();
            if (count > 0) {
                vehicleList = vehicleService.findVehicleBaseInfo(vehicleDTO, null);
            }
            HashMap map = new HashMap();
            map.put("vehicleList", vehicleList);
            map.put("total", count);
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, msg);
        }
        return responseObj;
    }


    /**
     * 车辆基础信息删除
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "删除车辆基本信息(逻辑删除)", notes = "参数vin、iccId、userId、appId、plateNO,vehicleType,vehicleCompany,其中vin、iccId必传", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iccId", value = "SIM卡卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleType", value = "车型", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleCompany", value = "车企", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appId", value = "应用id", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "plateNo", value = "车牌号", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj deleteVehicleBaseInfo(VehicleDTO vehicleDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        HashMap map = new HashMap();
        map.put("vin", vehicleDTO.getVin());
        map.put("iccId", vehicleDTO.getIccId());
        responseObj.setData(map);
        if (StringUtils.isEmpty(vehicleDTO.getVin()) || StringUtils.isEmpty(vehicleDTO.getIccId())) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数vin、iccId为必传项");
            return responseObj;
        }
        String delResult = vehicleService.deleteVehicleBaseInfo(vehicleDTO);
        if (StringUtils.isEmpty(delResult)) {
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        } else {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, delResult);
        }

        return responseObj;
    }

    /**
     * 统计车辆基础信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findVehicleCount",method= RequestMethod.GET)
    @ApiOperation(value = "统计车辆总数", notes = "参数vin、iccId、userId、appId、plateNO,vehicleType,vehicleCompany,其中vin、iccId必传", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iccId", value = "SIM卡卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleType", value = "车型", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleCompany", value = "车企", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appId", value = "应用id", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "plateNo", value = "车牌号", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj findVehicleCount(VehicleDTO vehicleDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String msg=queryValidateData(vehicleDTO);
        if(StringUtils.isEmpty(msg)){
            int count = vehicleService.findVehicleCount(vehicleDTO);
            HashMap map = new HashMap();
            map.put(Constants.QUERY_COUNT, count);
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, msg);
        }
            return responseObj;
    }


    /**
     * 批量保存车辆基础信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/batchSaveVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "批量保存车辆基础信息", notes = "传数组vehicles:[{vin、iccid、userid、appid、plateNO...}]参数", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj batchSaveVehicleBaseInfo(@RequestBody String vehicles) {
        JSONObject json = JSONObject.parseObject(vehicles);
        List<VehicleDTO> vehicleList = json.parseArray(json.get("vehicles")+"", VehicleDTO.class);
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = "";
        for (int i = 0; i < vehicleList.size(); i++) {
            String msg = queryValidateData((VehicleDTO) vehicleList.get(i));
            msg = msg+validateData((VehicleDTO) vehicleList.get(i));
            if (StringUtils.isNotEmpty(msg)) {
                result = result + msg;
            }
        }
        if (StringUtils.isNotEmpty(result)) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, result);
            return responseObj;
        }
        for (int i = 0; i < vehicleList.size(); i++) {
            VehicleDTO vehicle = (VehicleDTO) vehicleList.get(i);
            String saveResult = vehicleService.saveVehicleBaseInfo(vehicle);//保存数据到geode
            if (StringUtils.isNotEmpty(saveResult)) {
                result = result + "失败的数据：vin=" + vehicle.getVin() + "、iccId=" + vehicle.getIccId() + " " + saveResult;
            }
        }

        if (StringUtils.isNotEmpty(result)) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, result);
        } else {
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }
        return responseObj;
    }


    /**
     * 批量删除车辆基础信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/batchDeleteVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "批量删除车辆基础信息", notes = "传vehicles:[{vin、iccid}]数组参数", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj batchDeleteVehicleBaseInfo(@RequestBody String vehicles) {
        JSONObject json = JSONObject.parseObject(vehicles);
        List<VehicleDTO> vehicleList = json.parseArray(json.get("vehicles")+"", VehicleDTO.class);
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = "";
        for (int i = 0; i < vehicleList.size(); i++) {
            if (StringUtils.isEmpty(vehicleList.get(i).getVin()) || StringUtils.isEmpty(vehicleList.get(i).getIccId())) {
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "数组中参数vin、iccId为必传项");
                return responseObj;
            }
        }
        for (int k = 0; k < vehicleList.size(); k++) {
            VehicleDTO vehicleDTO = vehicleList.get(k);
            String delResult = vehicleService.deleteVehicleBaseInfo(vehicleDTO);
            if (StringUtils.isNotEmpty(delResult)) {
                result = result + "vin=" + vehicleDTO.getVin() + "、iccId=" + vehicleDTO.getIccId() + "的数据删除失败>" + delResult + ";";
            }
        }
        if (StringUtils.isNotEmpty(result)) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, result);
        } else {
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }
        return responseObj;
    }

    /**
     * 多个vin查询车辆基础信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findVehicleBaseInfoByVinList",method= RequestMethod.GET)
    @ApiOperation(value = "批量查询车辆基础信息", notes = "传vins:[vin1,vin2]数组参数", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj findVehicleBaseInfoByVinList(@RequestBody String vins) {
        JSONObject json = JSONObject.parseObject(vins);
        List<String> vehicleList = json.parseArray(json.get("vins")+"", String.class);
        ResponseObj responseObj = ResponseObj.createResponseObj();
        List<VehicleDTO> list = vehicleService.findVehicleBaseInfoByVinList(vehicleList);
        HashMap map = new HashMap();
        map.put("vehicleList", list);
        map.put("total", list.size());
        responseObj.setData(map);
        responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        return responseObj;
    }

    /**
     * 验证输入参数
     *
     * @param vehicleDTO
     * @return
     */
    private String validateData(VehicleDTO vehicleDTO) {
        StringBuffer result = new StringBuffer("");
        if (StringUtils.isEmpty(vehicleDTO.getVin()) || !CommonUtils.checkVIN(vehicleDTO.getVin())) {
            result.append("vin为"+vehicleDTO.getVin()+"的vin 格式错误;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getAppId())) {
            result.append("vin为"+vehicleDTO.getVin()+"的appId 不能为空;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getIccId()) || !CommonUtils.checkIccId(vehicleDTO.getIccId())) {
            result.append("vin为"+vehicleDTO.getVin()+"的iccId 格式错误;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getUserId())) {
            result.append("vin为"+vehicleDTO.getVin()+"的userId 不能为空;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getPlateNo())
                && (!Pattern.matches("^[\\u4e00-\\u9fa5|WJ]{1}[A-Z0-9]{6}$", vehicleDTO.getPlateNo()))
                &&!Pattern.matches("^[\\u4e00-\\u9fa5|WJ]{1}[A-Z0-9]{7}$", vehicleDTO.getPlateNo())) {
            result.append("vin为"+vehicleDTO.getVin()+"的plateNo 格式错误;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getVehicleCompany())) {
            result.append("vin为"+vehicleDTO.getVin()+"的vehicleCompany 不能为空;");
        }
//        if (StringUtils.isEmpty(vehicleDTO.getVehicleType())) {
//            result.append("vin为"+vehicleDTO.getVin()+"的vehicleType 不能为空;");
//        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleCompany())&&vehicleDTO.getVehicleCompany().length()>50) {
            result.append("vin为"+vehicleDTO.getVin()+"的vehicleCompany 长度不能超过50个字符;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleType())&&vehicleDTO.getVehicleType().length()>50) {
            result.append("vin为"+vehicleDTO.getVin()+"的vehicleType 长度不能超过50个字符;");
        }
        return result.toString();
    }

    /**
     * 查询条件验证
     *
     * @param vehicleDTO
     * @return
     */
    private String queryValidateData(VehicleDTO vehicleDTO) {
        StringBuffer result = new StringBuffer("");
        if (StringUtils.isNotEmpty(vehicleDTO.getVin())&&vehicleDTO.getVin().indexOf("'")>-1) {
            result.append("vin不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getAppId())&&vehicleDTO.getAppId().indexOf("'")>-1) {
            result.append("appId不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getIccId())&&vehicleDTO.getIccId().indexOf("'")>-1) {
            result.append("iccId不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getUserId())&&vehicleDTO.getUserId().indexOf("'")>-1) {
            result.append("userId不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getPlateNo())&&vehicleDTO.getPlateNo().indexOf("'")>-1) {
            result.append("plateNo不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleCompany())&&vehicleDTO.getVehicleCompany().indexOf("'")>-1) {
            result.append("vehicleCompany不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleType())&&vehicleDTO.getVehicleType().indexOf("'")>-1) {
            result.append("vehicleType不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getIsValid())&&vehicleDTO.getIsValid().indexOf("'")>-1) {
            result.append("isValid不能包含单引号;");
        }
        return result.toString();
    }

}
