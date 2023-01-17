package com.dfssi.dataplatform.userhome.dao;


import com.dfssi.dataplatform.userhome.entity.RoleMenuEntity;
import com.dfssi.dataplatform.userhome.entity.RoleMenuExampleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuDao {
    int countByExample(RoleMenuExampleEntity example);

    int deleteByExample(RoleMenuExampleEntity example);

    List<RoleMenuEntity> selectByExample(RoleMenuExampleEntity example);

    /**
     * 批量插入角色关联的权限
     * @param roleMenuEntity
     */
    void multiInsert(RoleMenuEntity roleMenuEntity);

    RoleMenuEntity selectByRoleId(String roleId);
}