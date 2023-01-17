package com.dfssi.dataplatform.ide.access.mvc.service.impl;

import com.dfssi.dataplatform.ide.access.mvc.constants.Constants;
import com.dfssi.dataplatform.ide.access.mvc.dao.TaskAccessPluginsDao;
import com.dfssi.dataplatform.ide.access.mvc.entity.*;
import com.dfssi.dataplatform.ide.access.mvc.restful.ICleanTransformFeign;
import com.dfssi.dataplatform.ide.access.mvc.restful.IDataresourceFeign;
import com.dfssi.dataplatform.ide.access.mvc.restful.IDatasourceFeign;
import com.dfssi.dataplatform.ide.access.mvc.service.IFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author
 * @date 2018/9/17
 * @description 微服务调用模块，用于获取接入任务新增和修改左边树形菜单栏
 */
@Service(value = "feignService")
public class FeignService implements IFeignService {
    @Autowired
    private IDatasourceFeign iDatasourceFeign;

    @Autowired
    private IDataresourceFeign iDataresourceFeign;

    @Autowired
    private ICleanTransformFeign iCleanTransformFeign;

    @Autowired
    private TaskAccessPluginsDao taskAccessPluginsDao;

    /**
     * 获取接入任务新增和修改左边树形菜单栏
     * @return
     */
    @Override
    //@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public AccessLeftMenuItemsEntity getLeftMenus(){
        AccessLeftMenuItemsEntity leftMenuItems = new AccessLeftMenuItemsEntity();
        //查询所有的数据源
        List<DataSourceConfEntity> datasources= iDatasourceFeign.getAllDataSources();
        //查询所有的数据资源
        List<DataResourceConfEntity> dataResources = iDataresourceFeign.getAllDataResources();
        //查询清洗转换主表
        List<TaskCleanTransformMappingEntity> cleanTranses= iCleanTransformFeign.getAllCleanTranses();
        /*添加数据源*/
        for (DataSourceConfEntity dsce : datasources) {
            dsce.setId(dsce.getDatasourceId());
            dsce.setShowName(dsce.getDatasourceName());
            dsce.setCommonType(dsce.getDatasourceType());
            leftMenuItems.addDataSourceItem(dsce);
        }
        /*添加数据资源*/
        for (DataResourceConfEntity drce : dataResources) {
            drce.setId(drce.getDataresourceId());
            drce.setShowName(drce.getDataresourceName());
            drce.setCommonType(drce.getDataresourceType());
            leftMenuItems.addOutputItem(drce);
        }
        /*添加清洗转换*/
        for(TaskCleanTransformMappingEntity tctm:cleanTranses){
            tctm.setShowName(tctm.getCleanName());
            tctm.setCommonType(tctm.getId());
            leftMenuItems.addProcessItem(tctm);
        }
        //获取任务步骤组件信息
        List<TaskAccessPluginsEntity> list = taskAccessPluginsDao.getAllPluginEntities();
        for (TaskAccessPluginsEntity tape : list){
            tape.setShowName(tape.getStepTypeName());
            tape.setCommonType(tape.getStepTypeCode());
            if (Constants.S_STEP_TYPE_P_CODE.equalsIgnoreCase(tape.getStepTypePcode())) {
                leftMenuItems.addChannelItem(tape);
            }
        }
        return leftMenuItems;
    }

    @Override
    public List<TaskCleanTransformInfoEntity> findListByMappingId(String mappingId) {
        return iCleanTransformFeign.findListByMappingId(mappingId);
    }

    @Override
    public List<MetaDataresourceAccessInfoEntity> getResourceAccessInfoByResId(String resId) {
        List<MetaDataresourceAccessInfoEntity> list = iDataresourceFeign.getResourceAccessInfoByResId(resId);
        return list;
    }

    @Override
    public List<MetaDatasourceAccessInfoEntity> getSourceAccessInfoBySrcId(String srcId) {
        List<MetaDatasourceAccessInfoEntity> list = iDatasourceFeign.getSourceAccessInfoBySrcId(srcId);
        return list;
    }


}