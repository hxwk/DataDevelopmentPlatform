package com.dfssi.dataplatform.ide.datasource.mvc.service.impl;

import com.dfssi.dataplatform.ide.datasource.mvc.service.FieldRulesService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class FieldRulesServiceImpl implements FieldRulesService {
    @Override
    public String paramValid(BindingResult bindingResult) {
        String result="";
        StringBuilder sb = new StringBuilder();
            if(bindingResult.hasErrors()){
                //bindingResult.getFieldError()随机返回一个对象属性的异常信息
                //如果要一次性返回所有对象属性异常信息，则调用getFieldErrors()
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    sb.append("参数[").append(fieldError.getRejectedValue()).append("]")
                            .append(fieldError.getDefaultMessage()).append(";");
                }
                result=sb.toString();
            }else{
                result="";
            }
        return result;
    }
}
