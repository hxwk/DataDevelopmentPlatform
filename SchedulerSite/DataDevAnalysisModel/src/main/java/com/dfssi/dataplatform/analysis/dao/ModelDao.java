package com.dfssi.dataplatform.analysis.dao;


import com.dfssi.dataplatform.analysis.entity.AnalysisModelEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ModelDao extends CrudDao<AnalysisModelEntity> {
    public List<AnalysisModelEntity> listAllModels(Map params);
    public void updateStatus(Map params);
    public List<AnalysisModelEntity> listRunningModels();

}
