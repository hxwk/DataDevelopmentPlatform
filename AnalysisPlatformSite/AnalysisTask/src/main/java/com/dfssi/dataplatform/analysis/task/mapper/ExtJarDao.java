package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.ExtJarEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ExtJarDao extends BaseDao<ExtJarEntity> {

    List<ExtJarEntity> listAllJars();

    void delectJarByPath(String jarPath);
}
