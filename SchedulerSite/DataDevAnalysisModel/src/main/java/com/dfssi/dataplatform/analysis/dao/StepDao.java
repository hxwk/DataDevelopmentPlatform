package com.dfssi.dataplatform.analysis.dao;


import com.dfssi.dataplatform.analysis.entity.AnalysisStepEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepDao extends CrudDao<AnalysisStepEntity> {

    public void deleteByModelId(String modelId);

    public List<AnalysisStepEntity> getStepsByModelId(String modelId);

    public void batchInsert(List<AnalysisStepEntity> stepEntities);

}
