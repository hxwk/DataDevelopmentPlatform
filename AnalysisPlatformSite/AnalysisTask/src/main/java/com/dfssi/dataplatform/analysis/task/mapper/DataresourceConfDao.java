package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.DataresourceConfEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataresourceConfDao extends BaseDao<DataresourceConfEntity> {

    List<DataresourceConfEntity> listAllSources();

    DataresourceConfEntity getByDataresourceId(String dataresourceId);

    void insert(Map params);

}
