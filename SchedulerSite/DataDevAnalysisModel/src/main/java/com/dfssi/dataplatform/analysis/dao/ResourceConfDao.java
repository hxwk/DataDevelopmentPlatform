package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.AnalysisResourceConfEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceConfDao extends CrudDao<AnalysisResourceConfEntity> {

    public List<AnalysisResourceConfEntity> getResourceConf(String resourceId);

    public void deleteByResourceId(String resourceId);
}
