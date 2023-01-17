package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.PlatformDTO;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.IPlatformInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 平台信息
 *
 * @author 杨海松
 * @since 2018-4-11 14:57:43.
 */
@Controller
@RequestMapping("/platform")
@Api(value = "platformInfoController", description = "平台信息接入")
public class PlatformInfoController {

    @Autowired
    private IPlatformInfoService iPlatformInfoService;

    /**
     * 查询平台信息
     *
     * @param platformDTO
     * @return
     */
    @RequestMapping(value="/findPlatformInfo",method= RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "平台基本信息查询", notes = "参数userName、vehicleCompany", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj findPlatformInfo(PlatformDTO platformDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String msg=queryValidateData(platformDTO);
        if (StringUtils.isEmpty(msg)){
            int count = iPlatformInfoService.findPlatformCount(platformDTO);
            List<PlatformDTO> platformList = new ArrayList<>();
            if (count > 0) {
                platformList = iPlatformInfoService.findPlatformInfo(platformDTO,null);
            }
            HashMap map = new HashMap();
            map.put("platformList", platformList);
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
     * 保存平台信息
     *
     * @param platformDTO
     * @return
     */
    @RequestMapping(value="/savePlatformInfo",method= RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "平台基本信息单条记录保存", notes = "userName、password、vehicleCompany为必填项,userId可选填", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj savePlatformInfo(PlatformDTO platformDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String valid=validateData(platformDTO);
        if (StringUtils.isNotEmpty(valid)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, valid);
            return responseObj;
        }
       String msg= queryValidateData(platformDTO);
        if(StringUtils.isNotEmpty(msg)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, msg);
            return responseObj;
        }
        String result = iPlatformInfoService.savePlatformInfo(platformDTO);
        HashMap map = new HashMap();
        responseObj.setData(map);
        if (StringUtils.isNotEmpty(result)) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, result);
        } else {
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }

        return responseObj;
    }

    /**
     * 删除平台信息
     *
     * @param platformDTO
     * @return
     */
    @RequestMapping(value="/deletePlatformInfo",method= RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "平台基本信息删除", notes = "userName", code = 200, produces = "application/json")
    @LogAudit
    public ResponseObj deletePlatformInfo(PlatformDTO platformDTO) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        if (StringUtils.isEmpty(platformDTO.getUserName())){
            responseObj.setStatus(ResponseObj.CODE_FAIL_P, Constants.FAIL, "userName为必传项");
            return responseObj;
        }
        String result = iPlatformInfoService.deletePlatformInfo(platformDTO);
        if (StringUtils.isNotEmpty(result)) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, Constants.FAIL, result);
        } else {
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, Constants.SUCCESS, "");
        }

        return responseObj;
    }

    /**
     * 验证输入参数
     * @param platformDTO
     * @return
     */
    private String validateData(PlatformDTO platformDTO){
        StringBuffer result = new StringBuffer("");
        if (StringUtils.isEmpty(platformDTO.getUserName())) {
            result.append("userName不能为空");
        }
//        if (StringUtils.isNotEmpty(platformDTO.getUserName())&&platformDTO.getUserName().length()!=12) {
//            result.append("userName长度只能为12");
//        }
        if (StringUtils.isEmpty(platformDTO.getPassword())) {
            result.append("password不能为空");
        }
//        if (StringUtils.isNotEmpty(platformDTO.getPassword())&&platformDTO.getPassword().length()!=20) {
//            result.append("password长度只能为20");
//        }
        if (StringUtils.isEmpty(platformDTO.getVehicleCompany())) {
            result.append("vehicleCompany不能为空");
        }
        if (StringUtils.isNotEmpty(platformDTO.getVehicleCompany())&&platformDTO.getVehicleCompany().length()>50) {
            result.append("vehicleCompany长度不能大于50");
        }
        return result.toString();
    }

    /**
     * 查询条件验证
     *
     * @param platformDTO
     * @return
     */
    private String queryValidateData(PlatformDTO platformDTO) {
        StringBuffer result = new StringBuffer("");
        if (StringUtils.isNotEmpty(platformDTO.getUserId())&&platformDTO.getUserId().indexOf("'")>-1) {
            result.append("userId不能包含单引号");
        }
        if (StringUtils.isNotEmpty(platformDTO.getUserName())&&platformDTO.getUserName().indexOf("'")>-1) {
            result.append("userName不能包含单引号");
        }
        if (StringUtils.isNotEmpty(platformDTO.getPassword())&&platformDTO.getPassword().indexOf("'")>-1) {
            result.append("password不能包含单引号");
        }
        if (StringUtils.isNotEmpty(platformDTO.getVehicleCompany())&&platformDTO.getVehicleCompany().indexOf("'")>-1) {
            result.append("vehicleCompany不能包含单引号");
        }
        return result.toString();
    }
}
