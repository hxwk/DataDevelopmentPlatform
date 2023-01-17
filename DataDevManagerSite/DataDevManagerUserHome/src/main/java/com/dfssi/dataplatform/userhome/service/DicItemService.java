package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.userhome.Entity.DicItemEntity;

import java.util.List;

public interface DicItemService {
    /**
     * 显示字典子表
     * @return
     */
    List<DicItemEntity> listGetDicItem();

    /**
     * 新增/修改子表信息
     * @param dicItemEntity
     * @return
     */
    int insert(DicItemEntity dicItemEntity);

    /**
     * 根据主键批量删除
     * @param ids
     * @return
     */
    int deleteById(String ids);

    /**
     * 更改有效性
     * @param dicItemEntity
     * @return
     */
    int changeValid(DicItemEntity dicItemEntity);

    /**
     * 根据字典类型查数据
     * @param dicType
     * @return
     */
    List<DicItemEntity> listGetByDicType(String dicType);
}
