package com.dfssi.dataplatform.analysis.dao;


import com.dfssi.dataplatform.analysis.entity.AnalysisJarEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisModelEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JarDao extends CrudDao<AnalysisJarEntity> {
    public List<AnalysisJarEntity> listAllJars(Map params);
    public void delectJarByPath(String jarPath);

}
