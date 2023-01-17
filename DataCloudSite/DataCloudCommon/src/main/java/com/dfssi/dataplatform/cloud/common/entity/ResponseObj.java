package com.dfssi.dataplatform.cloud.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口返回
 * @author yanghs
 * @since 2018-4-4 11:16:00
 */
public class ResponseObj extends LinkedHashMap implements Serializable{

    private static final String RESULT_DATA = "data";//业务数据
    private static final String RESULT_STATUS = "status";//状态信息
    private static final String CODE = "code";//状态码
    private static final String MSG = "msg";//消息
    private static final String DETAILS = "details";//详细信息

    public static final String CODE_SUCCESS = "200";//请求成功
    public static final String CODE_FAIL_B = "9999";//失败
    public static final String CODE_FAIL_T = "10001";//token认证失败
    public static final String CODE_FAIL_P = "10002";//参数格式不正确
    public static final String CODE_FAIL_O = "10003";//请求频次超过限额


    /**
     * 构造器
     * @return
     */
    public static ResponseObj createResponseObj() {
        return new ResponseObj();
    }

    /**
     * @param data 业务数据
     */
    public void setData(Object data) {
        this.put(RESULT_DATA, data);
    }

    /**
     * @param code 状态码
     * @param msg 消息
     * @param details 详细信息
     */
    public void setStatus(String code, String msg, String details) {
        Map<String, String> statusInfo = new HashMap<String, String>();
        statusInfo.put(CODE, code);
        statusInfo.put(MSG, msg);
        statusInfo.put(DETAILS, details);
        this.put(RESULT_STATUS, statusInfo);
    }


}
