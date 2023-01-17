package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.EmailAlertConfigEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAlertConfigDao extends CrudDao<EmailAlertConfigEntity> {

    public List<EmailAlertConfigEntity> getConfigByModelId(String modelId);

    public List<String> getUserIdsByModelId(String modelId);

    public List<String> getRuleIdsByModelId(String modelId);

    public void deleteByModelId(String modelId);

}