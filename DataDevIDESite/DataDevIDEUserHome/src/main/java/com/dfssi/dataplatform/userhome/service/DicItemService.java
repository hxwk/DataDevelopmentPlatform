package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.entity.DicEntity;
import com.dfssi.dataplatform.userhome.entity.DicItemEntity;

import java.util.List;

public interface DicItemService {
    /**
     * 显示字典子表
     * @return
     */
    PageResult<DicItemEntity> listGetDicItem(PageParam pageParam,DicItemEntity entity);

    /**
     * 新增/修改子表信息
     * @param dicItemEntity
     * @return
     */
    String insert(DicItemEntity dicItemEntity);

    /**
     * 根据主键批量删除
     * @param ids
     * @return
     */
    String deleteByIds(String ids);

    /**
     * 更改有效性
     * @param dicItemEntity
     * @return
     */
    String changeValid(DicItemEntity dicItemEntity);

    /**
     * 根据字典类型查数据
     * @param dicType
     * @return
     */
    List<DicItemEntity> listGetByDicType(String dicType);

    /**
     * 根据字典类型删除
     * @param dicType
     * @return
     */
    int deleteByType(String dicType);

    /**
     * 修改字表字段的字典类型
     * @param entity
     * @return
     */
    int updateByType(DicEntity entity);

    /**
     * 接入任务显示数据源对应的所有有效的字典子表
     * @return
     */
    List<DicItemEntity> listGetValidSource();

    /**
     * 接入任务显示数据资源对应的所有有效的字典子表
     * @return
     */
    List<DicItemEntity> listGetValidResource();

    /**
     * 显示数据库对应的所有有效的字典信息
     * @return
     */
    List<DicItemEntity> listGetValidDataItem();


    /**
     * 根据类型预览字表信息
     * @return
     */
    List<DicItemEntity> listSelectDataItem(String dicType);

    /**
     * 查询系统信息
     * @return
     */
    List<DicItemEntity> listSelectSystem();
}
