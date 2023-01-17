package com.dfssi.dataplatform.ide.datasource.mvc.service;

import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceSubEntity;

import java.util.List;

public interface DataSourceSubService {
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
    List<DataSourceSubEntity> getSubinfoById(String srcId);}
