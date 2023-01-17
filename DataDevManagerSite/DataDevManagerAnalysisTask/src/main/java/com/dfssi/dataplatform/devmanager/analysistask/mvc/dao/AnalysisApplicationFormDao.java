package com.dfssi.dataplatform.devmanager.analysistask.mvc.dao;


import com.dfssi.dataplatform.devmanager.analysistask.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisApplicationFormEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisApplicationFormDao extends BaseDao<AnalysisApplicationFormEntity> {

    List<AnalysisApplicationFormEntity> getById(String id);

}
