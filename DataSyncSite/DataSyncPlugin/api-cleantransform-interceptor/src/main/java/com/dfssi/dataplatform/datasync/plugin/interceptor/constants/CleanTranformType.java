package com.dfssi.dataplatform.datasync.plugin.interceptor.constants;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description
 */
public class CleanTranformType {
    public static Map<String,String> cleanTransformTypes = Maps.newConcurrentMap();
    /*static {
        cleanTransformTypes.put("searchreplace","com.dfssi.dataplatform.datasync.plugin.interceptor.factory.SearchAndReplaceHandler");
        cleanTransformTypes.put("numberhandle","com.dfssi.dataplatform.datasync.plugin.interceptor.factory.NumberHandler");
        cleanTransformTypes.put("maskhandler","com.dfssi.dataplatform.datasync.plugin.interceptor.factory.MaskHandler");
    }*/

    public static Map<String, String> getCleanTransformTypes() {
        return cleanTransformTypes;
    }

    public static void registerTypes(Map<String,String> ctTypesMap){
        cleanTransformTypes = ctTypesMap;
    }
}
