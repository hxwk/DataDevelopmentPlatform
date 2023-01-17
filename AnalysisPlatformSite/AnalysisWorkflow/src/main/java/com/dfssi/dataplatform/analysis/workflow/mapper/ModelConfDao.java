package com.dfssi.dataplatform.analysis.workflow.mapper;

import com.dfssi.dataplatform.analysis.workflow.entity.ModelConfEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModelConfDao extends BaseDao<ModelConfEntity> {

    public String getModelConf(String modelId);

}
