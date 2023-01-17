package com.dfssi.dataplatform.analysis.service.mapper;

import com.dfssi.dataplatform.analysis.service.entity.ResourceConfEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceConfDao extends BaseDao<ResourceConfEntity> {

    List<ResourceConfEntity> getResourceConf(String resourceId);
}
