package com.dfssi.dataplatform.devmanage.dataresource.mvc.dao;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceAccessEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DataResourceAccessDao extends BaseDao<DataResourceAccessEntity> {

    List<DataResourceAccessEntity> findListByDsId(String dsId);

    int insertMutil(List<DataResourceAccessEntity> columnEntities);

}
