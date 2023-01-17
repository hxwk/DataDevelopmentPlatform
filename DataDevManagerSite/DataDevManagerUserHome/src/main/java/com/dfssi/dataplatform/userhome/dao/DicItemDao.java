package com.dfssi.dataplatform.userhome.dao;

import com.dfssi.dataplatform.userhome.Entity.DicItemEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DicItemDao {
    /**
     * 显示字典子表
     * @return
     */
    List<DicItemEntity> listGetDicItem();

    /**
     * 新增子表信息
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

    /**
     * 新增时名称是否重复
     * @param itemName
     * @return
     */
    int countByItemName(String itemName);

    /**
     * * 判断是否为修改
     * @param id
     * @return
     */
    int countById(String id);
}
