package com.dfssi.dataplatform.analysis.dao;


import com.dfssi.dataplatform.analysis.entity.AnalysisStepAttrEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttrDao extends CrudDao<AnalysisStepAttrEntity> {

    public void batchDelete(List<AnalysisStepAttrEntity> deletedAttrs);

    public void deleteByModelId(String modelId);

    public void batchInsert(List<AnalysisStepAttrEntity> attrEntities);
}
