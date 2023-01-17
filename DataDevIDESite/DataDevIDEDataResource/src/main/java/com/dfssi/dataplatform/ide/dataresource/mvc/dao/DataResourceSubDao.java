package com.dfssi.dataplatform.ide.dataresource.mvc.dao;

import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceSubEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataResourceSubDao {

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
