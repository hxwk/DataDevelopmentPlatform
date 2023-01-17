package com.dfssi.dataplatform.manager.service.service;

import com.dfssi.dataplatform.analysis.entity.AnalysisModelEntity;
import com.dfssi.dataplatform.common.service.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "serviceManagerService")
@Transactional(value = "analysis", readOnly = true)
public class ServiceManagerService extends AbstractModelService {

    public List<AnalysisModelEntity> listAllServiceModels(int pageIdx, int pageSize, String modelName, Long
            startTime, Long endTime, String status) {
        return this.listAllModels(pageIdx, pageSize, MODEL_TYPE_SERVICE_DATA, modelName, startTime, endTime, status);
    }

}
