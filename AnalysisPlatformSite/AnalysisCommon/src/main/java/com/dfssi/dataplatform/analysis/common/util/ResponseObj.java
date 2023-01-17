package com.dfssi.dataplatform.analysis.common.util;

import java.util.LinkedHashMap;

public class ResponseObj extends LinkedHashMap {

    public static final String ATTR_NAME_DATA = "data";
    public static final String ATTR_NAME_TOTAL = "total";
    public static final String ATTR_NAME_CODE = "code";
    public static final String ATTR_NAME_ERRMSG = "errMsg";

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAIL = -1;
    public static final int CODE_CONTENT_NOT_EXIST = 1000;
    public static final String MSG_SUCCESS_QUERY_SUCCESS = "查询成功";

    public static ResponseObj createResponseObj() {
        return new ResponseObj();
    }

    public void setData(Object data) {
        this.put(ATTR_NAME_DATA, data);
    }

    public void setTotal(long total) {
        this.put(ATTR_NAME_TOTAL, total);
    }

    public void setMsgCode(int msgCode) {
        this.put(ATTR_NAME_CODE, msgCode);
    }

    public void setMsg(String msg) {
        this.put(ATTR_NAME_ERRMSG, msg);
    }

    public void setSuccessCode() {
        this.setMsgCode(CODE_SUCCESS);
    }

    public void addKeyVal(Object key, Object value) {
        this.put(key, value);
    }

    public void setDetailMsg(String msg) {
        this.put("detail.", msg);
    }

    public void buildSuccessMsg(String msg) {
        this.setSuccessCode();
        this.setMsg(msg);
    }

    public void buildFailMsg(Integer code, String msg, String detail) {
        if (code == null) code = 9999;
        this.setMsgCode(code);
        this.setMsg(msg);
        this.setDetailMsg(detail);
    }
}
