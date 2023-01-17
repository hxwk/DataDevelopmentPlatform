package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.EmailAlertConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmailAlertConfigDao extends BaseDao<EmailAlertConfigEntity> {

    List<EmailAlertConfigEntity> getConfigByModelId(String modelId);

    List<String> getUserIdsByModelId(String modelId);

    List<String> getRuleIdsByModelId(String modelId);

    void deleteByModelId(String modelId);

}