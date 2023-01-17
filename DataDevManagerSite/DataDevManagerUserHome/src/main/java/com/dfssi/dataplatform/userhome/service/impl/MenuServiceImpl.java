package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.MenuEntity;
import com.dfssi.dataplatform.userhome.Entity.MenuExampleEntity;
import com.dfssi.dataplatform.userhome.Entity.RoleMenuEntity;
import com.dfssi.dataplatform.userhome.Entity.RoleMenuExampleEntity;
import com.dfssi.dataplatform.userhome.dao.MenuDao;
import com.dfssi.dataplatform.userhome.service.MenuService;
import com.dfssi.dataplatform.userhome.service.RoleMenuService;
import com.dfssi.dataplatform.userhome.utils.UUIDUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 系统功能菜单管理实现类
 * @author wanlong
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public int deleteByPrimaryKey(String id) {
        return menuDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.REQUIRED )
    public PageResult<MenuEntity> selectByMenuName(PageParam pageParam, String menuName) {
        Page<MenuEntity> page=PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<MenuEntity> menuEntityList = menuDao.selectByMenuName(menuName);
        for(MenuEntity menuEntity : menuEntityList){
            if(StringUtils.isNotEmpty(menuEntity.getParentMenu())){
                menuEntity.setParentMenuName(menuDao.selectByPrimaryKey(menuEntity.getParentMenu()).getMenuName());
            }
        }
        PageResult<MenuEntity> pageResult=new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    @Transactional(readOnly = false,propagation =Propagation.REQUIRED )
    public String modifyMenu(MenuEntity record) {
        String result="";
        try {
            MenuExampleEntity menuExampleEntity = new MenuExampleEntity();
            menuExampleEntity.createCriteria().andIdEqualTo(record.getId());
            if(StringUtils.isEmpty(record.getParentMenu())){
                record.setParentMenu("");
            }
            menuDao.updateByExampleWithBLOBs(record, menuExampleEntity);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false,propagation =Propagation.REQUIRED )
    public String deleteMenu(String recordIDs) {
        String result="";
        try {
            String[] recordIDArray = recordIDs.split(",");
            List<String> recordIDList = new ArrayList<String>();
            for(int i=0;i<recordIDArray.length;i++){
                recordIDList.add(recordIDArray[i]);
            }
            MenuExampleEntity menuExampleEntity = new MenuExampleEntity();
            menuExampleEntity.createCriteria().andIdIn(recordIDList);
            menuDao.deleteByExample(menuExampleEntity);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public boolean checkMenuDeleteEnv(String recordIDs) {
        boolean canDelete = true;
        String[] recordIDArray = recordIDs.split(",");
        List<String> recordIDList = new ArrayList<String>();
        for(int i=0;i<recordIDArray.length;i++){
            recordIDList.add(recordIDArray[i]);
        }
        RoleMenuExampleEntity roleMenuExampleEntity = new RoleMenuExampleEntity();
        roleMenuExampleEntity.createCriteria().andMenuIdIn(recordIDList);
        //如果当前的菜单有关联的角色,那么不可删除
        if(roleMenuService.countByExample(roleMenuExampleEntity)>=1){
            canDelete = canDelete&&false;
        }
//        MenuBtnExampleEntity menuBtnExampleEntity = new MenuBtnExampleEntity();
//        menuBtnExampleEntity.createCriteria().andBelongMenuIn(recordIDList);
//        如果当前的菜单有下属的菜单按钮,那么不可删除
//        if(this.selectMenuBtnByExample(menuBtnExampleEntity).size()>=1){
//            canDelete = canDelete&&false;
//        }
        return canDelete;
    }

    @Override
    @Transactional(readOnly =false,propagation =Propagation.REQUIRED )
    public String insert(MenuEntity record) {
        String result="";
        try {
            record.setId(UUIDUtil.getTableKey());
            if(StringUtils.isEmpty(record.getParentMenu())){
                record.setParentMenu("");
            }
            int count=menuDao.countByName(record.getMenuName());
            if(count!=0){
                result="菜单名已存在";
            }else {
                menuDao.insert(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    /**
     * 递归查询菜单表，根据角色关联的关系，拼接得到菜单及按钮的树
     * @return
     */
    private Object recursionMenuByUser(String menuParentID, Set<Object> menuIdSet){
        StringBuffer menuData =  new StringBuffer();
        MenuExampleEntity menuExampleEntity = new MenuExampleEntity();
        menuExampleEntity.setOrderByClause("ORDER_NUM");
        menuExampleEntity.createCriteria().andParentMenuEqualTo(menuParentID);
        List<MenuEntity> menuBeenList = menuDao.selectByExampleWithBLOBs(menuExampleEntity);
        menuData.append("[");
        int index = 0;
        for (MenuEntity menuEntity : menuBeenList) {
            Object removeTarget = null;

            innerForeach:for (Object menuId: menuIdSet){
                if(menuId.equals(menuEntity.getId())){
                    removeTarget = menuId;
                    break innerForeach;
                }
            }
            //每次遍历到一个目标就可移除当前目标，为下次遍历节省一次循环
            if(null!=removeTarget){
                if (index > 0) {
                    menuData.append(",");
                }
                menuData.append("{");
                menuData.append("\"id\":\"" + menuEntity.getId() + "\",");
                menuData.append("\"name\":\"" + menuEntity.getMenuName() + "\"");
                //判断当前菜单是否有访问路径
                if (!StringUtils.isEmpty(menuEntity.getAccessUrl())) {
                    menuData.append(",\"attribute\":{\"url\":\"" + menuEntity.getAccessUrl() + "\",\"remark\":\"" + menuEntity.getRemark() +"\"}");
                }
//                if(StringUtils.isNotEmpty(removeTarget.getButtonIds())){
//                    MenuBtnExampleEntity menuBtnExampleEntity = new MenuBtnExampleEntity();
//                    menuBtnExampleEntity.setOrderByClause("ORDER_NUM");
//                    List<String> btnIdList = new ArrayList<String>();
//                    for(String btnID:removeTarget.getButtonIds().split(",")){
//                        btnIdList.add(btnID);
//                    }
//                    menuBtnExampleEntity.createCriteria().andIdIn(btnIdList);
//                    判断当前菜单是否有功能菜单按钮
//                    List<MenuBtnEntity> menuBtnEntityList = menuBtnService.selectByExampleWithBLOBs(menuBtnExampleEntity);
//                    menuData.append(getBtnByMenu(menuBtnEntityList));
//               }
                menuExampleEntity.clear();
                menuExampleEntity.createCriteria().andParentMenuEqualTo(menuEntity.getId());
                if(menuDao.countByExample(menuExampleEntity)>0){
                    menuData.append(",\"children\":");
                    menuData.append(this.recursionMenuByUser(menuEntity.getId(), menuIdSet));
                }
                menuData.append("}");
                index++;
                menuIdSet.remove(removeTarget);
            }
        }
        menuData.append("]");
        return menuData.toString();
    }

    //根据用户查询当前用户下的菜单权限和菜单下的功能按键
    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public Object getMenuByUser(String roleId) {
        Set<Object> set=new HashSet<>();
        if(StringUtils.isNotEmpty(roleId)) {
            String[] roleIdArray = roleId.split(",");
            for(int i=0;i<roleIdArray.length;i++){
                RoleMenuEntity roleMenuEntity=roleMenuService.selectByRoleId(roleIdArray[i]);
                if(StringUtils.isNotEmpty(roleMenuEntity.getMenuId())){
                    String[] menuIdArray = roleMenuEntity.getMenuId().split(",");
                    for(int j=0;j<menuIdArray.length;j++){
                        Object o=menuIdArray[j];
                        set.add(o);
                    }
                }
            }
        }
//        RoleMenuExampleEntity roleMenuExampleEntity = new RoleMenuExampleEntity();
//        roleMenuExampleEntity.createCriteria().andRoleIdEqualTo(roleId);
       Object menuData = this.recursionMenuByUser("", set);
        return menuData;
    }

    /**
     * 递归查询所有的菜单表，拼接得到菜单及按钮的树
     * @return
     */
    private String recursionAllMenu(String menuParentID, int requestTreeType){
        StringBuffer menuData =new StringBuffer();
        MenuExampleEntity menuExampleEntity = new MenuExampleEntity();
        menuExampleEntity.setOrderByClause("ORDER_NUM");
        menuExampleEntity.createCriteria().andParentMenuEqualTo(menuParentID);
        List<MenuEntity> menuBeenList = menuDao.selectByExample(menuExampleEntity);
        menuData.append("[");
        int index = 0;
        for (MenuEntity menuEntity : menuBeenList) {
            if (index > 0) {
                menuData.append(",");
            }
            menuData.append("{");
            menuData.append("\"id\":\"" + menuEntity.getId() + "\",");
            menuData.append("\"name\":\"" + menuEntity.getMenuName() + "\"");
            //判断当前菜单是否有访问路径
            if (!StringUtils.isEmpty(menuEntity.getAccessUrl())) {
                menuData.append(",\"attribute\":{\"url\":\"" + menuEntity.getAccessUrl() + "\"}");
            }
            menuExampleEntity.clear();
            menuExampleEntity.createCriteria().andParentMenuEqualTo(menuEntity.getId());
            if(menuDao.countByExample(menuExampleEntity)>0){
                menuData.append(",\"children\":");
                StringBuffer append = menuData.append(this.recursionAllMenu(menuEntity.getId(), requestTreeType));
            }else{
//                if(requestTreeType==2){
//                    MenuBtnExampleEntity menuBtnExampleEntity = new MenuBtnExampleEntity();
//                    menuBtnExampleEntity.setOrderByClause("ORDER_NUM");
//                    menuBtnExampleEntity.createCriteria().andBelongMenuEqualTo(menuEntity.getId());
//                    //判断当前菜单是否有功能菜单按钮
//                    List<MenuBtnEntity> menuBtnEntityList = menuBtnService.selectByExampleWithBLOBs(menuBtnExampleEntity);
//                    menuData.append(getBtnByMenu(menuBtnEntityList));
//                }
            }
            menuData.append("}");
            index++;
        }
        menuData.append("]");
        return menuData.toString();
    }

//    private String getBtnByMenu(List<MenuBtnEntity> menuBtnEntityList){
//        StringBuffer menuData =  new StringBuffer();
//        if (menuBtnEntityList.size()>0) {
//            menuData.append(",\"children\":[");
//            for (int i = 0; i < menuBtnEntityList.size(); i++) {
//                MenuBtnEntity tmp = menuBtnEntityList.get(i);
//                menuData.append("{" + "\"id\":\"" + tmp.getId() + "\",");
//                menuData.append("\"name\":\"" + tmp.getButtonName() + "\"");
//                //判断当前菜单是否有访问路径
//                if (!StringUtils.isEmpty(tmp.getAccessUrl())) {
//                    menuData.append(",\"attribute\":{\"url\":\"" + tmp.getAccessUrl() + "\"}");
//                }
//                menuData.append("}");
//                if (i != (menuBtnEntityList.size()-1)) {
//                    menuData.append(",");
//                }
//            }
//            menuData.append("]");
//        }
//        return menuData.toString();
//    }

    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public String getAllMenu(int requestTreeType) {
        String menuData = this.recursionAllMenu("", requestTreeType);
        return menuData;
    }
}
