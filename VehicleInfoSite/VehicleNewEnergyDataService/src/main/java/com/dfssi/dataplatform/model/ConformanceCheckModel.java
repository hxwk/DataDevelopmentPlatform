package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.annotation.DataSource;
import com.dfssi.dataplatform.mapper.ConformanceCheckMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/3 9:59
 */
@Component
public class ConformanceCheckModel {
    @Autowired
    private ConformanceCheckMapper conformanceCheckMapper;
    @DataSource(DataSource.MYSQL)
    public List<Map<String, String>>  getCheckResult(String companyId) {
        List<Map<String, String>> checkResult =conformanceCheckMapper.getCheckResult(companyId);
        return checkResult;
    }
    @DataSource(DataSource.MYSQL)
    public List<Map<String, String>>  getItemResult(String companyId) {
        List<Map<String, String>> itemResult = conformanceCheckMapper.getItemResult(companyId);
        return itemResult;
    }
}
