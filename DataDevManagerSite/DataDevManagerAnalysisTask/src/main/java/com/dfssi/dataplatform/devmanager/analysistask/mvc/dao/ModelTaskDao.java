package com.dfssi.dataplatform.devmanager.analysistask.mvc.dao;


import com.dfssi.dataplatform.devmanager.analysistask.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisModelEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelTaskDao extends BaseDao<AnalysisModelEntity> {
    List<AnalysisModelEntity> listAllModels(AnalysisModelEntity entity);
}
