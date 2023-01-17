package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.OfflineAnalysisRecEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfflineAnalysisRecDao extends CrudDao<OfflineAnalysisRecEntity> {
    public List<String> getOozieId(String modeId);

    public void updateStatus(String modelId);

    public String getLastJobId(String modelId);
}