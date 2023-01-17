package com.dfssi.dataplatform.datasync.plugin.interceptor.common;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description
 */
public class Utils {
    static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * map -> json
     * @param entity
     * @return
     */
    public static String map2Json(Map<String,Object> entity){
        return JSON.toJSONString(entity);
    }

    /**
     * json -> map
     * @param json
     * @return
     */
    public static Map<String,Object> json2Map(String json){
        return JSON.parseObject(json);
    }

    /**
     * json -> List
     * @param json
     * @return
     */
    public static List<String> json2List(String json){
        return (List<String>) JSON.parseObject(json);
    }
}
