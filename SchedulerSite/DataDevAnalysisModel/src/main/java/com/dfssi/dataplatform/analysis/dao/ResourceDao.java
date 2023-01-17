package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.AnalysisResourceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ResourceDao extends CrudDao<AnalysisResourceEntity> {

    public List<AnalysisResourceEntity> listResourceAllTable(Map params);

    public List<AnalysisResourceEntity> listResource(Map params);

    public List<AnalysisResourceEntity> listAllSources();

    public void deleteByResourceId(String resourceId);

}
