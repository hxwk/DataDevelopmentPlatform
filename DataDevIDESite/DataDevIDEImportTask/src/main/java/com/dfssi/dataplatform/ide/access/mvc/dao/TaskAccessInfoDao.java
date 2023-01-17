package com.dfssi.dataplatform.ide.access.mvc.dao;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskAccessInfoDao{
    /**
     * 接入任务分页和条件查询
     * @param entity
     * @return
     */
    List<TaskAccessInfoEntity> findList(TaskAccessInfoEntity entity);

    /**
     * 更新接入任务信息表
     * @param taskAccessInfoEntity
     * @return
     */
    int update(TaskAccessInfoEntity taskAccessInfoEntity);

    /**
     * 插入接入任务信息表
     * @param taskAccessInfoEntity
     * @return
     */
    int insert(TaskAccessInfoEntity taskAccessInfoEntity);

    /**
     * 根据任务id获取详情
     * @param id
     * @return
     */
    TaskAccessInfoEntity get(String id);

    /**
     * 根据taskId删除单条任务记录
     * @param id
     * @return
     */
    int delete(String id);

    /**
     * 根据任务id改变运行状态
     * @param taskId
     * @param status
     * @return
     */
    int updateStatus(@Param("taskId") String taskId,@Param("status") String status);

    /**
     * 接入名不能重复
     * @param taskAccessInfoEntity
     * @return
     */
    List<TaskAccessInfoEntity> queryRepeatName(TaskAccessInfoEntity taskAccessInfoEntity);

}
