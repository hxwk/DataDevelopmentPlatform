package com.dfssi.dataplatform.userhome.dao;

import com.dfssi.dataplatform.userhome.Entity.DicEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DicDao {

    /**
     * 显示字典主表
     * @return
     */
    List<DicEntity> listGetDic();

    /**
     * 新增主表信息
     * @param dicEntity
     * @return
     */
    int insert(DicEntity dicEntity);

    /**
     * 根据主键删除
     * @param id
     * @return
     */
    int deleteById(String id);

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
}
