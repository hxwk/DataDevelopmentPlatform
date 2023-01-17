package com.dfssi.dataplatform.analysis.dao;

import com.dfssi.dataplatform.analysis.entity.EmailAlertRuleEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAlertRuleDao extends CrudDao<EmailAlertRuleEntity> {

    public EmailAlertRuleEntity getRuleByRuleId(String ruleId);

}