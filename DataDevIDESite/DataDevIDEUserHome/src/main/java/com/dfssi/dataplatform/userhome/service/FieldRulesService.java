package com.dfssi.dataplatform.userhome.service;

import org.springframework.validation.BindingResult;

/**
 * 字段规则
 */
public interface FieldRulesService {

    String paramValid(BindingResult bindingResult);
}
