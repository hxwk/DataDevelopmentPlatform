package com.dfssi.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/15 19:00
 */
public class ConfigUtils {
    private final Logger logger = LogManager.getLogger(ConfigUtils.class);

    private ConfigUtils(){}

    public static <T> T getAs(Map config, Object key){
        return getAsWithDefault(config, key, null);
    }

    public static <T> T getAsWithKeys(Map config, Object ... keys){
        Object value = getByKeys(config, keys);
        return value == null ? null : (T)value;
    }

    public static <T> T getAsWithDefault(Map config, Object key, T defaultValue){
        Object o = config.get(key);
        return (o == null) ? defaultValue : (T)o;
    }

    public static String getAsStringWithDefault(Map config, Object key, String defaultValue){
        Object o = config.get(key);
        return (o == null) ? defaultValue : String.valueOf(o);
    }

    public static String getAsString(Map config, Object key){
        return getAsStringWithDefault(config, key, null);
    }

    public static String getAsStringByKeys(Map config, Object ... keys){
        Object value = getByKeys(config, keys);
       return (value == null) ? null : String.valueOf(value);
    }

    public static Integer getAsIntegerWithDefault(Map config, Object key, Integer defaultValue){
        Object o = config.get(key);
        return (o == null) ? defaultValue : Integer.parseInt(String.valueOf(o));
    }

    public static Integer getAsIntegerByKeys(Map config, Object ... keys){
        Object value = getByKeys(config, keys);
        return (value == null) ? null :  Integer.parseInt(String.valueOf(value));
    }

    public static Integer getAsInteger(Map config, Object key){
        return getAsIntegerWithDefault(config, key, null);
    }

    public static Long getAsLongWithDefault(Map config, Object key, Long defaultValue){
        Object o = config.get(key);
        return (o == null) ? defaultValue : Long.parseLong(String.valueOf(o));
    }

    public static Long getAsLongByKeys(Map config, Object ... keys){
        Object value = getByKeys(config, keys);
        return (value == null) ? null : Long.parseLong(String.valueOf(value));
    }

    public static Long getAsLong(Map config, Object key){
        return getAsLongWithDefault(config, key, null);
    }

    public static Float getAsFloatWithDefault(Map config, Object key, Float defaultValue){
        Object o = config.get(key);
        return (o == null) ? defaultValue : Float.parseFloat(String.valueOf(o));
    }

    public static Float getAsFloatByKeys(Map config, Object ... keys){
        Object value = getByKeys(config, keys);
        return (value == null) ? null : Float.parseFloat(String.valueOf(value));
    }

    public static Float getAsFloat(Map config, Object key){
        return getAsFloatWithDefault(config, key, null);
    }

    public static Double getAsDoubleWithDefault(Map config, Object key, Double defaultValue){
        Object o = config.get(key);
        return (o == null) ? defaultValue : Double.parseDouble(String.valueOf(o));
    }

    public static Double getAsDoubleByKeys(Map config, Object ... keys){
        Object value = getByKeys(config, keys);
        return (value == null) ? null : Double.parseDouble(String.valueOf(value));
    }

    public static Double getAsDouble(Map config, Object key){
        return getAsDoubleWithDefault(config, key, null);
    }


    private static Object getByKeys(Map config, Object[] keys) {
        Object value = null;
        for(Object key : keys){
            value = config.get(key);
            if(value != null) break;
        }
        return value;
    }

}
