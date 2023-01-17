package com.dfssi.dataplatform.ide.access.mvc.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.access.mvc.entity.ResponseObjectEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskModelEntity;
import com.dfssi.dataplatform.ide.access.mvc.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"接入任务控制器"})
@RestController
@RequestMapping(value = "ide/task/access")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    /**
     * 接入任务分页查询和条件查询
     * @param entity
     * @param pageParam
     * @return
     */
    @ApiOperation(value = " 获取接入任务分页列表")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj list(@Validated TaskAccessInfoEntity entity, BindingResult bindingResult, PageParam pageParam) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String str = taskService.paramValid(bindingResult);
        if(StringUtils.isNotEmpty(str)){
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",str);
            return responseObj;
        }
        try {
            PageResult<TaskAccessInfoEntity> pageResult = taskService.findeEntityList(entity,pageParam);
            responseObj.setData(pageResult);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Exception e) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",e.getMessage());
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * 接入任务新增接口
     * @param entity
     * @return
     */
    @ApiOperation(value = " 接入任务新增")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj saveModel(@RequestBody @Validated TaskModelEntity entity, BindingResult bindingResult) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String str = taskService.paramValid(bindingResult);
        if(StringUtils.isNotEmpty(str)){
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",str);
            return responseObj;
        }
        try {
            ResponseObjectEntity result = (ResponseObjectEntity)taskService.saveTaskModel(entity);
            if(result.getFlag()){
                responseObj.setData(result);
                responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
            }else{
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",result.getMessage());
            }
        } catch (Exception e) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",e.getMessage());
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * 根据任务实体查询任务详情
     * @param entity
     * @return
     */
    @ApiOperation(value = " 查询实体方法")
    @RequestMapping(value = "get", method = RequestMethod.POST)
    @LogAudit
    public ResponseObj getTaskModel(@RequestBody TaskAccessInfoEntity entity) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            Object result = taskService.getTaskModel(entity);
            responseObj.setData(result);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Exception e) {
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",e.getMessage());
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * 根据任务ID获取详情
     * @param taskId
     * @return
     */
    @ApiOperation(value = " 根据任务ID获取详情")
    @RequestMapping(value = "getDetail", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj getDetailByTaskId(@RequestParam("taskId") String taskId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            Object result=taskService.getDetailByTaskId(taskId);
            responseObj.setData(result);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Exception e) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",e.getMessage());
            e.printStackTrace();
        }
        return  responseObj;
    }

}
