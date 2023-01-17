package com.dfssi.dataplatform.metadata.dao;

import com.dfssi.dataplatform.analysis.dao.CrudDao;
import com.dfssi.dataplatform.metadata.entity.ServerDataSourceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/12 13:31
 */
@Mapper
public interface ServerDataSourceDao extends CrudDao<ServerDataSourceEntity> {
    String getServerDataSourceSeq();
    void insertDataSource(ServerDataSourceEntity serverDataSourceEntity,String seq);
    void saveParams(HashMap<String,String> map,String seq);
}
