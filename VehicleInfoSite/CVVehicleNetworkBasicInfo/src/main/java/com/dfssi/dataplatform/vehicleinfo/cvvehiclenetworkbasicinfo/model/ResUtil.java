package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ResUtil {

    public static String DATETIME_FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    /**
     * 根据传入的响应code、详细信息对象、描述信息msg封装返回json对象
     *
     * @param code
     * @param data
     * @param msg
     * @return "{"code":${code},"data":${data},"msg":${msg}}"
     */
    public static String getJsonStr(int code, String msg, Object data) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("data", data);
        json.put("msg", msg);
        return JSON.toJSONStringWithDateFormat(json, DATETIME_FORMAT_SECOND,
                SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 根据传入的响应code和具体描述信息msg封装返回json字符串
     *
     * @param code
     * @param msg
     * @return "{"code":${code},"msg":${msg}}"
     */
    public static String getJsonStr(int code, String msg) {
        return getJson(code, msg).toJSONString();
    }

    /**
     * 根据传入的响应code和具体描述信息msg封装返回json对象
     *
     * @param code
     * @param msg
     * @return {"code":${code},"msg":${msg}}
     */
    public static JSONObject getJson(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json;
    }
}
