package com.dfssi.dataplatform.devmanage.dataresource.mvc.dao;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.MetaDicFieldTypeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDicFieldTypeDao extends BaseDao<MetaDicFieldTypeEntity> {

    List<MetaDicFieldTypeEntity> findMapList();

}
