package com.dfssi.dataplatform.ide.access.mvc.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.access.mvc.constants.Constants;
import com.dfssi.dataplatform.ide.access.mvc.entity.AccessLeftMenuItemsEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDataresourceAccessInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDatasourceAccessInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.ResponseObjectEntity;
import com.dfssi.dataplatform.ide.access.mvc.service.IFeignService;
import com.dfssi.dataplatform.ide.access.mvc.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.util.*;


/**
 * Created by hongs on 2018/4/27.
 */
@Api(tags = {"接入模块控制器"})
@RestController
@RequestMapping(value = "task/access")
public class AccessModelController{
    protected Logger logger = LoggerFactory.getLogger(AccessModelController.class);

    @Autowired
    RestTemplate resttemplate;

//    @Value("${access.service.url}")
//    private String accessServiceUrl;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IFeignService feignService;

    /**
     * 获取接入任务新增和修改左边树形菜单栏
     */
    @ApiOperation(value = " 获取接入任务左边树形菜单栏")
    @RequestMapping(value = "leftitems", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj getMenuItems() {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            AccessLeftMenuItemsEntity accessLeftMenu = feignService.getLeftMenus();
            responseObj.setData(accessLeftMenu);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Throwable t) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",t.getMessage());
        }
        return responseObj;
    }

    /**
     * 根据数据源主表的主键ID查询数据源子表配置信息，
     * 用于新增修改时右侧参数配置信息展示，和启动任务获取数据源配置信息
     */
    @ApiOperation(value = " 根据数据源ID查询数据源配置信息")
    @RequestMapping(value = "getSourceAccessInfoBySrcId/{srcId}",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj getSourceAccessInfoBySrcId(@PathVariable String srcId)
    {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            //微服务调用，根据数据源主表的主键ID查询数据源子表配置信息
            List<MetaDatasourceAccessInfoEntity> lis = feignService.getSourceAccessInfoBySrcId(srcId);
            List<Map<String, Object>> resMap=new ArrayList() ;
            for(MetaDatasourceAccessInfoEntity metaDatasourceAccessInfo:lis){
                Map<String, Object> map =beanToMap(metaDatasourceAccessInfo);
                resMap.add(map);
            }
            responseObj.setData(resMap);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Throwable t) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",t.getMessage());
        }
        return responseObj;
    }

    /**
     * 根据数据资源主表的主键id查询数据资源子表配置信息
     * 用于新增修改时右侧参数配置信息展示，和启动任务获取数据资源配置信息
     */
    @ApiOperation(value = " 根据数据资源ID查询数据资源配置信息")
    @RequestMapping(value = "getResourceAccessInfoByResId/{resId}",method = RequestMethod.POST)
    @LogAudit
    public ResponseObj getResourceAccessInfoByResId(@PathVariable String resId)
    {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            //微服务调用
            List<MetaDataresourceAccessInfoEntity> lis = feignService.getResourceAccessInfoByResId(resId);
            responseObj.setData(lis);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Throwable t) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",t.getMessage());
        }
        return responseObj;
    }

    /**
     * 生成新的UUID，该id用作task_access_step_info表的主鍵id，具有关联作用
     */
    @ApiOperation(value = " 生成新的UUID")
    @RequestMapping(value = "getnewid", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj getStepId() {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String newId = UUID.randomUUID().toString().replace("-", "").toLowerCase();;
            responseObj.setData(newId);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (Throwable t) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",t.getMessage());
        }
        return responseObj;
    }

     /**
      * 删除单条任务列表数据
      */
     @ApiOperation(value = " 删除单条任务列表数据")
     @RequestMapping(value = "deleteTaskSingle", method = RequestMethod.POST)
     @LogAudit
     public ResponseObj deleteTaskSingleByTaskId(@RequestBody String taskId) {
         ResponseObj responseObj = ResponseObj.createResponseObj();
         try {
             Object result=taskService.deleteTaskSingleByTaskId(taskId);
             responseObj.setData(result);
             responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
         } catch (Exception e) {
             responseObj.setData("");
             responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",e.getMessage());
             e.printStackTrace();
         }
         return responseObj;
     }

     /**
      * 启动或停止任务
      */
     @ApiOperation(value = " 启动或停止任务")
     @RequestMapping(value = "getTaskStart", method = RequestMethod.POST)
     @LogAudit
    public ResponseObj getTaskStart(@RequestBody Map<String,String> map){
         ResponseObj responseObj = ResponseObj.createResponseObj();
         ResponseObjectEntity ro = taskService.taskStartOrStop(map);
         if(ro.getFlag()){
             responseObj.setData("");
             responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！",ro.getMessage());
         }else{
             responseObj.setData("");
             responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",ro.getMessage());
         }
         return responseObj;
    }

    /**
     * 新增修改点击提交按钮时会获取存活的客户端列表
     * @param id  数据源id
     * @return
     */
    @ApiOperation(value = " 获取存活的客户端列表")
    @RequestMapping(value = "clientList/{id}", method = RequestMethod.GET)
    @LogAudit
    public ResponseObj clientList(@PathVariable String id){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String masterUrl = taskService.getMasterUrl(id);
            JSONObject json = taskService.getAliveClientList(masterUrl);
            responseObj.setData(json);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功！","");
        } catch (RestClientException e) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！",e.getMessage());
            e.printStackTrace();
        }
        return responseObj;
    }

    /**
     * bean转map
     */
    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> params = new HashMap<String, Object>(0);
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (!Constants.S_CLASS_NAME.equals(name)) {
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


}
