package com.dfssi.dataplatform.analysis.dao;


import com.dfssi.dataplatform.analysis.entity.AnalysisLinkEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkDao extends CrudDao<AnalysisLinkEntity> {

    public void deleteByModelId(String modelId);

    public List<AnalysisLinkEntity> getLinksByModelId(String modelId);

    public void batchInsert(List<AnalysisLinkEntity> linkEntities);

}
