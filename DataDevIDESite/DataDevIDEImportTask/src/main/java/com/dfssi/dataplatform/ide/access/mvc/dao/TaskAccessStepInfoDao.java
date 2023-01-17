package com.dfssi.dataplatform.ide.access.mvc.dao;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessStepInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskAccessStepInfoDao {
    /**
     * 插入步骤信息表
     * @param columnEntities
     * @return
     */
    int insertMutil(List<TaskAccessStepInfoEntity> columnEntities);

    /**
     * 物理删除步骤信息
     * @param taskId
     * @return
     */
    int deleteByTaskId(String taskId);

    /**
     * 逻辑删除步骤信息
     * @param map
     * @return
     */
    int deleteByTaskAccessStepInfoMap(Map map);

    /**
     *根据任务id查询步骤信息
     * @param taskId
     * @return
     */
    List<TaskAccessStepInfoEntity> findListByTaskId(String taskId);


}
