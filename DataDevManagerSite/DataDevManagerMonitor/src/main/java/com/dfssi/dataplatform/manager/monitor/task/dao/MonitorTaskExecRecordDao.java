package com.dfssi.dataplatform.manager.monitor.task.dao;


import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskExecRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MonitorTaskExecRecordDao extends CrudDao<MonitorTaskExecRecordEntity> {

    public List<MonitorTaskExecRecordEntity> getExecRecordsByTaskId(Map params);

    public void deleteByTaskId(String taskId);

    public List<MonitorTaskExecRecordEntity> listOozieTasks(Map params);

    public void updateTaskStatus(MonitorTaskExecRecordEntity mtere);

}
