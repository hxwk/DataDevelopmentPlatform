package com.dfssi.dataplatform.userhome.dao;

import com.dfssi.dataplatform.userhome.entity.DicEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DicDao {

    /**
     * 显示字典主表
     * @return
     */
    List<DicEntity> listGetDic(DicEntity entity);

    /**
     * 新增/修改主表信息
     * @param dicEntity
     * @return
     */
    int insert(DicEntity dicEntity);

    /**
     * 根据字典类型删除
     * @param dicType
     * @return
     */
    int deleteByType(String dicType);

    /**
     * 更改有效性
     * @param dicEntity
     * @return
     */
    int changeValid(DicEntity dicEntity);

    /**
     * 新增时判断字典类型是否重复
     * @param dicType
     * @return
     */
    int countByDicType(String dicType);

    /**
     * * 判断是否为修改
     * @param id
     * @return
     */
    int countById(String id);

    /**
     * 修改字典类型名称时判断与其他类型名称是否重复
     * @param dicEntity
     * @return
     */
    int countByUpdate(DicEntity dicEntity);

    /**
     * 有效字典信息
     * @return
     */
    List<DicEntity> listGetDicType();

    /**
     * 设置外键无效
     */
    void set0();

    /**
     * 设置外键有效
     */
    void set1();
}
