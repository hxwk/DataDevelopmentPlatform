package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.DataSourceMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/3 18:39
 */
@Mapper
public interface DataSourceMetaDao {

//    @Select("SELECT * FROM datasource_meta")
//    List<DataSourceMeta> findAll();

    @Select("SELECT * FROM copy_meta_dataresource_conf_t")
    List<DataSourceMeta> findAll();

}
