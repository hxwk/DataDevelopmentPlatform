package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.AnalysisSourceConfEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceConfDao extends CrudDao<AnalysisSourceConfEntity> {

    public List<AnalysisSourceConfEntity> getSourceConf(String dataresourceId);
}
