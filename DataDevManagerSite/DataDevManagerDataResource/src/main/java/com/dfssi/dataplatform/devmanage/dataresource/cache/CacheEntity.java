package com.dfssi.dataplatform.devmanage.dataresource.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hannibal on 2018-01-11.
 */
public class CacheEntity {

    //数据类型字典缓存
    public static Map<Integer, String> dataResourceTypeCache = new HashMap<>();

    //字段类型数据缓存
    public static Map<Integer, String> fieldTypeCache = new HashMap<>();

}
