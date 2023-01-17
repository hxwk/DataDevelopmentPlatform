package com.dfssi.dataplatform.ide.access.mvc.dao;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessLinkInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskAccessLinkInfoDao {

    int insertMutil(List<TaskAccessLinkInfoEntity> columnEntities);

    int deleteByTaskId(String taskId);

    List<TaskAccessLinkInfoEntity> findListByTaskId(String taskId);
}
