package com.dfssi.dataplatform.ide.datasource.mvc.service;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceConfEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceSubEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据源业务
 * @author dingsl
 * @since 2018/8/21
 */
public interface DataSourceService {

    /**
     * 新增修改
     * @param entity
     * @return String
     */
    String saveEntity(DataSourceEntity entity);

    /**
     * 查询关系型数据库表
     * @param entity
     * @param pageParam
     * @return PageResult<DataSourceEntity>
     */
    PageResult<DataSourceEntity> findDBEntityList(DataSourceEntity entity, PageParam pageParam);

    /**
     * 查询大数据库表
     * @param entity
     * @param pageParam
     * @return PageResult<DataSourceEntity>
     */
    PageResult<DataSourceEntity> findBigdataEntityList(DataSourceEntity entity, PageParam pageParam) ;

    /**
     * 查询协议接口表
     * @param entity
     * @param pageParam
     * @return PageResult<DataSourceEntity>
     */
    PageResult<DataSourceEntity> findInterfaceEntityList(DataSourceEntity entity, PageParam pageParam);

    /**
     * 预览查询
     * @param datasourceId
     * @return List<DataSourceSubEntity>
     */
    List<DataSourceSubEntity> findDatasourceInfo(String datasourceId);

    /**
     * 查询数据源数量
     * @return Map
     */
    Map countTotal();

    /**
     * 删除数据
     * @param datasouceId
     * @return String
     */
    String deleteDataSourceEntity(String datasouceId);

    /**
     * 查询数据源表
     * @return List<DataSourceConfEntity>
     */
    List<DataSourceConfEntity> getAllDataSources();

    /**
     * 新连接测试
     * @param entity
     * @return
     */
    String connectionTestNew(DataSourceEntity entity);


}
