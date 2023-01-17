package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.DataresourceAccessInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataresourceAccessInfoDao extends BaseDao<DataresourceAccessInfoEntity> {

    List<DataresourceAccessInfoEntity> getSourceConf(String dataresourceId);

    void insert(Map params);

}
