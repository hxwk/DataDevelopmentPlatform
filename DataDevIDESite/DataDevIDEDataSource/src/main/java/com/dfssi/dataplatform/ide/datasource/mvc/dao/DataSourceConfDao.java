package com.dfssi.dataplatform.ide.datasource.mvc.dao;

import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceConfEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hongs
 */
@Mapper
public interface DataSourceConfDao{
    //查询数据源表
    List<DataSourceConfEntity> getAllDataSources(@Param("list")List<String> datasourceTypes);


}
