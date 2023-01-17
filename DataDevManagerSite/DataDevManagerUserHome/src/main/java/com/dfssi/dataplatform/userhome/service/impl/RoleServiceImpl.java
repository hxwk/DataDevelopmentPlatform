package com.dfssi.dataplatform.userhome.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.*;
import com.dfssi.dataplatform.userhome.dao.RoleDao;
import com.dfssi.dataplatform.userhome.service.RoleMenuService;
import com.dfssi.dataplatform.userhome.service.RoleService;
import com.dfssi.dataplatform.userhome.service.UserService;
import com.dfssi.dataplatform.userhome.utils.DateUtil;
import com.dfssi.dataplatform.userhome.utils.JsonXMLUtils;
import com.dfssi.dataplatform.userhome.utils.UUIDUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleDao roleDao;

    @Autowired
    UserService userService;

    @Autowired
    RoleMenuService roleMenuService;

    @Override
    public int deleteByPrimaryKey(String id) {
        return roleDao.deleteByPrimaryKey(id);
    }

    //root/menus/menu/menuId/btnIds
    @Override
    @Transactional(readOnly = false,propagation = Propagation.REQUIRED )
    public String insert(Map<String, Object> models) {
        String result="";
        try {
            RoleEntity roleEntity = JsonXMLUtils.map2obj((Map<String, Object>)models.get("roleEntity"), RoleEntity.class);
            RoleMenuEntity roleMenuEntity = JsonXMLUtils.map2obj((Map<String, Object>)models.get("roleMenuEntity"), RoleMenuEntity.class);
            roleEntity.setId(UUIDUtil.getTableKey());
            roleMenuEntity.setRoleId(roleEntity.getId());
            roleMenuEntity.setRelationDate(DateUtil.getNow());
//          JSONObject roleMenusAndBtns = JsonXMLUtils.map2obj((Map<String, Object>)models.get("root"),JSONObject.class);
//            List<RoleMenuEntity> roleMenuBeansList = new ArrayList<RoleMenuEntity>();
//            JSONArray menuJsonArrays = roleMenusAndBtns.getJSONArray("menus");
//            for (int i=0;i<menuJsonArrays.size();i++) {
//                JSONObject menuJson = menuJsonArrays.getJSONObject(i);
//                RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
//                roleMenuEntity.setRoleId(roleEntity.getId());
//                roleMenuEntity.setMenuId(menuJson.getString("menuId"));
//                roleMenuEntity.setButtonIds(menuJson.getString("btnIds"));
//                roleMenuEntity.setRelationDate(DateUtil.getNow());
//                roleMenuBeansList.add(roleMenuEntity);
//            }
            int count=roleDao.countByName(roleEntity.getRoleName());
            if(count!=0){
                result="角色名已存在";
            }else {
                roleDao.insert(roleEntity);
                roleMenuService.multiInsert(roleMenuEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    @Transactional(readOnly =true,propagation = Propagation.REQUIRED )
    public PageResult<RoleEntity> selectByRoleName(PageParam pageParam, String roleName) {
        Page<RoleEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<RoleEntity> list= roleDao.selectByRoleName(roleName);
        PageResult<RoleEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public RoleEntity selectByPrimaryKey(String id) {

        return roleDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public boolean checkRoleDeleteEnv(String recordIDs) {
        String[] recordIDArray = recordIDs.split(",");
        List<String> recordIDList = new ArrayList<String>();
        for(int i=0;i<recordIDArray.length;i++){
            recordIDList.add(recordIDArray[i]);
        }
        UserExampleEntity userExampleEntity = new UserExampleEntity();
        userExampleEntity.createCriteria().andURoleIn(recordIDList).andIsDeleteEqualTo(1);
        if (userService.countByExample(userExampleEntity)>=1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false,propagation =Propagation.REQUIRED )
    public String deleteRole(String recordIDs) {
        String result="";
        try {
            String[] recordIDArray = recordIDs.split(",");
            List<String> recordIDList = new ArrayList<String>();
            for(int i=0;i<recordIDArray.length;i++){
                recordIDList.add(recordIDArray[i]);
            }
            RoleExampleEntity roleExampleEntity = new RoleExampleEntity();
            roleExampleEntity.createCriteria().andIdIn(recordIDList);
            RoleMenuExampleEntity roleMenuExampleEntity = new RoleMenuExampleEntity();
            roleMenuExampleEntity.createCriteria().andRoleIdIn(recordIDList);
            //批量删除角色关联的权限
            roleMenuService.deleteByExample(roleMenuExampleEntity);
            //批量删除角色
            roleDao.deleteByExample(roleExampleEntity);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false,propagation =Propagation.REQUIRED )
    public String modifyRole(Map<String, Object> models) {
        String result="";
        try {
            RoleEntity roleEntity = JsonXMLUtils.map2obj((Map<String, Object>)models.get("roleEntity"), RoleEntity.class);
            RoleMenuEntity roleMenuEntity = JsonXMLUtils.map2obj((Map<String, Object>)models.get("roleMenuEntity"), RoleMenuEntity.class);
//            JSONObject roleMenusAndBtns = JsonXMLUtils.map2obj((Map<String, Object>)models.get("root"),JSONObject.class);
//            List<RoleMenuEntity> roleMenuBeansList = new ArrayList<RoleMenuEntity>();
//            JSONArray menuJsonArrays = roleMenusAndBtns.getJSONArray("menus");
//            for (int i=0;i<menuJsonArrays.size();i++) {
//                JSONObject menuJson = menuJsonArrays.getJSONObject(i);
//                RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
//                roleMenuEntity.setRoleId(roleEntity.getId());
//                roleMenuEntity.setMenuId(menuJson.getString("menuId"));
//                roleMenuEntity.setButtonIds(menuJson.getString("btnIds"));
//                roleMenuEntity.setRelationDate(DateUtil.getNow());
//                roleMenuBeansList.add(roleMenuEntity);
//            }
            RoleExampleEntity roleExampleEntity = new RoleExampleEntity();
            roleExampleEntity.createCriteria().andIdEqualTo(roleEntity.getId());
            //更新角色表
            roleDao.updateByExample(roleEntity, roleExampleEntity);
            RoleMenuExampleEntity roleMenuExampleEntity = new RoleMenuExampleEntity();
            roleMenuExampleEntity.createCriteria().andRoleIdEqualTo(roleEntity.getId());
            //先根据角色删除所有关联的权限
            roleMenuService.deleteByExample(roleMenuExampleEntity);
            //再批量插入角色关联的所有权限
            roleMenuService.multiInsert(roleMenuEntity);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }
}
