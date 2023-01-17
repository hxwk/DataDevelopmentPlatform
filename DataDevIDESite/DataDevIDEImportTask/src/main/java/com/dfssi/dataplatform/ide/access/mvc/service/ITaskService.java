package com.dfssi.dataplatform.ide.access.mvc.service;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.access.mvc.entity.ResponseObjectEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskModelEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskStartEntity;
import org.springframework.validation.BindingResult;

import java.util.Map;

/**
 * 接入任务service
 */
public interface ITaskService {

    /**
     * 获取接入任务分页查询和条件查询列表
     * @param entity
     * @param pageParam
     * @return
     */
    PageResult<TaskAccessInfoEntity> findeEntityList(TaskAccessInfoEntity entity, PageParam pageParam);

    /**
     * 逻辑删除单条任务数据
     * @param taskId
     * @return
     */
    Object deleteTaskSingleByTaskId(String taskId);

    /**
     *任务启停
     * @param map
     * @return
     */
    ResponseObjectEntity taskStartOrStop(Map<String,String> map);

    /**
     * 拼接启动或停止任务报文
     * @param map
     * @return
     */
    TaskStartEntity getTaskStartMessage(Map<String,String> map);

    /**
     * 接入任务新增接口
     * @param taskModel
     * @return
     */
    Object saveTaskModel (TaskModelEntity taskModel);

    /**
     * 根据任务实体获取任务详情
     * @param taskAccessInfoEntity
     * @return
     */
    Object getTaskModel (TaskAccessInfoEntity taskAccessInfoEntity);

    /**
     * 根据任务id获取详情
     * @param taskId
     * @return
     */
    Object getDetailByTaskId (String taskId);

    /**
     * 根据任务id更新运行状态
     * @param taskId
     * @param status
     * @return
     */
    int updateStatus(String taskId, String status);

    /**
     * 根据masterUrl获取存活的客户端
     * @param url  masterUrl
     * @return
     */
    JSONObject getAliveClientList(String url);

    /**
     * 参数校验异常捕获判断
     * @param bindingResult
     * @return  校验参数合法性，校验的信息会存放在bindingResult
     */
    String paramValid(BindingResult bindingResult);

    /**
     * 根据任务id获取步骤信息数据，分离出数据源主表的主键id，数据源类型，
     *  数据资源主表的主键id，数据资源类型，通道类型，清洗转换
     * @param taskId
     * @return
     */
    Map getMessageByTaskId(String taskId);

    /**
     * 根据数据源id获取masterUrl
     * @param dataSourceId
     * @return
     */
    String getMasterUrl(String dataSourceId);


}
