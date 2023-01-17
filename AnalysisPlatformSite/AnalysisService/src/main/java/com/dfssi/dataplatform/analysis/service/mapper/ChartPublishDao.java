package com.dfssi.dataplatform.analysis.service.mapper;

import com.dfssi.dataplatform.analysis.service.entity.ChartPublishEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChartPublishDao {
    ChartPublishEntity findById(Long id);

    List<ChartPublishEntity> findAllList();

    int insertSelective(ChartPublishEntity entity);

    int updateSelective(ChartPublishEntity entity);

    int deleteById(Long id);
}
