package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.AnalysisSourceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SourceDao extends CrudDao<AnalysisSourceEntity> {

    public List<AnalysisSourceEntity> listAllSources(Map params);
    public AnalysisSourceEntity getByDataresourceId(String dataresourceId);
}
