package com.dfssi.dataplatform.ide.access.mvc.service;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessStepAttrEntity;

import java.util.List;

/**
 * @author
 * @date 2018/9/17
 * @description 接入任务步骤属性增删改查模块
 */
public interface ITaskAccessStepAttrService {
    /**
     * 逻辑删除接入任务步骤属性表
     * @param taskId
     * @return
     */
    int deleteByTaskId(String taskId);

    /**
     * 保存接入任务步骤属性信息
     * @param taskAccessStepAttrEntitiesnew
     * @return
     */
    int insertMutil(List<TaskAccessStepAttrEntity> taskAccessStepAttrEntitiesnew);

    /**
     * 根据任务id查询接入任务步骤属性信息
     * @param taskId
     * @return
     */
    List<TaskAccessStepAttrEntity> findListByTaskId(String taskId);

    /**
     * 根据任务id和步骤id查询
     * @param taskId
     * @param stepId
     * @return
     */
    List<TaskAccessStepAttrEntity> findListByTaskIdandStepId(String taskId,String stepId);
}