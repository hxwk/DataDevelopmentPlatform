package com.dfssi.dataplatform.ide.access.mvc.service;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessStepInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2018/9/17
 * @description 接入任务操作步骤信息模块
 */
public interface ITaskAccessStepInfoService {
    /**
     * 根据taskId查询接入步骤信息
     * @param taskId
     * @return
     */
    List<TaskAccessStepInfoEntity> findListByTaskId(String taskId);

    /**
     * 根据任务id物理删除步骤信息
     * @param taskId
     * @return
     */
    int deleteByTaskId(String taskId);

    /**
     * 逻辑删除步骤信息表数据
     * @param map
     * @return
     */
    int deleteByTaskAccessStepInfoMap(Map map);

    /**
     * 插入步骤信息表
     * @param taskAccessStepInfoEntitiesnew
     * @return
     */
    int insertMutil(List<TaskAccessStepInfoEntity> taskAccessStepInfoEntitiesnew);

}