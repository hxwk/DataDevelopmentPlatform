package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.StepInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StepInfoDao extends BaseDao<StepInfoEntity> {

    List<StepInfoEntity> listAllSteps();

    String getOutputByClassName(String className);
}
