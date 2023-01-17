package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.CoordTaskMonitorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CoordTaskMonitorDao extends CrudDao<CoordTaskMonitorEntity> {

    public CoordTaskMonitorEntity getById(String id);

    public List<CoordTaskMonitorEntity> getByModelId(String modelId);

    public List<CoordTaskMonitorEntity> getByStatus(String status);

    public List<String> getOozieIdByModelId(String modelId);

    public void updateStatusByModelId(Map params);

    public void updateStatusByOozieTaskId(Map params);

    public void updateWhenStart(Map params);

    public void deleteByModelId(String modelId);

    public List<CoordTaskMonitorEntity> listRunningCoordTasksByModelId(String modelId);
}