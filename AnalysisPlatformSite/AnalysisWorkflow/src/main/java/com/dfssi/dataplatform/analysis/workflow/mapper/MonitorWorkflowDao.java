package com.dfssi.dataplatform.analysis.workflow.mapper;

import com.dfssi.dataplatform.analysis.workflow.entity.MonitorWorkflowEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MonitorWorkflowDao extends BaseDao<MonitorWorkflowEntity> {

    public List<String> getOozieIdByModelId(String modelId);

}