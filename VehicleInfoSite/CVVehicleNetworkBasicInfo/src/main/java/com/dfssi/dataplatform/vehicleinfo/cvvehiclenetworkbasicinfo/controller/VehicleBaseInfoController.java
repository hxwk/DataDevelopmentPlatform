package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleBaseInfoService;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;
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
import java.util.regex.Pattern;

/**
 * 车辆基础信息接入
 *
 * @author yanghs
 * @since 2018-4-2 11:11:32
 */
@Controller
@RequestMapping(value = "/vehicleBaseInfo")
@Api(value = "vehicleBaseInfoController", description = "车辆基础信息接入")
public class VehicleBaseInfoController {

    @Autowired
    private IVehicleBaseInfoService vehicleService;

    /**
     * 车辆基础信息查询
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "查询车辆基本信息", notes = "参数vin、sim、vid、did", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sim", value = "SIM卡卡号", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vid", value = "vid", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "did", value = "终端ID", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj findVehicleBaseInfo(@RequestBody String vehicle) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        boolean validParam=false;
        CVVehicleDTO vehicleDTO=new CVVehicleDTO();
        try {
            JSONObject json = JSONObject.parseObject(vehicle);
            vehicleDTO.setVin(json.get("vin")==null?"":(json.get("vin") + ""));
            vehicleDTO.setSim(json.get("sim")==null?"":(json.get("sim") + ""));
            vehicleDTO.setVid(json.get("vid")==null?"":(json.get("vid") + ""));
            vehicleDTO.setDid(json.get("did")==null?"":(json.get("did") + ""));
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            String msg = queryValidateData(vehicleDTO);
            if (StringUtils.isEmpty(msg)) {
                int count = vehicleService.findVehicleCount(vehicleDTO);
                List<CVVehicleDTO> vehicleList = new ArrayList<>();
                if (count > 0) {
                    vehicleList = vehicleService.findVehicleBaseInfo(vehicleDTO, null);
                }
                HashMap map = new HashMap();
                map.put(Constants.RECORDS, vehicleList);
                map.put(Constants.TOTAL, count);
                responseObj.setData(map);
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
            } else {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, msg);
            }
        }else{
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
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
    @ApiOperation(value = "批量保存车辆基础信息", notes = "传数组vehicles:[{vin、sim、vid、did}]参数", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj batchSaveVehicleBaseInfo(@RequestBody String vehicles) {
        boolean validParam=false;
        List<CVVehicleDTO> vehicleList=null;
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        try {
            JSONObject json = JSONObject.parseObject(vehicles);
            vehicleList = json.parseArray(json.get("vehicles") + "", CVVehicleDTO.class);
            if(vehicleList!=null){
                validParam=true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            String result = "";
            for (int i = 0; i < vehicleList.size(); i++) {
                String msg = queryValidateData((CVVehicleDTO) vehicleList.get(i));
                msg = msg + validateData((CVVehicleDTO) vehicleList.get(i));
                if (StringUtils.isNotEmpty(msg)) {
                    result = result + msg;
                }
            }
            if (StringUtils.isNotEmpty(result)) {
                responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, result);
                return responseObj;
            }
            for (int i = 0; i < vehicleList.size(); i++) {
                CVVehicleDTO vehicle = (CVVehicleDTO) vehicleList.get(i);
                String saveResult = vehicleService.saveVehicleBaseInfo(vehicle);//保存数据到geode
                if (StringUtils.isNotEmpty(saveResult)) {
                    result = result + "失败的数据：vin=" + vehicle.getVin() + "、sim=" + vehicle.getSim() + " " + saveResult;
                }
            }

            if (StringUtils.isNotEmpty(result)) {
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, result);
            } else {
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
            }
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
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
    @ApiOperation(value = "批量删除车辆基础信息", notes = "传vehicles:[{vid}]数组参数", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj batchDeleteVehicleBaseInfo(@RequestBody String vids) {
        boolean validParam=false;
        List<String> vehicleList=null;
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData(new HashMap());
        try {
            JSONObject json = JSONObject.parseObject(vids);
            vehicleList = json.parseArray(json.get("vids") + "", String.class);
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            String result = "";
            for (int k = 0; k < vehicleList.size(); k++) {


                CVVehicleDTO vehicleDTO = new CVVehicleDTO();
                vehicleDTO.setVid(vehicleList.get(k));
                String delResult = vehicleService.deleteVehicleBaseInfo(vehicleDTO);
                if (StringUtils.isNotEmpty(delResult)) {
                    result = result + "vid=" + vehicleDTO.getVid() + "的数据删除失败>" + delResult + ";";
                }
            }
            if (StringUtils.isNotEmpty(result)) {
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, result);
            } else {
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
            }
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }
        return responseObj;
    }

    /**
     * 车辆基础信息手动录入
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "车辆基本信息单条记录保存", notes = "参数vin、sim、vid、did", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sim", value = "SIM卡卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vid", value = "vid", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "did", value = "终端ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vehicleType", value = "车型", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "plateNo", value = "车牌号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "buyTime", value = "购车时间", required = true, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj saveVehicleBaseInfo(@RequestBody String obj) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData("");
        JSONObject json = JSONObject.parseObject(obj);
        CVVehicleDTO  vehicleDTO= json.parseObject(json.get("vehicles") + "", CVVehicleDTO.class);
        String msg = queryValidateData(vehicleDTO);
//        msg=msg+validateData(vehicleDTO);
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
     * 车辆基础信息删除
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteVehicleBaseInfo",method= RequestMethod.POST)
    @ApiOperation(value = "删除车辆基本信息(逻辑删除)", notes = "参数vid", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vid", value = "vid", required = true, dataType = "String", paramType = "query"),
    })
    @LogAudit
    public ResponseObj deleteVehicleBaseInfo(CVVehicleDTO vehicleDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData("");
        if (StringUtils.isEmpty(vehicleDTO.getVid())) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数vid为必传项");
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
    @ApiOperation(value = "统计车辆总数", notes = "参数vin、sim、vid、did必传", code = 200, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆识别代码", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sim", value = "SIM卡卡号", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vid", value = "vid", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "did", value = "终端编号ID", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj findVehicleCount(CVVehicleDTO vehicleDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String msg=queryValidateData(vehicleDTO);
        if(StringUtils.isEmpty(msg)){
            int count = vehicleService.findVehicleCount(vehicleDTO);
            HashMap map = new HashMap();
            map.put(Constants.TOTAL,count);
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, msg);
        }
        return responseObj;
    }




    /**
     * 多个vin查询车辆基础信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findVehicleBaseInfoByVidList",method= RequestMethod.POST)
    @ApiOperation(value = "批量查询车辆基础信息", notes = "传vids:[vid]数组参数", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj findVehicleBaseInfoByVidList(@RequestBody String vids) {
        boolean validParam=false;
        List<String> vehicleList=null;
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
        JSONObject json = JSONObject.parseObject(vids);
            vehicleList = json.parseArray(json.get("vids")+"", String.class);
            validParam=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(validParam) {
            List<CVVehicleDTO> list = vehicleService.findVehicleBaseInfoByVidList(vehicleList);
            HashMap map = new HashMap();
            map.put(Constants.RECORDS, list);
            map.put(Constants.TOTAL,list.size());
            responseObj.setData(map);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }else{
            responseObj.setData(new HashMap());
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "参数解析失败");
        }
        return responseObj;
    }

    /**
     * 验证输入参数
     *
     * @param vehicleDTO
     * @return
     */
    private String validateData(CVVehicleDTO vehicleDTO) {
        StringBuffer result = new StringBuffer("");
        if (StringUtils.isEmpty(vehicleDTO.getVin()) || !CommonUtils.checkVIN(vehicleDTO.getVin())) {
            result.append("vin为"+vehicleDTO.getVin()+"的vin 格式错误;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getSim())) {
            result.append("vin为"+vehicleDTO.getVin()+"的sim 不能为空;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getVid()) ) {
            result.append("vin为"+vehicleDTO.getVin()+"的vid 不能为空;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getDid()) ) {
            result.append("vin为"+vehicleDTO.getVin()+"的did 不能为空;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getVehicleType())) {
            result.append("vin为"+vehicleDTO.getVin()+"的vehicleType不能为空;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getPlateNo())) {
            result.append("vin为"+vehicleDTO.getVin()+"的plateNo不能为空;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getPlateNo())
                && !Pattern.matches("^[\\u4e00-\\u9fa5|WJ]{1}[A-Z0-9]{6}$", vehicleDTO.getPlateNo())) {
            result.append("vin为"+vehicleDTO.getVin()+"的plateNo 格式错误;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getBuyTime())) {
            result.append("vin为"+vehicleDTO.getVin()+"的buyTime不能为空;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getBuyTime())&&!CommonUtils.covertDate(vehicleDTO.getBuyTime())) {
            result.append("vin为"+vehicleDTO.getVin()+"的buyTime格式错误;");
        }
        if (StringUtils.isEmpty(vehicleDTO.getUserId())) {
            result.append("vin为"+vehicleDTO.getVin()+"的userId不能为空;");
        }
        return result.toString();
    }

    /**
     * 查询条件验证
     *
     * @param vehicleDTO
     * @return
     */
    private String queryValidateData(CVVehicleDTO vehicleDTO) {
        StringBuffer result = new StringBuffer("");
        if (StringUtils.isNotEmpty(vehicleDTO.getVin())&&vehicleDTO.getVin().indexOf("'")>-1) {
            result.append("vin不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getSim())&&vehicleDTO.getSim().indexOf("'")>-1) {
            result.append("sim不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVid())&&vehicleDTO.getVid().indexOf("'")>-1) {
            result.append("vid不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getDid())&&vehicleDTO.getDid().indexOf("'")>-1) {
            result.append("did不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleType())&&vehicleDTO.getVehicleType().indexOf("'")>-1) {
            result.append("vehicleType不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getPlateNo())&&vehicleDTO.getPlateNo().indexOf("'")>-1) {
            result.append("plateNo不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getBuyTime())&&vehicleDTO.getBuyTime().indexOf("'")>-1) {
            result.append("buyTime不能包含单引号;");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getUserId())&&vehicleDTO.getUserId().indexOf("'")>-1) {
            result.append("userId不能包含单引号;");
        }
        return result.toString();
    }

}
