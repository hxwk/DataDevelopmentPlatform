package com.dfssi.dataplatform.devmanage.dataresource.mvc.dao;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceTableColumnEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataResourceTableColumnDao extends BaseDao<DataResourceTableColumnEntity> {

    List<DataResourceTableColumnEntity> findListByDsId(String dsId);

}
