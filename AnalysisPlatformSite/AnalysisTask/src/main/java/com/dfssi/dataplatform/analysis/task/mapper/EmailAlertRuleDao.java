package com.dfssi.dataplatform.analysis.task.mapper;

import com.dfssi.dataplatform.analysis.task.entity.EmailAlertRuleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailAlertRuleDao extends BaseDao<EmailAlertRuleEntity> {

    EmailAlertRuleEntity getRuleByRuleId(String ruleId);

}