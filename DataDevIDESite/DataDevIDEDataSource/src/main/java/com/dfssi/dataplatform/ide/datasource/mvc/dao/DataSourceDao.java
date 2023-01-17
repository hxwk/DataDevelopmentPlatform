package com.dfssi.dataplatform.ide.datasource.mvc.dao;


import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 操作数据库dv_datasource表
 * @author dingsl
 * @since 2018/8/21
 */
@Mapper
public interface DataSourceDao  {

    /**
     * 新增，修改
     * @param entity
     */
    void insert(DataSourceEntity entity);

    /**
     * 删除
     * @param datasourceId
     */
    void delete(String datasourceId);

    /**
     * 查询关系型数据库
     * @param entity
     * @return List<DataSourceEntity>
     */
    List<DataSourceEntity> findDBList(DataSourceEntity entity);

    /**
     * 查询大数据库
     * @param entity
     * @return List<DataSourceEntity>
     */
    List<DataSourceEntity> findBigdataList(DataSourceEntity entity);

    /**
     * 查询协议接口
     * @param entity
     * @return List<DataSourceEntity>
     */
    List<DataSourceEntity> findInterfaceList(DataSourceEntity entity);

    /**
     * 统计数据源数量
     * @return Map
     */
    Map countTotal();

    /**
     * 检查数据源名称是否重复
     * @param datasourceName
     * @return
     */
    int countByName(String datasourceName);

    /**
     * 判断是否为修改
     * @param datasourceId
     * @return
     */
    int countById(String datasourceId);

    /**
     * 修改数据源名称时判断与其他数据源名称是否冲突
     * @param entity
     * @return
     */
    int countByUpdate(DataSourceEntity entity);
}
