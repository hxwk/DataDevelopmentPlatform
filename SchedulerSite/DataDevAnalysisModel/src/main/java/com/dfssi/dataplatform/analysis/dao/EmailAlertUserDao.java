package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.EmailAlertUserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAlertUserDao extends CrudDao<EmailAlertUserEntity> {

    public EmailAlertUserEntity getUserByUserId(String userId);

}