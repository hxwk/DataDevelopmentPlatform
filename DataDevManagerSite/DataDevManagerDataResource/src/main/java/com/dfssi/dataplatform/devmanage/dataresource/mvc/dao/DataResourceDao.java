package com.dfssi.dataplatform.devmanage.dataresource.mvc.dao;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataResourceDao extends BaseDao<DataResourceEntity> {
    int insertCol(DataResourceEntity entity);

    List<DataResourceEntity> findList(DataResourceEntity entity);

    int insertStatus(String dsId);
}
