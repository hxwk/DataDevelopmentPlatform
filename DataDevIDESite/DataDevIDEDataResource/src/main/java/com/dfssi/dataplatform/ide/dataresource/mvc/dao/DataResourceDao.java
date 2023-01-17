package com.dfssi.dataplatform.ide.dataresource.mvc.dao;

import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 数据资源数据库操作
 * @author dingsl
 * @since 2018/8/21
 */
@Mapper
public interface DataResourceDao {
    /**
     * //新增，修改
     * @param entity
     */
    void insert(DataResourceEntity entity);

    /**
     * 删除
     * @param dataResourceId
     */
    void deleteById(String dataResourceId);

    /**
     * 查找大数据库数据资源表
     * @param entity
     * @return List<DataResourceEntity>
     */
    List<DataResourceEntity> findBigdataEntityList(DataResourceEntity entity);

    /**
     * 查找关系数据库数据资源表
     * @param entity
     * @return List<DataResourceEntity>
     */
    List<DataResourceEntity> findDBEntityList(DataResourceEntity entity);

    /**
     * 查找私有数据库数据资源表
     * @param entity
     * @return List<DataResourceEntity>
     */
    List<DataResourceEntity> findPrivateResourcesEntityList(DataResourceEntity entity);

    /**
     * 查找共享数据库数据资源表
     * @param entity
     * @return List<DataResourceEntity>
     */
    List<DataResourceEntity> findSharedResourcesEntityList(DataResourceEntity entity);

    /**
     * 统计数据资源数量
     * @return Map
     */
    Map countRtotal();

    /**
     * 改变数据私有化属性
     */
    void changePrivateStatus(String dataResourceId);

    /**
     * 改变数据公有化属性
     */
    void changeSharedStatus(String dataResourceId);

    /**
     * 检查数据资源名称是否重复
     * @param dataresourceName
     * @return
     */
    int countByName(String dataresourceName);

    /**
     * 判断是否为修改
     * @param dataresourceId
     * @return
     */
    int countById(String dataresourceId);

    /**
     * 修改数据资源名称时判断与其他数据资源名称是否冲突
     * @param entity
     * @return
     */
    int countByUpdate(DataResourceEntity entity);
}
