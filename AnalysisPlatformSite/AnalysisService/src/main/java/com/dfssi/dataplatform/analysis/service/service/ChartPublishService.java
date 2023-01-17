package com.dfssi.dataplatform.analysis.service.service;

import com.dfssi.dataplatform.analysis.service.entity.ChartPublishEntity;
import com.dfssi.dataplatform.analysis.service.mapper.ChartPublishDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description 图表发布接口
 * @Author zhangcheng
 * @Date 2018/9/29 13:39
 */
@Service(value = "chartPublishService")
public class ChartPublishService {
    @Autowired
    private ChartPublishDao chartPublishDao;

    public int insert(ChartPublishEntity chartPublishEntity) {
        chartPublishEntity.setCreateTime(new Date());
        chartPublishEntity.setUpdateTime(new Date());
        return chartPublishDao.insertSelective(chartPublishEntity);
    }

    public int update(ChartPublishEntity chartPublishEntity) {
        chartPublishEntity.setUpdateTime(new Date());
        return chartPublishDao.updateSelective(chartPublishEntity);
    }

    public List<ChartPublishEntity> findAllList() {
        return chartPublishDao.findAllList();
    }

    public ChartPublishEntity get(Long id) {
        return chartPublishDao.findById(id);
    }

    public int delete(Long id) {
        return chartPublishDao.deleteById(id);
    }
}
