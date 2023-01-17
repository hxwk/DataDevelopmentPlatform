package com.dfssi.dataplatform.userhome.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.userhome.Entity.UserEntity;
import com.dfssi.dataplatform.userhome.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Api
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;


    @ApiOperation(value = "新增系统用户", notes = "根据表单信息新增系统用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userEntity", value = "系统用户模块表单信息", required = true, dataType = "UserEntity")
    })
    @RequestMapping(value ="/insertUser",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj insertMenu(@RequestBody UserEntity userEntity) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            String result= userService.insert(userEntity);
            if (StringUtils.isEmpty(result)) {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
            }
        } catch (Exception e) {
           e.printStackTrace();
           responseObj.setData("");
           responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }

    @ApiOperation(value = "修改系统用户", notes = "根据表单信息修改系统用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userEntity", value = "系统用户模块表单信息", required = true, dataType = "UserEntity")
    })
    @RequestMapping(value ="/modifyUser",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj modifyMenu(@RequestBody UserEntity userEntity) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            String result= userService.updateUser(userEntity);
            if (StringUtils.isEmpty(result)) {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }

    @ApiOperation(value = "删除系统用户", notes = "删除系统用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID，逗号拼接", required = true, dataType = "String")
    })
    @RequestMapping(value ="/deleteUser",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj deleteUser(String id) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        String result= userService.deleteUser(id);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    @ApiOperation(value = "修改用户密码",notes = "根据用户ID修改用户密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户ID",required = true,dataType = "String"),
            @ApiImplicitParam(name = "uPsword",value = "用户密码",required = true,dataType = "String")
    })
    @RequestMapping(value = "/updatauPsword",method =RequestMethod.GET)
    @LogAudit
    public ResponseObj updataUPsword(UserEntity entity, String nUPsword) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try{
            String result = userService.modifyUser(entity, nUPsword);
            if (StringUtils.isEmpty(result)) {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
            }
        }catch(Exception e){
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return  responseObj;
    }

    @ApiOperation(value = "查询所有用户",notes = "根据条件查询符合的全部用户")
    @RequestMapping(value = "/getusers",method =RequestMethod.GET)
    @LogAudit
    public ResponseObj getUsers(UserEntity entity, PageParam pageParam) {
        ResponseObj result=ResponseObj.createResponseObj();
        try {
            PageResult<UserEntity> pageResult= userService.selectByUserEntity(entity,pageParam);
            result.setData(pageResult);
            result.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return result;
    }

}