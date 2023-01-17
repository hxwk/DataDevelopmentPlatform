package com.dfssi.dataplatform.userhome.service;


import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.entity.RoleEntity;

import java.util.Map;

/**
 * 角色管理接口
 * @author wanlong
 */
public interface RoleService {

    /**
     * 根据主键删除角色
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * 插入角色和角色关联的功能菜单及按钮权限
     * @param models
     * @return
     */
    String insert(Map<String, Object> models);

    /**
     * 条件查询角色列表
     * @param pageParam
     * @param roleName
     * @return
     */
    PageResult<RoleEntity> selectByRoleName(PageParam pageParam, String roleName);

    /**
     * 根据主键查询角色
     * @param id
     * @return
     */
    RoleEntity selectByPrimaryKey(String id);

    /**
     * 更新系统角色
     * @param models
     * @return
     */
    String modifyRole(Map<String, Object> models);

    /**
     * 批量删除系统角色
     * @param recordID
     * @return
     */
    String deleteRole(String recordID);

    /**
     * 批量检查用户角色是否可以被删除
     * @param recordID
     * @return
     */
    boolean checkRoleDeleteEnv(String recordID);

}
