package com.dfssi.dataplatform.vehicleinfo.vehicleroad.util;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Description:
 *   用于封装响应请求的结果
 * @author LiXiaoCong
 * @version 2017/11/22 10:34
 */
public class ResponseUtil {
    private ResponseUtil(){}

    public static Map<String, Object> success(){
        return successBase();
    }

    public static Map<String, Object> success(Object data){
        Map<String, Object> res = successBase();
        if(data == null) data = new Object[0];
        res.put("data", data);
        return res;
    }

    public static Map<String, Object> error(Object data){
        Map<String, Object> res = errorBase(data);
        return res;
    }

    public static Map<String, Object> success(long total, Object record){
        Map<String, Object> data = Maps.newHashMap();
        data.put("total", total);
        data.put("record", record);
        return success(data);
    }

    private static Map<String, Object> successBase(){
        Map<String, Object> res = Maps.newHashMap();

        Map<String, Object> status = Maps.newHashMap();
        status.put("code", 200);
        status.put("msg", "请求成功");

        res.put("status", status);
        return res;
    }


    private static Map<String, Object> errorBase(Object detail){
        Map<String, Object> res = Maps.newHashMap();

        Map<String, Object> status = Maps.newHashMap();
        status.put("code", 500);
        status.put("msg", "请求失败");
        status.put("details", detail);

        res.put("status", status);
        return res;
    }
}
