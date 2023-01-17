package com.dfssi.dataplatform.devmanager.analysistask.mvc.service;



import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisApplicationFormEntity;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisModelEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by cxq on 2018/1/2.
 */
public interface IAnalysisManagerTaskService {

    List<AnalysisModelEntity> findEntityList(AnalysisModelEntity entity);


    Map<String, Object> verifyTask(AnalysisApplicationFormEntity entity);

    Map<String, Object> getApplicationformById(String id);
}
