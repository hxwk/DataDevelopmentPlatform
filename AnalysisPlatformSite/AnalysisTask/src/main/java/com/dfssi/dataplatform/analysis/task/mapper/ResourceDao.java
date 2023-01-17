package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.ResourceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceDao extends BaseDao<ResourceEntity> {

    void insert(Map<String, Object> params);

    ResourceEntity getEntityByModelStepId(String modelStepId);
}
