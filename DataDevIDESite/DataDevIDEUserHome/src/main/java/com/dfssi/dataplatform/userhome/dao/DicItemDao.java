package com.dfssi.dataplatform.userhome.dao;

import com.dfssi.dataplatform.userhome.entity.DicEntity;
import com.dfssi.dataplatform.userhome.entity.DicItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DicItemDao {
    /**
     * 显示字典子表
     * @return
     */
    List<DicItemEntity> listGetDicItem(DicItemEntity entity);

    /**
     * 新增修改子表信息
     * @param dicItemEntity
     * @return
     */
    int insert(DicItemEntity dicItemEntity);

    /**
     * 根据主键批量删除
     * @param ids
     * @return
     */
    int deleteById(@Param("list")List<String> ids);

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
     * 新增时,检查相同类型名称是否重复
     * @param entity
     * @return
     */
    int countByItemName(DicItemEntity entity);

    /**
     * 判断是否为修改
     * @param id
     * @return
     */
    int countById(String id);

    /**
     * 修改名称时判断与同类型的其他名称是否重复
     * @param entity
     * @return
     */
    int countByUpdate(DicItemEntity entity);

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
