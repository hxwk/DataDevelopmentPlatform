package com.dfssi.dataplatform.ide.cleantransform.mvc.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.SerResponseEntity;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformEntity;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformModelEntity;
import com.dfssi.dataplatform.ide.cleantransform.mvc.service.ICleanTransformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by hongs on 2018/5/10.
 * 清洗转换控制器
 */
@Api(tags = {"清洗转换控制器"})
@RestController
@RequestMapping(value = "/ide/task/cleantransform/")
public class CleanTransformController {
    @Autowired
    private ICleanTransformService cleanTransformService;


    /**
    * 清洗转换新增保存
    */
    @ApiOperation(value = "清洗转换新增保存")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj saveLists(@RequestBody List<String> lists) {
        ResponseObj result = ResponseObj.createResponseObj();
        try {
            int i = cleanTransformService.saveCleanTransformModel(lists);
            if(i > 0){
                result.setData(i);
                result.setStatus(ResponseObj.CODE_SUCCESS,"成功！","保存信息成功");
            }else{
                result.setData(i);
                result.setStatus(ResponseObj.CODE_FAIL_B,"失败！","保存信息失败");
            }
        } catch (Exception e) {
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 清洗转换字段映射删除操作
     */
    @ApiOperation(value = "清洗转换字段映射删除操作")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj delete(String id) {
        ResponseObj result= ResponseObj.createResponseObj();
        try {
            int i = cleanTransformService.deleteMappingById(id);
            if(i > 0){
                result.setData(i);
                result.setStatus(ResponseObj.CODE_SUCCESS,"成功！","删除信息成功");
            }else{
                result.setData(i);
                result.setStatus(ResponseObj.CODE_FAIL_B,"失败！","删除信息失败");
            }
        } catch (Exception e) {
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
    * 清洗转换修改操作
    */
    @ApiOperation(value = " 清洗转换修改操作")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj updateModel(@RequestBody String jsonStr) {
        ResponseObj result= ResponseObj.createResponseObj();
        try {
            int i = cleanTransformService.updateCleanTransformModel(jsonStr);
            if(i > 0){
                result.setData(i);
                result.setStatus(ResponseObj.CODE_SUCCESS,"成功！","更新信息成功");
            }else{
                result.setData(i);
                result.setStatus(ResponseObj.CODE_FAIL_B,"失败！","更新信息失败");
            }
        } catch (Exception e) {
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
    * 清洗转换任务查询所有列表
    */
    @ApiOperation(value = " 清洗转换任务查询所有列表")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj list() {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            responseObj.setData(this.cleanTransformService.findeEntityList());
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","Get list successfully");
        } catch (Throwable t) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!",t.getMessage());
        }
        return responseObj;
    }

    /**
     * 查询字段映射主表信息
     * @param entity
     * @return
     */
    @ApiOperation(value = " 查询字段映射主表信息")
    @RequestMapping(value = "getMappings", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj getMappings(@Validated TaskCleanTransformEntity entity,BindingResult bindingResult, PageParam pageParam) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        //校验参数合法性，校验的信息会存放在bindingResult
        String str = cleanTransformService.paramValid(bindingResult);
        if(StringUtils.isNotEmpty(str)){
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",str);
            return responseObj;
        }
        try {
            PageResult<TaskCleanTransformEntity> pageResult = cleanTransformService.getMappings(entity,pageParam);
            responseObj.setData(pageResult);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","查询字段映射主表信息成功");
        } catch (Exception e) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!","查询字段映射主表信息失败");
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * 保存字段映射主表和子表信息
     */
    @ApiOperation(value = " 保存字段映射主表和子表信息")
    @RequestMapping(value = "saveModel", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj saveModel(@RequestBody @Validated TaskCleanTransformModelEntity entity, BindingResult bindingResult) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        //校验参数合法性，校验的信息会存放在bindingResult
        String str = cleanTransformService.paramValid(bindingResult);
        if(StringUtils.isNotEmpty(str)){
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",str);
            return responseObj;
        }
        try {
            SerResponseEntity result=cleanTransformService.saveModel(entity);
            if(result.getFlag()){
                responseObj.setData(result);
                responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","保存信息成功");
            }else{
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!",result.getMessage());
            }
        } catch (Exception e) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * 清洗转换字段映射根据id查询主表和子表信息
     * @return
     */
    @ApiOperation(value = " 清洗转换字段映射根据id查询主表和子表信息")
    @RequestMapping(value = "getModel", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj getModel(String id) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            Object result=cleanTransformService.getModel(id);
            responseObj.setData(result);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","获取信息成功");
        } catch (Exception e) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * 根据id删除单条主表数据
     * @param id
     * @return
     */
      @ApiOperation(value = " 根据id删除单条主表数据")
      @RequestMapping(value = "deleteCleanTransformSingle", method = RequestMethod.POST)
      @LogAudit
      public ResponseObj deleteTaskSingleByTaskId(String id) {
          ResponseObj responseObj = ResponseObj.createResponseObj();
          try {
              Object result=cleanTransformService.deleteSingleByMappingId(id);
              responseObj.setData(result);
              responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","删除信息成功");
          } catch (Exception e) {
              responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
              e.printStackTrace();
          }
          return responseObj;
      }

    /**
     * 根据映射ID查询子表数据
     * @param
     * @return
     */
      @ApiOperation(value = " 根据映射ID查询子表数据")
      @RequestMapping(value = "findListByMappingId", method = RequestMethod.POST)
      @LogAudit
      public ResponseObj findListByMappingId(@RequestBody String mappingID) {
          ResponseObj responseObj = ResponseObj.createResponseObj();
          try {
              Object result=cleanTransformService.findListByMappingId(mappingID);
              responseObj.setData(result);
              responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","查询子表信息成功");
          } catch (Exception e) {
              responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败!",e.getMessage());
              e.printStackTrace();
          }
          return responseObj;
      }

    /**
     * 查询清洗转换主表数据,用于接入任务里面微服务调用，直接返回Object，不使用ResponseObj返回
     * @return
     */
    @ApiOperation(value = " 查询清洗转换主表数据")
    @RequestMapping(value = "/getAllCleanTranses", method = RequestMethod.GET)
    @LogAudit
    public Object getAllCleanTranses(){
        //这里不使用ResponseObj返回
        return cleanTransformService.getAllCleanTranses();
    }
    /**
     * 查询清洗转换子表数据,用于接入任务里面微服务调用，直接返回Object，不使用ResponseObj返回
     * @return
     */
    @ApiOperation(value = " 查询清洗转换主表数据")
    @RequestMapping(value = "/micro/getList", method = RequestMethod.POST)
    @LogAudit
    public Object getListByMappingId(@RequestBody String mappingID) {
        return cleanTransformService.findListByMappingId(mappingID);
    }




}
