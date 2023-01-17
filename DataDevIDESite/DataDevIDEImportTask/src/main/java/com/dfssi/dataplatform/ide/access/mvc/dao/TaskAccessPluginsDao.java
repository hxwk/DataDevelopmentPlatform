package com.dfssi.dataplatform.ide.access.mvc.dao;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessPluginsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface TaskAccessPluginsDao{
    /**
     * 获取任务步骤组件信息，表task_access_plugins
     * @return
     */
    List<TaskAccessPluginsEntity> getAllPluginEntities();
}
