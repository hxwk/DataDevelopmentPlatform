package com.dfssi.dataplatform.analysis.service.mapper;

import com.dfssi.dataplatform.analysis.service.entity.ResourceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceDao extends BaseDao<ResourceEntity> {

    List<ResourceEntity> listAllSources();

}
