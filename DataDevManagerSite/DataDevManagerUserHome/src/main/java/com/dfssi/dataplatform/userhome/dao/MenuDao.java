package com.dfssi.dataplatform.userhome.dao;


import com.dfssi.dataplatform.userhome.Entity.MenuEntity;
import com.dfssi.dataplatform.userhome.Entity.MenuExampleEntity;
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
}