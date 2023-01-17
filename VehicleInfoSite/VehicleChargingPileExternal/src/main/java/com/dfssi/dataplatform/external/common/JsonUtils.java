package com.dfssi.dataplatform.external.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/30 9:18
 */

public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    public static <T> T fromJson(String json, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
//        try {
        return gson.fromJson(json, clazz);
//        } catch (Exception ex) {
//            LOGGER.error(json + "json转换失败! json信息如下：", ex);
//            LOGGER.error(json);
//            return null;
//        }
    }
}
