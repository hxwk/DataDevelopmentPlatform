package com.dfssi.dataplatform.userhome.dao;


import com.dfssi.dataplatform.userhome.entity.UserEntity;
import com.dfssi.dataplatform.userhome.entity.UserExampleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    int countByExample(UserExampleEntity example);

    int deleteByExample(UserExampleEntity example);

    int deleteByPrimaryKey(String id);

    int insert(UserEntity record);

    List<UserEntity> selectByExample(UserEntity entity);

    UserEntity selectByPrimaryKey(String id);

    int updateByExample(@Param("record") UserEntity record, @Param("example") UserExampleEntity example);

    int fakeDeleteByExample(@Param("list")List<String> example);

    UserEntity login(@Param("uName")String uName, @Param("uPsword") String uPsword);

    /**
     * 修改时判断用户名与其他角色名是否冲突
     * @param entity
     * @return
     */
    int countByUpdate(UserEntity entity);
}