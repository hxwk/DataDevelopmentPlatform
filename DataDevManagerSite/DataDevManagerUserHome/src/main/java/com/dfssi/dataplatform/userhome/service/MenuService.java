package com.dfssi.dataplatform.userhome.service;


import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.MenuEntity;

import java.util.Set;

/**
 * 系统功能菜单管理接口
 * @author wanlong
 */
public interface MenuService {


    /**
     * 根据主键删除功能菜单
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * 条件查询功能菜单列表
     * @param pageParam
     * @param menuName
     * @return
     */
    PageResult<MenuEntity> selectByMenuName(PageParam pageParam, String menuName);

    /**
     * 更新系统功能菜单
     * @param record
     * @return
     */
    String modifyMenu(MenuEntity record);

    /**
     * 批量删除系统功能菜单
     * @param recordIDs
     * @return
     */
    String deleteMenu(String recordIDs);

    /**
     * 批量检查功能菜单是否可以被删除
     * @param recordIDs
     * @return
     */
    boolean checkMenuDeleteEnv(String recordIDs);

    /**
     * 插入系统菜单
     * @param record
     */
    String insert(MenuEntity record);

    /**
     * 根据用户查询当前用户下的菜单权限和按键权限
     * @param roleID
     * @return
     */
    Object getMenuByUser(String roleID);

    /**
     * 根据条件查询所有菜单和按键
     * @param requestTreeType
     * @return
     */
    String getAllMenu(int requestTreeType);

}
