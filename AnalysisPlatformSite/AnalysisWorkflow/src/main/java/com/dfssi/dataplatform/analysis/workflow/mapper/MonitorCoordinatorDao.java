package com.dfssi.dataplatform.analysis.workflow.mapper;

import com.dfssi.dataplatform.analysis.workflow.entity.MonitorCoordinatorEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MonitorCoordinatorDao extends BaseDao<MonitorCoordinatorEntity> {

    public List<String> getOozieIdByModelId(String modelId);

}