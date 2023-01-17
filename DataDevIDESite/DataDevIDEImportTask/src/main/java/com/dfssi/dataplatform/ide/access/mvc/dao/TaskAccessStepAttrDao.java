package com.dfssi.dataplatform.ide.access.mvc.dao;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessStepAttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskAccessStepAttrDao {
    /**
     * 插入接入任务步骤属性信息
     * @param columnEntities
     * @return
     */
    int insertMutil(List<TaskAccessStepAttrEntity> columnEntities);

    /**
     *逻辑删除接入任务步骤属性信息
     * @param taskId
     * @return
     */
    int deleteByTaskId(String taskId);

    /**
     * 根据任务id查询接入任务步骤属性信息
     * @param taskId
     * @return
     */
    List<TaskAccessStepAttrEntity> findListByTaskId(String taskId);

    /**
     * 根据步骤id和任务id查询
     * @param taskId
     * @param stepId
     * @return
     */
    List<TaskAccessStepAttrEntity> findListByTaskIdandStepId(@Param("taskId") String taskId,@Param("stepId")String stepId);
}
