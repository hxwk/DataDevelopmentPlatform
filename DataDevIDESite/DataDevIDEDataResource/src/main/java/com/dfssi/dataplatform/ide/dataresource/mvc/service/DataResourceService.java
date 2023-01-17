package com.dfssi.dataplatform.ide.dataresource.mvc.service;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceConfEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceSubEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据资源业务
 * @author dingsl
 * @since 2018/8/21
 */
public interface DataResourceService {
    /**
     * 新增，修改
     * @param entity
     * @return String
     */
    String saveEntity(DataResourceEntity entity);

    /**
     * 查找大数据数据资源表
     * @param entity
     * @param pageParam
     * @return PageResult<DataResourceEntity>
     */
    PageResult<DataResourceEntity> findBigdataEntityList(DataResourceEntity entity, PageParam pageParam);

    /**
     * 查找关系数据库数据资源表
     * @param entity
     * @param pageParam
     * @return PageResult<DataResourceEntity>
     */
    PageResult<DataResourceEntity> findDBEntityList(DataResourceEntity entity, PageParam pageParam);

    /**
     * 查找私有化数据数据资源表
     * @param entity
     * @param pageParam
     * @return PageResult<DataResourceEntity>
     */
    PageResult<DataResourceEntity> findPrivateResourcesEntityList(DataResourceEntity entity, PageParam pageParam);

    /**
     * 查找公有化数据数据资源表
     * @param entity
     * @param pageParam
     * @return PageResult<DataResourceEntity>
     */
    PageResult<DataResourceEntity> findSharedResourcesEntityList(DataResourceEntity entity, PageParam pageParam);

    /**
     * 预览查询DataResourceSub信息
     * @param dataResourceId
     * @return List<DataResourceSubEntity>
     */
    List<DataResourceSubEntity> findDataresourceInfo(String dataResourceId);

    /**
     * 删除数据源实体
     * @param dataresId
     * @return String
     */
    String deleteDataResourceEntity(String dataresId);

    /**
     * 统计数据资源数量
     * @return Map
     */
    Map countRtotal();

    /**
     * 私有化属性变公有化
     * @return String
     */
    String changePrivateStatus(String dataResourceId);

    /**
     * 公有化属性变私有化
     * @return
     */
    String changeSharedStatus(String dataResourceId);

    List<DataResourceConfEntity> getAllDataResources();

    /**
     * 新连接测试
     * @param entity
     * @return
     */
    String connectionTestNew(DataResourceEntity entity);
}
