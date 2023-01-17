package com.dfssi.dataplatform.userhome.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.userhome.Entity.MenuEntity;
import com.dfssi.dataplatform.userhome.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menu")
@Api
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "分页查询所有菜单信息")
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj getAllMenuByPage(PageParam pageParam, String menuName) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            PageResult<MenuEntity> pageResult = menuService.selectByMenuName(pageParam,menuName);
            responseObj.setData(pageResult);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return responseObj;
    }


    @ApiOperation(value = "新增功能菜单模块", notes = "根据表单信息新增功能菜单模块")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuEntity", value = "功能菜单模块表单信息", required = true, dataType = "MenuEntity")
    })
    @RequestMapping(value = "/insertMenu",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj insertMenu(@RequestBody MenuEntity menuEntity) {
         ResponseObj responseObj=ResponseObj.createResponseObj();
         String result= menuService.insert(menuEntity);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    @ApiOperation(value = "修改系统功能菜单", notes = "根据表单信息修改系统功能菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuEntity", value = "功能菜单模块表单信息", required = true, dataType = "MenuEntity")
    })
    @RequestMapping(value = "/modifyMenu",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj modifyMenu(@RequestBody MenuEntity menuEntity) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        String result= menuService.modifyMenu(menuEntity);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    @ApiOperation(value = "检查系统功能菜单是否可以删除", notes = "如果要删除的功能菜单当前还有关联的按钮或者角色，那么就不可删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuIDs", value = "功能菜单ID", required = true, dataType = "String")
    })
    @RequestMapping(value ="/checkMenuDeleteEnv",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj checkMenuDeleteEnv( String menuIDs) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            if (!menuService.checkMenuDeleteEnv(menuIDs)) {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败","当前功能菜单不可删除，请先删除该菜单下属的菜单按钮或解除所关联的角色");
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

    @ApiOperation(value = "删除系统功能菜单", notes = "根据主键删除系统功能菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuIDs", value = "功能菜单ID", required = true, dataType = "String")
    })
    @RequestMapping(value ="/deleteMenu",method = RequestMethod.GET)
    @LogAudit
    public ResponseObj deleteMenu(String menuIDs) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        String result= menuService.deleteMenu(menuIDs);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }
}
