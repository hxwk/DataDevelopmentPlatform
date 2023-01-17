package com.dfssi.dataplatform.ide.dataresource.mvc.service;

import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceSubEntity;

import java.util.List;

public interface DataResourceSubService {
    /**
     * //新增，修改
     * @param entity
     */
    void insertSub(DataResourceEntity entity);

    /**
     * 删除
     * @param dataResourceId
     */
    void deleteSubInfo(String dataResourceId);

    /**
     * 根据id查询DataresourceSubInfo
     * @param dataresourceId
     * @return List<DataResourceSubEntity>
     */
    List<DataResourceSubEntity> findDataresourceSubInfo(String dataresourceId);


    /**
     * 接入里的微服务调用
     * @param resId
     * @return
     */
    List<DataResourceSubEntity> getResourceSubInfoByResId(String resId);
}
