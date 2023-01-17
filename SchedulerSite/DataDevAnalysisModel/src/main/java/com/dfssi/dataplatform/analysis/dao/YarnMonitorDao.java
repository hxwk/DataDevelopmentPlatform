package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.YarnMonitorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface YarnMonitorDao extends CrudDao<YarnMonitorEntity> {

    public YarnMonitorEntity getById(String id);

    public List<YarnMonitorEntity> getByModelId(String modelId);

    public List<YarnMonitorEntity> getByOozieId(String oozieTaskId);

    public List<YarnMonitorEntity> getByStatus(String status);

    public List<String> getOozieIdByModelId(String modelId);

    public List<String> getApplicationIdByOozieId(String oozieTaskId);

    public void updateStatusByModelId(Map params);

    public void updateStatusByOozieTaskId(Map params);

    public void updateStatusByApplicationId(Map params);

    public void updateWhenStart(Map params);

    public void deleteByModelId(String modelId);

    public List<String> listRunningApplicationIdsByOozieId(String oozieTaskId);
}