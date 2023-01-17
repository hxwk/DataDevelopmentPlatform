package com.dfssi.dataplatform.analysis.service.mapper;

import com.dfssi.dataplatform.analysis.service.entity.ServiceModelEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceModelDao extends BaseDao<ServiceModelEntity> {

    List<ServiceModelEntity> listModels(Map params);

    String getModelConf(String modelId);
}
