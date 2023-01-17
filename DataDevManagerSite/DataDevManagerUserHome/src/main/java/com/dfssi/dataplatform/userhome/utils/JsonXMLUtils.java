package com.dfssi.dataplatform.userhome.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * json及xml工具类
 *
 * @author wanlong
 */
public class JsonXMLUtils {

    public static String obj2json(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T json2obj(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    public static <T> Map<String, Object> json2map(String jsonStr) {
        return JSON.parseObject(jsonStr, Map.class);
    }

    public static <T> T map2obj(Map<?, ?> map, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }
}
