package com.dfssi.dataplatform.ide.datasource.mvc.dao;

import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceSubEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 操作数据库dv_datasource_sub表
 * @author dingsl
 * @since 2018/8/21
 */
@Mapper
public interface DataSourceSubDao {
    /**
     * 新增，修改
     * @param entity
     */
    void insertSub(DataSourceEntity entity);

    /**
     * 删除
     * @param datasourceId
     */
    void deleteSubInfo(String datasourceId);

    /**
     * 预览查询
     * @param datasourceId
     * @return List<DataSourceSubEntity>
     */
    List<DataSourceSubEntity> findDatasourceInfo(String datasourceId);

    /**
     * 可视化接入里的微服务调用
     * @param srcId
     * @return
     */
    List<DataSourceSubEntity> getSubinfoById(String srcId);

}
