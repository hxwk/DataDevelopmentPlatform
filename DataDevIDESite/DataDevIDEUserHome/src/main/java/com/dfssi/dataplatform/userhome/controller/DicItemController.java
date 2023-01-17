package com.dfssi.dataplatform.userhome.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.userhome.entity.DicItemEntity;
import com.dfssi.dataplatform.userhome.service.DicItemService;
import com.dfssi.dataplatform.userhome.service.FieldRulesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典字表表管理控制
 * @author dingsl
 * @since 2018/10/12
 */
@RestController
@RequestMapping("/dicItem")
@Api(value = "DicItemController",description = "字典子表管理控制")
public class DicItemController {

 @Autowired
    private DicItemService dicItemService;

 @Autowired
    private FieldRulesService fieldRulesService;

    /**
     * 分页查询字典表中的实体信息
     * @param pageParam
     * @param entity
     * @return
     */
    @RequestMapping(value = "/alldicItem",method = RequestMethod.GET)
    @ApiOperation(value = "分页查询字典表中的实体信息")
    @LogAudit
    public ResponseObj getAllDic(PageParam pageParam,DicItemEntity entity) {
        ResponseObj result=ResponseObj.createResponseObj();
        try {
            PageResult<DicItemEntity> pageResult=dicItemService.listGetDicItem(pageParam,entity);
            result.setData(pageResult);
            result.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return result;
    }

    /**
     * 新增/修改字典字表信息
     * @param entity
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/savedicItem",method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改字典字表信息")
    @LogAudit
    public ResponseObj saveDic(@RequestBody @Validated DicItemEntity entity, BindingResult bindingResult){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String str =fieldRulesService.paramValid(bindingResult);
            if(StringUtils.isNotEmpty(str)){
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_P,"参数格式不正确！",str);
                return responseObj;
            }
            String result = dicItemService.insert(entity);
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

    /**
     * 根据主键批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ApiOperation(value = "根据主键批量删除")
    @LogAudit
    public ResponseObj deleteByType(String ids){
        ResponseObj responseObj=ResponseObj.createResponseObj();
        String result=dicItemService.deleteByIds(ids);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return  responseObj;
    }

    /**
     * 根据类型显示字典字表信息
     * @param dicType
     * @return
     */
    @RequestMapping(value = "/dicItemValue",method = RequestMethod.GET)
    @ApiOperation(value = "根据类型显示字典字表信息")
    @LogAudit
    public ResponseObj listGetByDicType(String dicType){
        ResponseObj result=ResponseObj.createResponseObj();
        try {
            List<DicItemEntity> list=dicItemService.listGetByDicType(dicType);
            result.setData(list);
            result.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"异常",e.getMessage());
        }
        return result;
    }

    /**
     * 接入任务显示数据源对应的所有有效的字典子表
     * @return
     */
    @RequestMapping(value = "/taskdatasourcedicItem",method = RequestMethod.POST)
    @ApiOperation(value = "接入任务显示数据源对应的所有有效的字典子表")
    @LogAudit
    public List<DicItemEntity> listGetValidSource() {
        return dicItemService.listGetValidSource();
    }

    /**
     * 接入任务数据资源对应的所有有效的字典子表
     * @return
     */
    @RequestMapping(value = "/taskdataresourcedicItem",method = RequestMethod.POST)
    @ApiOperation(value = "(接入任务显示数据资源对应的所有有效的字典子表")
    @LogAudit
    public List<DicItemEntity> listGetValidResource(){
        return dicItemService.listGetValidResource();
    }

    /**
     * 接入任务显示数据资源对应的所有有效的字典子表
     * @return
     */
    @RequestMapping(value = "/dataresourcedicItem",method = RequestMethod.GET)
    @ApiOperation(value = "数据资源对应的所有有效的字典子表")
    @LogAudit
    public ResponseObj listGetValidDataItem() {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            List<DicItemEntity> list=dicItemService.listGetValidDataItem();
            responseObj.setData(list);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return responseObj;
    }

    /**
     * 根据类型预览字表信息
     * @param dicType
     * @return
     */
    @RequestMapping(value = "/listSelectDataItem",method = RequestMethod.GET)
    @ApiOperation(value = "根据类型预览字表信息")
    @LogAudit
    public ResponseObj listSelectDataItem(String dicType){
        ResponseObj responseObj=ResponseObj.createResponseObj();
        try {
            List<DicItemEntity> list=dicItemService.listSelectDataItem(dicType);
            responseObj.setData(list);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return responseObj;
    }
}
