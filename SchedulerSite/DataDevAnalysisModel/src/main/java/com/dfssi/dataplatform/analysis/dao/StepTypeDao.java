package com.dfssi.dataplatform.analysis.dao;


import com.dfssi.dataplatform.analysis.entity.AnalysisStepEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisStepTypeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepTypeDao extends CrudDao<AnalysisStepTypeEntity> {

    public List<AnalysisStepTypeEntity> getAllStepTypes(String stepGroup);

}
