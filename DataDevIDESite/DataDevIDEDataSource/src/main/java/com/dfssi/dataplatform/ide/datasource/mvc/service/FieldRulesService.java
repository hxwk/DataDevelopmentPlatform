package com.dfssi.dataplatform.ide.datasource.mvc.service;

import org.springframework.validation.BindingResult;

/**
 * 字段规则
 */
public interface FieldRulesService {

    String paramValid(BindingResult bindingResult);
}
