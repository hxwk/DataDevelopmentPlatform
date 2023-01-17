package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.ModelConfEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModelConfDao extends BaseDao<ModelConfEntity> {

    String getModelConf(String modelId);

}
