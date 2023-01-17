package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.EmailAlertRecordEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmailAlertRecordDao extends BaseDao<EmailAlertRecordEntity> {

    List<EmailAlertRecordEntity> getRecordByModelId(String modelId);

    void deleteByModelId(String modelId);

}