package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.RoleEntity;
import com.dfssi.dataplatform.userhome.Entity.UserEntity;
import com.dfssi.dataplatform.userhome.Entity.UserExampleEntity;
import com.dfssi.dataplatform.userhome.dao.UserDao;
import com.dfssi.dataplatform.userhome.service.RoleService;
import com.dfssi.dataplatform.userhome.service.UserService;
import com.dfssi.dataplatform.userhome.utils.UUIDUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleService roleService;


    @Override
    public int deleteByPrimaryKey(String id) {
        return userDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false,propagation =Propagation.REQUIRED )
    public String insert(UserEntity record) {
        String result="";
        if(record.getId()==null) {
            record.setId(UUIDUtil.getTableKey());
        }
        UserExampleEntity userExampleEntity = new UserExampleEntity();
        UserExampleEntity.Criteria c= userExampleEntity.createCriteria();

        c.andUNameEqualTo(record.getUName());
        c.andIsDeleteEqualTo(1);
        int count= userDao.countByExample(userExampleEntity);
        if(count!=0){
            result="用户名已存在";
            return result;
        }
        record.setIsDelete("1");
           if (userDao.insert(record)!=0){
               result="";
           }else {
               result="添加失败";
           }
        return result;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<UserEntity> selectByUserEntity(UserEntity entity, PageParam pageParam) {
        Page<UserEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<UserEntity> userEntityList = userDao.selectByExample(entity);
        for(UserEntity userEntity : userEntityList){
            if(StringUtils.isNotEmpty(userEntity.getURole())) {
                String[] recordIDArray = userEntity.getURole().split(",");
                StringBuffer roleNames=new StringBuffer("");
                for(int i=0;i<recordIDArray.length;i++){
                    RoleEntity roleEntity = roleService.selectByPrimaryKey(recordIDArray[i]);
                    if (roleEntity != null) {
                        roleNames.append(roleEntity.getRoleName());
                        roleNames.append(",");
                    }
                }
                String roleName=roleNames.substring(0,roleNames.lastIndexOf(","));
                    userEntity.setURoleName(roleName);
            }
        }
        PageResult<UserEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String modifyUser(UserEntity entity, String nUPsword) {
       //判断原密码是否正确
        String result="";
        UserExampleEntity example = new UserExampleEntity();
        UserEntity userEntity = userDao.selectByPrimaryKey(entity.getId());
        if (!userEntity.getUPsword().equals(entity.getUPsword())){
            result="原密码错误";
        }else {
            userEntity.setUPsword(nUPsword);
            example.createCriteria().andIdEqualTo(entity.getId());
            if (userDao.updateByExample(userEntity, example) != 0) {
                result="";
            } else {
                result="修改失败";
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String deleteUser(String recordIDs) {
        String result="";
        try {
            String[] recordIDArray = recordIDs.split(",");
        List<String> recordIDList = new ArrayList<String>();
        for(int i=0;i<recordIDArray.length;i++){
            recordIDList.add(recordIDArray[i]);
        }
            userDao.fakeDeleteByExample(recordIDList);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String updateUser(UserEntity userEntity) {
        String result="";
        UserExampleEntity example = new UserExampleEntity();
        example.createCriteria().andIdEqualTo(userEntity.getId());
        if (userDao.updateByExample(userEntity,example)!=0){
             result="";
        }else {
            result="修改失败";
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public int countByExample(UserExampleEntity entity){
        return userDao.countByExample(entity);
    }

    @Override
    @Transactional(readOnly = true,propagation =Propagation.REQUIRED )
    public UserEntity login(String uName, String uPsword) {
        return userDao.login(uName,uPsword);
    }
}
