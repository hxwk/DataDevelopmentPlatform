package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.userhome.Entity.RoleMenuEntity;
import com.dfssi.dataplatform.userhome.Entity.RoleMenuExampleEntity;

import java.util.List;

public interface RoleMenuService {
    int countByExample(RoleMenuExampleEntity example);

    int deleteByExample(RoleMenuExampleEntity example);

    List<RoleMenuEntity> selectByExampleWithBLOBs(RoleMenuExampleEntity example);

    List<RoleMenuEntity> selectByExample(RoleMenuExampleEntity example);

    /**
     * 批量插入角色关联的权限
     * @param roleMenuEntity
     */
    void multiInsert(RoleMenuEntity roleMenuEntity);

    RoleMenuEntity selectByRoleId(String roleId);
}
