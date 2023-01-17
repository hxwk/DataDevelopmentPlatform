package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.ResourceConfEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceConfDao extends BaseDao<ResourceConfEntity> {

    void insert(Map<String, Object> params);

}
