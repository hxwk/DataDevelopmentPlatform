package com.dfssi.dataplatform.ide.access.mvc.service;

import com.dfssi.dataplatform.ide.access.mvc.entity.AccessLeftMenuItemsEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDataresourceAccessInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDatasourceAccessInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskCleanTransformInfoEntity;

import java.util.List;

/**
 * @author
 * @date 2018/9/17
 * @description  微服务调用模块
 */
public interface IFeignService {
    /**
     * 获取接入任务新增和修改左边树形菜单栏
     * @return
     */
    AccessLeftMenuItemsEntity getLeftMenus();

    /**
     *根据mappingid获取清洗转换子表数据
     * @param mappingId
     * @return
     */
    List<TaskCleanTransformInfoEntity> findListByMappingId(String mappingId);

    /**
     * 根据数据资源ID查询数据资源接入信息，用于新增修改时右侧参数配置信息展示
     * @param resId
     * @return
     */
    List<MetaDataresourceAccessInfoEntity> getResourceAccessInfoByResId(String resId);

    /**
     * 根据数据源id查询子表信息，用于新增修改时右侧参数配置信息展示
     * @param srcId
     * @return
     */
    List<MetaDatasourceAccessInfoEntity> getSourceAccessInfoBySrcId(String srcId);


}