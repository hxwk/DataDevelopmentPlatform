package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.EmailAlertRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAlertRecordDao extends CrudDao<EmailAlertRecordEntity> {

    public List<EmailAlertRecordEntity> getRecordByModelId(String modelId);

    public void deleteByModelId(String modelId);

}