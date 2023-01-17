package com.dfssi.dataplatform.userhome.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.userhome.entity.DicEntity;
import com.dfssi.dataplatform.userhome.service.DicService;
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
 * 字典主表管理控制
 * @author dingsl
 * @since 2018/10/12
 */
@RestController
@RequestMapping("/dic")
@Api(value = "DicController",description = "字典主表管理控制")
public class DicController {

    @Autowired
    private DicService dicService;

    @Autowired
    private FieldRulesService fieldRulesService;

   @RequestMapping(value = "/alldic",method = RequestMethod.GET)
   @ApiOperation(value = "分页查询字典表中的实体信息")
   @LogAudit
   public ResponseObj getAllDic(PageParam pageParam,DicEntity entity) {
       ResponseObj result=ResponseObj.createResponseObj();
       try {
            PageResult<DicEntity> pageResult=dicService.listGetDic(pageParam,entity);
            result.setData(pageResult);
            result.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/savedic",method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改字典信息")
    @LogAudit
    public ResponseObj saveDic(@RequestBody @Validated DicEntity entity, BindingResult bindingResult){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String str=fieldRulesService.paramValid(bindingResult);
            if(StringUtils.isNotEmpty(str)){
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_P,"参数格式不正确！",str);
                return responseObj;
            }
            String result = dicService.insert(entity);
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

 @RequestMapping(value = "/delete",method = RequestMethod.GET)
 @ApiOperation(value = "根据字典类型删除字典主表",notes = "字典字表对应信息一起删除")
 @LogAudit
  public ResponseObj deleteByType(String dicType){
       ResponseObj responseObj=ResponseObj.createResponseObj();
     try {
         String result=dicService.deleteByType(dicType);
         if (StringUtils.isEmpty(result)) {
             responseObj.setData("");
             responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
         }
     } catch (Exception e) {
         e.printStackTrace();
         responseObj.setData("");
         responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
     }
     return  responseObj;
  }

  @RequestMapping(value = "/dicType",method = RequestMethod.GET)
  @ApiOperation(value = "显示字典主表有效类型信息",notes = "供字典子表新增修改时选择dicType")
  @LogAudit
  public ResponseObj listGetDicType(){
       ResponseObj result=ResponseObj.createResponseObj();
      try {
          List<DicEntity> list=dicService.listGetDicType();
          result.setData(list);
          result.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
      } catch (Exception e) {
          e.printStackTrace();
          result.setData("");
          result.setStatus(ResponseObj.CODE_FAIL_B,"失败",e.getMessage());
      }
      return result;
  }

}
