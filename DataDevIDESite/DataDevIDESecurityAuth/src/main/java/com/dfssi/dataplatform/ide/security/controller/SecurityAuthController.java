package com.dfssi.dataplatform.ide.security.controller;

import com.dfssi.dataplatform.ide.security.model.SecurityAuthModels;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/4 9:46
 */
@Api(tags = {"安全认证控制器"})
@RestController
@RequestMapping(value="/securityauth/")
public class SecurityAuthController {

    private final Logger logger = LoggerFactory.getLogger(SecurityAuthController.class);

    @Autowired
    private SecurityAuthModels securityAuthModels;

    @ApiOperation(value = "列举安全认证信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderField", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderType",  value = "排序类型", dataType = "String",  paramType = "query", allowableValues = "asc,desc"),
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "int", defaultValue="1", paramType = "query"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "int", defaultValue="10", paramType = "query")
    })
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    @ResponseBody
    public Object list(String orderField,
                       @RequestParam(defaultValue = "asc") String orderType,
                       @RequestParam(defaultValue = "1") int pagenum,
                       @RequestParam(defaultValue = "10")int pagesize){
        String error;
        try {

            if("desc".equalsIgnoreCase(orderType)){
                orderType = "desc";
            }else{
                orderType = "asc";
            }

            Map<String, Object> authByCondition =
                    securityAuthModels.findAll(pagenum, pagesize, orderField, orderType);
            return ResponseHelper.success(authByCondition);
        } catch (Exception e) {
            error = e.getMessage();
            logger.error("列举安全认证信息失败。", e);
        }
        return ResponseHelper.error(error);
    }

    @ApiOperation(value = "根据条件查询安全认证信息", notes = "auth_name使用模糊匹配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authname", value = "认证名称, 模糊匹配", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "authtype", value = "认证类型，精确匹配", dataType = "String"),
            @ApiImplicitParam(name = "starttime", value = "创建时间下界, 精确到毫秒", dataType = "long"),
            @ApiImplicitParam(name = "endtime", value = "创建时间上界, 精确到毫秒", dataType = "long"),
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "int", defaultValue="1"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "int", defaultValue="10"),
            @ApiImplicitParam(name = "orderField", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType",  value = "排序类型", dataType = "String", allowableValues = "asc,desc")
    })
    @RequestMapping(value = "findauth", method = {RequestMethod.GET})
    @ResponseBody
    public Object findAuthByCondition(String authname,
                                      String authtype,
                                      Long starttime,
                                      Long endtime,
                                      String orderField,
                                      @RequestParam(defaultValue = "asc") String orderType,
                                      @RequestParam(defaultValue = "1") int pagenum,
                                      @RequestParam(defaultValue = "10")int pagesize){
        String error;
        try {
            StringBuilder sb = new StringBuilder("条件查询安全认证信息, 参数如下：\n\t");
            sb.append("authname = ").append(authname).append(", ");
            sb.append("authtype = ").append(authtype).append(", ");

            sb.append("starttime = ").append(starttime).append(", ");
            sb.append("endtime = ").append(endtime).append(", ");
            sb.append("pagenum = ").append(pagenum).append(", ");
            sb.append("pagesize = ").append(pagesize).append(", ");

            if("desc".equalsIgnoreCase(orderType)){
                orderType = "desc";
            }else{
                orderType = "asc";
            }

            sb.append("orderField = ").append(orderField).append(", ");
            sb.append("orderType = ").append(orderType);
            logger.info(sb.toString());

            Map<String, Object> authByCondition =
                    securityAuthModels.findAuthByCondition(authname,
                            authtype, starttime, endtime, pagenum, pagesize, orderField, orderType);
            return ResponseHelper.success(authByCondition);
        } catch (Exception e) {
            error = e.getMessage();
            logger.error("条件查询安全认证信息失败。", e);
        }

        return ResponseHelper.error(error);
    }


    @ApiOperation(value = "修改安全认证信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "认证信息ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "authname", value = "认证名称", dataType = "String"),
            @ApiImplicitParam(name = "authdesc", value = "认证描述", dataType = "String"),
            @ApiImplicitParam(name = "authtype", value = "认证类型", dataType = "String"),
            @ApiImplicitParam(name = "authuser", value = "认证用户名", dataType = "String"),
            @ApiImplicitParam(name = "authpassword", value = "认证用户密码", dataType = "String"),
            @ApiImplicitParam(name = "editor", value = "修改者",  required = true, dataType = "String")

    })
    @RequestMapping(value = "update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(@ApiParam(hidden = true) @RequestBody Map<String, String> param){
        String error;
        String idStr = param.get("id");
        String editor = param.get("editor");
        if(idStr != null && editor != null) {
            try {
                int id = Integer.parseInt(idStr);
                StringBuilder sb = new StringBuilder("修改安全认证信息, 参数如下：\n\t");
                sb.append("id = ").append(id).append(", ");
                sb.append("authname = ").append(param.get("authname")).append(", ");
                sb.append("authdesc = ").append(param.get("authdesc")).append(", ");
                sb.append("authtype = ").append(param.get("authtype")).append(", ");
                sb.append("authuser = ").append(param.get("authuser")).append(", ");
                sb.append("authpassword = ").append(param.get("authpassword")).append(", ");
                sb.append("editor = ").append(editor);
                logger.info(sb.toString());

                securityAuthModels.updateById(id,
                        param.get("authname"),
                        param.get("authdesc"),
                        param.get("authtype"),
                        param.get("authuser"),
                        param.get("authpassword"),
                        editor);
                return ResponseHelper.success(null);
            } catch (Exception e) {
                error = e.getMessage();
            }
        }else {
            error = "id和editor不能为空";
        }
        return ResponseHelper.error(error);
    }

    @ApiOperation(value = "删除安全认证信息")
    @ApiImplicitParam(name = "id", value = "认证信息ID", required = true, dataType = "int", paramType = "get")
    @RequestMapping(value = "delete", method = {RequestMethod.GET})
    @ResponseBody
    public Object delete(@RequestParam int id){
        String error;
        try {

            logger.warn(String.format("删除安全认证信息，参数如下：\n\t id = %s", id));

            securityAuthModels.deleteById(id);
            return ResponseHelper.success(null);
        } catch (Exception e) {
            error = e.getMessage();
        }
        return ResponseHelper.error(error);
    }

    @ApiOperation(value = "添加安全认证信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authname", value = "认证名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "authdesc", value = "认证描述", required = true, dataType = "String"),
            @ApiImplicitParam(name = "authtype", value = "认证类型", required = true, dataType = "String"),
            @ApiImplicitParam(name = "authuser", value = "认证用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "authpassword", value = "认证用户密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "creator", value = "创建者",  required = true, dataType = "String")

    })
    @RequestMapping(value = "add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@ApiParam(hidden = true) @RequestBody Map<String, String> param){
        String error;
        try {
            StringBuilder sb = new StringBuilder("添加安全认证信息, 参数如下：\n\t");
            sb.append("authname = ").append(param.get("authname")).append(", ");
            sb.append("authdesc = ").append(param.get("authdesc")).append(", ");
            sb.append("authtype = ").append(param.get("authtype")).append(", ");
            sb.append("authuser = ").append(param.get("authuser")).append(", ");
            sb.append("authpassword = ").append(param.get("authpassword")).append(", ");
            sb.append("creator = ").append(param.get("creator"));
            logger.info(sb.toString());

            securityAuthModels.add(param.get("authname"),
                    param.get("authdesc"),
                    param.get("authtype"),
                    param.get("authuser"),
                    param.get("authpassword"),
                    param.get("creator"));
            return ResponseHelper.success(null);
        } catch (Exception e) {
            error = e.getMessage();
        }
        return ResponseHelper.error(error);
    }
}
