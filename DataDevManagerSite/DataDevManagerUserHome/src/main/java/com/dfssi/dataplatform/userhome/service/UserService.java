package com.dfssi.dataplatform.userhome.service;



import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.UserEntity;
import com.dfssi.dataplatform.userhome.Entity.UserExampleEntity;

public interface UserService {

    /**
     * 根据主键删除用户
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * 插入角色和角色关联的功能菜单及按钮权限
     * @param record
     */
    String insert(UserEntity record);

    /**
     * 条件查询用户列表
     * @param entity
     * @param pageParam
     * @return
     */
    PageResult<UserEntity> selectByUserEntity(UserEntity entity, PageParam pageParam);

    /**
     * 更新系统用户
     * @param record
     * @return
     */
    String modifyUser(UserEntity entity, String nUPsword);

    /**
     * 批量删除系统用户
     * @param recordIDs
     * @return
     */
    String deleteUser(String recordIDs);

    /**
     * 修改用户
     * @param userEntity
     * @return String
     */
    String updateUser(UserEntity userEntity);

    int countByExample(UserExampleEntity entity);

    UserEntity login(String uName, String uPsword);
}
