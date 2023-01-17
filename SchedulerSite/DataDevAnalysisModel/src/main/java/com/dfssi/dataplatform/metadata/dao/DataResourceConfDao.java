package com.dfssi.dataplatform.metadata.dao;


import com.dfssi.dataplatform.analysis.dao.CrudDao;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataResourceConfDao extends CrudDao<DataResourceConfEntity> {

    List<DataResourceConfEntity> getAllDataResource();

}
