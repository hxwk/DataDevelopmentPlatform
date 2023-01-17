package com.dfssi.dataplatform.ide.dataresource.mvc.service.impl;

import com.dfssi.dataplatform.ide.dataresource.mvc.dao.DataResourceSubDao;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceSubEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.DataResourceSubService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service(value = "dataResourceSubService")
public class DataResourceSubServiceImpl implements DataResourceSubService {

    @Autowired
    DataResourceSubDao dataResourceSubDao;

    @Override
    public void insertSub(DataResourceEntity entity) {
         List<DataResourceSubEntity> list=entity.getDataResourceSubEntity();
         if(list!=null){
             for(DataResourceSubEntity dataResourceSubEntity :list){
                 if(StringUtils.isEmpty(dataResourceSubEntity.getDrsSubId())){
                 dataResourceSubEntity.setDrsSubId(UUID.randomUUID().toString());
                 }
             }
         }
        dataResourceSubDao.insertSub(entity);
    }

    @Override
    public void deleteSubInfo(String dataResourceId) {
         dataResourceSubDao.deleteSubInfo(dataResourceId);
    }

    @Override
    public List<DataResourceSubEntity> findDataresourceSubInfo(String dataresourceId) {
         return dataResourceSubDao.findDataresourceSubInfo(dataresourceId);
    }

    @Override
    public List<DataResourceSubEntity> getResourceSubInfoByResId(String resId) {
        return dataResourceSubDao.getResourceSubInfoByResId(resId);
    }
}
