package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.model.ConformanceCheckModel;
import com.dfssi.dataplatform.service.ConformanceCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/3 9:21
 */
@Service
public class ConformanceCheckServiceImp implements ConformanceCheckService {
    @Autowired
    private ConformanceCheckModel conformanceCheckModel;

    @Override
    public Map<String, Map<String, String>> getCheckResult(String companyId) {
        Map<String, Map<String, String>> checkResultMap = listToMap(conformanceCheckModel.getCheckResult(companyId));
        Map<String, Map<String, String>> itemResultMap = listToMap(conformanceCheckModel.getItemResult(companyId));
        checkResultMap.putAll(itemResultMap);
        return checkResultMap;
    }

    public Map<String, Map<String, String>> listToMap(List<Map<String, String>> listMap) {
        Map<String, Map<String, String>> stringHashMapHashMap = new HashMap<String, Map<String, String>>();
        listMap.forEach(map -> {
            String checkItemNo = map.remove("checkItemNo");
            stringHashMapHashMap.put(checkItemNo, map);
        });
        return stringHashMapHashMap;
    }
}
