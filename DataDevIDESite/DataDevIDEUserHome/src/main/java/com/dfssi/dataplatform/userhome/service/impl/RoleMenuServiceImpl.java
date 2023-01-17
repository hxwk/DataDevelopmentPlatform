package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.userhome.dao.RoleMenuDao;
import com.dfssi.dataplatform.userhome.entity.RoleMenuEntity;
import com.dfssi.dataplatform.userhome.entity.RoleMenuExampleEntity;
import com.dfssi.dataplatform.userhome.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleMenuServiceImpl implements RoleMenuService {
    @Autowired
    private RoleMenuDao roleMenuDao;
    @Override
    public int countByExample(RoleMenuExampleEntity example) {
        return roleMenuDao.countByExample(example);
    }

    @Override
    public int deleteByExample(RoleMenuExampleEntity example) {
        return roleMenuDao.deleteByExample(example);
    }

    @Override
    public List<RoleMenuEntity> selectByExample(RoleMenuExampleEntity example) {
        return roleMenuDao.selectByExample(example);
    }

    @Override
    public void multiInsert(RoleMenuEntity roleMenuEntity) {
          roleMenuDao.multiInsert(roleMenuEntity);
    }

    @Override
    public RoleMenuEntity selectByRoleId(String roleId){
      return roleMenuDao.selectByRoleId(roleId);
    }
}
