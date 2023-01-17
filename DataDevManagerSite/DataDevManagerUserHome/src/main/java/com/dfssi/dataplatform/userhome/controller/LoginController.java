package com.dfssi.dataplatform.userhome.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.userhome.Entity.UserEntity;
import com.dfssi.dataplatform.userhome.service.LoginService;
import com.dfssi.dataplatform.userhome.service.MenuService;
import com.dfssi.dataplatform.userhome.utils.CheckImageServlet;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *  登录及会话管理控制器
 */
@RestController
@Api
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    /**
     * 账号密码登入
     * @param usermap
     * @return ResponseObj
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj login(@RequestBody Map<String,String> usermap) {


        ResponseObj result=ResponseObj.createResponseObj();
        //账号密码核对
        try {
        UserEntity user = loginService.login(usermap.get("userName"),usermap.get("userPassword"));
        //登入成功返回用户名,微服务ID,本账号的消息
        if (user!=null){
                if(StringUtils.isNotEmpty(user.getURole())){
                    user.setMenuData(menuService.getMenuByUser(user.getURole()).toString());
                }
                result.setData(user);
                result.setStatus("1","成功","");
        }else {
            result.setData("");
            result.setStatus("-1","失败","账号密码错误");
        }
        } catch (Exception e){
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return result;
    }

}