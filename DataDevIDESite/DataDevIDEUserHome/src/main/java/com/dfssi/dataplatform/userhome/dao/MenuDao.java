package com.dfssi.dataplatform.userhome.dao;


import com.dfssi.dataplatform.userhome.entity.MenuEntity;
import com.dfssi.dataplatform.userhome.entity.MenuExampleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuDao {
    int countByExample(MenuExampleEntity example);

    int deleteByExample(MenuExampleEntity example);

    int deleteByPrimaryKey(String id);

    int insert(MenuEntity record);

    List<MenuEntity> selectByExampleWithBLOBs(MenuExampleEntity example);

    List<MenuEntity> selectByExample(MenuExampleEntity example);

    List<MenuEntity> selectByMenuName(@Param("menuName") String menuName);

    MenuEntity selectByPrimaryKey(String id);

    int updateByExampleWithBLOBs(@Param("record") MenuEntity record, @Param("example") MenuExampleEntity example);

    /**
     * 检查菜单名称是否重复
     * @param menuName
     * @return
     */
    int countByName(String menuName);

    /**
     * 修改时判断菜单名与其他菜单名是否冲突
     * @param entity
     * @return
     */
    int countByUpdate(MenuEntity entity);

    /**
     * 根据条件查询菜单
     * @param entity
     * @return
     */
    List<MenuEntity> selectByEntity(MenuEntity entity);

    /**
     * 根据所属系统查询菜单
     * @param system
     * @return
     */
    List<MenuEntity> selectBySystem(String system);

    /**
     * 根据所属菜单Id查菜单
     * @param pid
     * @return
     */
    List<MenuEntity> selectByParentMenu(String pid);
}