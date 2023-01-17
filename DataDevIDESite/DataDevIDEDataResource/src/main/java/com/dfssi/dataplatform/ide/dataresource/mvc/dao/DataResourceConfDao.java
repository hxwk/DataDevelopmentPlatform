package com.dfssi.dataplatform.ide.dataresource.mvc.dao;


import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceConfEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 对meta_dataresource_conf_t表的操作
 * @author hongs
 */
@Mapper
public interface DataResourceConfDao{

    List<DataResourceConfEntity> getAllDataResources(@Param("list")List<String> dataresourceTypes);

}
