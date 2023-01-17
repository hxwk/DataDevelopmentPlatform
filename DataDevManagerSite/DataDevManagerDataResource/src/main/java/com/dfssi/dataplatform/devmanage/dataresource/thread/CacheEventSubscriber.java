package com.dfssi.dataplatform.devmanage.dataresource.thread;

import com.dfssi.dataplatform.devmanage.dataresource.cache.CacheEntity;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.dao.MetaDicDataResourceTypeDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.dao.MetaDicFieldTypeDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.MetaDicDataResourceTypeEntity;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.MetaDicFieldTypeEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Hannibal on 2018-01-11.
 */
@Component
public class CacheEventSubscriber {

    private static Logger logger = Logger.getLogger(CacheEventSubscriber.class);

    private final static long SLEEPTIME =  30 * 60 * 1000;

    @Autowired
    private MetaDicDataResourceTypeDao metaDicDataResourceTypeDao;

    @Autowired
    private MetaDicFieldTypeDao metaDicFieldTypeDao;

    @Scheduled(fixedDelay=SLEEPTIME)
    public void updateChache() {
        try {
            List<MetaDicDataResourceTypeEntity> dataResourceTypeEntityList = metaDicDataResourceTypeDao.findMapList();
            if (null != dataResourceTypeEntityList && !dataResourceTypeEntityList.isEmpty()) {
                for (MetaDicDataResourceTypeEntity dataResourceTypeEntity : dataResourceTypeEntityList)
                CacheEntity.dataResourceTypeCache.put(dataResourceTypeEntity.getDbType(), dataResourceTypeEntity.getDbName());
            }

            List<MetaDicFieldTypeEntity> fieldTypeEntityList = metaDicFieldTypeDao.findMapList();
            if (null != fieldTypeEntityList && !fieldTypeEntityList.isEmpty()) {
                for (MetaDicFieldTypeEntity fieldTypeEntity : fieldTypeEntityList)
                    CacheEntity.fieldTypeCache.put(fieldTypeEntity.getFieldType(), fieldTypeEntity.getFieldName());
            }

        } catch (Exception e) {
            logger.error(null, e);
        }
    }
}
