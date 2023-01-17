package com.dfssi.dataplatform.analysis.service.mapper;

import com.dfssi.dataplatform.analysis.service.entity.ServiceStepEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/11 20:16
 */
@Mapper
public interface ServiceStepDao extends BaseDao<ServiceStepEntity> {

    List<ServiceStepEntity> getPageSteps();

    List<ServiceStepEntity> getDataSteps();
}
