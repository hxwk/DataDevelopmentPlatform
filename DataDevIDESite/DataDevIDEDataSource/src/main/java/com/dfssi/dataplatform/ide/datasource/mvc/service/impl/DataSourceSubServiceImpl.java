package com.dfssi.dataplatform.ide.datasource.mvc.service.impl;

import com.dfssi.dataplatform.ide.datasource.mvc.dao.DataSourceSubDao;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceSubEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.service.DataSourceSubService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service(value ="dataSourceSubService")
public class DataSourceSubServiceImpl implements DataSourceSubService {
    @Autowired
    DataSourceSubDao dataSourceSubDao;

    @Override
    public void insertSub(DataSourceEntity entity) {
        List<DataSourceSubEntity> list=entity.getDataSourceSubEntity();
        if(list!=null){
            for(DataSourceSubEntity dataResourceSubEntity :list){
                if(StringUtils.isEmpty(dataResourceSubEntity.getDsSubId())){
                    dataResourceSubEntity.setDsSubId(UUID.randomUUID().toString());
                }
            }
        }
        dataSourceSubDao.insertSub(entity);
    }

    @Override
    public void deleteSubInfo(String datasourceId) {
        dataSourceSubDao.deleteSubInfo(datasourceId);
    }

    @Override
    public List<DataSourceSubEntity> findDatasourceInfo(String datasourceId) {
        return dataSourceSubDao.findDatasourceInfo(datasourceId);
    }

    @Override
    public List<DataSourceSubEntity> getSubinfoById(String srcId) {
        return dataSourceSubDao.getSubinfoById(srcId);
    }


}
