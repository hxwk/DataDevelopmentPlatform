package com.dfssi.dataplatform.manager.monitor.task.dao;


import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskAttrEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorTaskAttrDao extends CrudDao<MonitorTaskAttrEntity> {

    public void deleteByTaskId(String taskId);

    public void batchInsert(List<MonitorTaskAttrEntity> attrs);

}
