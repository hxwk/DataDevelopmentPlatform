package com.dfssi.dataplatform.userhome.dao;

import com.dfssi.dataplatform.userhome.Entity.RoleEntity;
import com.dfssi.dataplatform.userhome.Entity.RoleExampleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleDao {

    int deleteByExample(RoleExampleEntity example);

    int deleteByPrimaryKey(String id);

    int insert(RoleEntity record);

    List<RoleEntity> selectByRoleName(@Param("roleName") String roleName);

    RoleEntity selectByPrimaryKey(String id);

    int updateByExample(@Param("record") RoleEntity record, @Param("example") RoleExampleEntity example);

    /**
     * 检查角色名是否重复
     * @param roleName
     * @return
     */
    int countByName(String roleName);
}