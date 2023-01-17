package com.dfssi.dataplatform.manager.monitor.task.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

abstract public class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String TAG_RESULT_HEADER = "header";
    protected static final String TAG_RESULT_BODY = "body";
    protected static final String TAG_RESULT_STATUS = "status";
    protected static final String TAG_RESULT_CODE = "code";
    protected static final String TAG_RESULT_MESSAGE = "message";
    protected static final String TAG_RESULT_DETAILS = "details";

    protected static final String STATUS_CODE_SUCCESS = "00000";
    protected static final String STATUS_CODE_ERROR = "99999";
    protected static final String STATUS_MESSAGE_SUCCESS = "SUCCESS";
    protected static final String STATUS_MESSAGE_FAIL = "FAIL";

    protected Map buildSuccessResult() {
        Map resultMap = this.buildResult();
        return this.addStatus(resultMap, STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS);
    }

    protected Map buildSuccessResult(String message) {
        Map resultMap = this.buildResult();
        return this.addStatus(resultMap, STATUS_CODE_SUCCESS, message);
    }

    protected Map buildResult(String code, String message) {
        Map resultMap = this.buildResult();
        return this.addStatus(resultMap, code, message);
    }

    protected Map buildResult(String code, String message, String details) {
        Map resultMap = this.buildResult();
        return this.addStatus(resultMap, code, message, details);
    }

    private Map buildResult() {
        Map resultMap = new LinkedHashMap();
        Map headerMap = new LinkedHashMap();
        resultMap.put(TAG_RESULT_HEADER, headerMap);

        Map bodyMap = new LinkedHashMap();
        resultMap.put(TAG_RESULT_BODY, bodyMap);

        return resultMap;
    }

    protected Map addStatus(Map resultMap, String code, String message) {
        Map statusMap = new LinkedHashMap();
        statusMap.put(TAG_RESULT_CODE, code);
        statusMap.put(TAG_RESULT_MESSAGE, message);

        resultMap.put(TAG_RESULT_STATUS, statusMap);

        return resultMap;
    }

    protected Map addStatus(Map resultMap, String code, String message, String details) {
        Map resutlMap = this.addStatus(resultMap, code, message);
        ((Map) resultMap.get(TAG_RESULT_STATUS)).put(TAG_RESULT_DETAILS, details);

        return resultMap;
    }
}
