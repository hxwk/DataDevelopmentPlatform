package com.dfssi.dataplatform.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/3 9:21
 */
public interface ConformanceCheckService {
    Map<String, Map<String, String>> getCheckResult(String companyId);
}
