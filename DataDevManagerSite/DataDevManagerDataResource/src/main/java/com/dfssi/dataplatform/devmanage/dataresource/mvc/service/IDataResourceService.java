package com.dfssi.dataplatform.devmanage.dataresource.mvc.service;


import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by cxq on 2018/1/2.
 */
public interface IDataResourceService {
    DataResourceEntity getEntity(DataResourceEntity entity);

    DataResourceEntity getEntity(String dataresId);

    List<DataResourceEntity> findEntityList(DataResourceEntity entity);


    Map<String, Object> shareResource(DataResourceEntity entity);
}
