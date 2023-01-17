package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.TaskMonitorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TaskMonitorDao extends CrudDao<TaskMonitorEntity> {

    public TaskMonitorEntity getById(String id);

    public List<TaskMonitorEntity> getByModelId(String modelId);

    public List<TaskMonitorEntity> getByStatus(String status);

    public List<String> getOozieIdByModelId(String modelId);

    public void updateStatusByModelId(Map params);

    public void updateStatusByOozieTaskId(Map params);

    public void updateWhenStart(Map params);

    public void deleteByModelId(String modelId);

    public List<TaskMonitorEntity> listRunningTasksByModelId(String modelId);

}