package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.EmailAlertUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailAlertUserDao extends BaseDao<EmailAlertUserEntity> {

    EmailAlertUserEntity getUserByUserId(String userId);

}