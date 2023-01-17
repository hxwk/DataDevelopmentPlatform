package com.dfssi.dataplatform.devmanage.dataresource.mvc.dao;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.MetaDicDataResourceTypeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDicDataResourceTypeDao extends BaseDao<MetaDicDataResourceTypeEntity> {

    List<MetaDicDataResourceTypeEntity> findMapList();

}
