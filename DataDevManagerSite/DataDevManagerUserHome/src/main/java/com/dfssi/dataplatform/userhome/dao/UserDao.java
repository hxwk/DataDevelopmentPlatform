package com.dfssi.dataplatform.userhome.dao;


import com.dfssi.dataplatform.userhome.Entity.UserEntity;
import com.dfssi.dataplatform.userhome.Entity.UserExampleEntity;
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

}