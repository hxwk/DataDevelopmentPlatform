package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.ModelInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ModelInfoDao extends BaseDao<ModelInfoEntity> {

    List<ModelInfoEntity> listModels(Map params);

    List<ModelInfoEntity> listRunningModels();

    void updateStatus(Map params);

    ModelInfoEntity getByModelId(String modelId);
}
