package com.dfssi.dataplatform.userhome.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.userhome.entity.RoleEntity;
import com.dfssi.dataplatform.userhome.service.FieldRulesService;
import com.dfssi.dataplatform.userhome.service.MenuService;
import com.dfssi.dataplatform.userhome.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/role")
@Api
public class RoleController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private FieldRulesService fieldRulesService;


    @ApiOperation(value = "添加系统角色", notes = "根据表单信息新增系统角色和角色所关联的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "models", value = "角色表单信息", required = true, dataType = "Map")
    })
    @RequestMapping(value = "/insertRole",method =RequestMethod.POST)
    @LogAudit
    public ResponseObj insertRole(@RequestBody @Validated Map<String, Object> models, BindingResult bindingResult) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            String str =fieldRulesService.paramValid(bindingResult);
            if(StringUtils.isNotEmpty(str)){
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_P,"参数格式不正确！",str);
                return responseObj;
            }
            String result= roleService.insert(models);
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
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return responseObj;
    }

    @ApiOperation(value = "修改系统角色", notes = "根据表单信息修改系统角色和角色所关联的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "models", value = "角色表单信息", required = true, dataType = "Map")
    })
    @RequestMapping(value = "/modifyRole",method =RequestMethod.POST)
    @LogAudit
    public ResponseObj modifyRole(@RequestBody @Validated Map<String, Object> models,BindingResult bindingResult) {
            ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            String str =fieldRulesService.paramValid(bindingResult);
            if(StringUtils.isNotEmpty(str)){
             responseObj.setData("");
             responseObj.setStatus(ResponseObj.CODE_FAIL_P,"参数格式不正确！",str);
             return responseObj;
            }
            String result= roleService.modifyRole(models);
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
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return responseObj;
    }

    @ApiOperation(value = "检查系统角色是否可以删除", notes = "如果要删除的角色当前还有关联的有效用户，那么就不可删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleIDs", value = "角色ID", required = true, dataType = "String")
    })
    @RequestMapping(value = "/checkRoleDeleteEnv",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj checkRoleDeleteEnv( String roleIDs) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            if (roleService.checkRoleDeleteEnv(roleIDs)) {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败","当前角色不可删除，请先解除该角色关联的所有用户");
            } else {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","可以删除");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }

    @ApiOperation(value = "删除系统角色", notes = "根据主键删除系统角色和角色所关联的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleIDs", value = "角色ID", required = true, dataType = "String")
    })
    @RequestMapping(value = "/deleteRole",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj deleteRole(String roleIDs) {
         ResponseObj responseObj=ResponseObj.createResponseObj();
         String result= roleService.deleteRole(roleIDs);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    //单独请求字符串会发生乱码，将字符串放到map对象中请求
    @ApiOperation(value = "查询所有角色下面的权限", notes = "查询所有角色下面的权限")
    @RequestMapping(value = "/getrole/all",method =RequestMethod.GET)
    @LogAudit
    public ResponseObj getRoleAll(int requestTreeType) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            String result= menuService.getAllMenu(requestTreeType);
            responseObj.setData(result);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }

    @ApiOperation(value = "分页查询所有角色信息")
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj getAllRoleByPage(PageParam pageParam, String roleName) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            PageResult<RoleEntity> pageResult= roleService.selectByRoleName(pageParam,roleName);
            responseObj.setData(pageResult);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }


    @ApiOperation(value = "查询指定角色下面的权限", notes = "查询指定角色下面的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleID", value = "角色ID", required = true, dataType = "String")
    })
    @RequestMapping(value = "/getRoleMenu",method =RequestMethod.GET)
    @LogAudit
    public ResponseObj getRoleMenu(@RequestParam String roleID) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            Object result= menuService.getMenuByUser(roleID);
            responseObj.setData(result);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }
}
