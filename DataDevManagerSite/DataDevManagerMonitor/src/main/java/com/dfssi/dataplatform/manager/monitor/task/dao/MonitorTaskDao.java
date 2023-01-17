package com.dfssi.dataplatform.manager.monitor.task.dao;


import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MonitorTaskDao extends CrudDao<MonitorTaskEntity> {
    public List<MonitorTaskEntity> listAllTasks(Map params);

    public MonitorTaskEntity getByTaskId(String taskId);
}
