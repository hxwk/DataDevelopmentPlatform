package com.dfssi.dataplatform.analysis.preprocess.process.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class JacksonUtils {

    public static String getAsString(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            return je.getAsString();
        }

        return null;
    }

    public static Long getAsLong(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            return je.getAsLong();
        }

        return null;
    }

    public static Integer getAsInteger(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            return je.getAsInt();
        }

        return null;
    }

    public static BigInteger getAsBigInteger(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            return je.getAsBigInteger();
        }

        return null;
    }

    public static Double getAsDouble(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            return je.getAsDouble();
        }

        return null;
    }

    public static BigDecimal getAsBigDecimal(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            return je.getAsBigDecimal();
        }

        return null;
    }

    public static List getAsArray(JsonObject jsonObject, String key, String paramizedType) {
        List result = new ArrayList();
        if (jsonObject == null) {
            return result;
        }

        JsonElement je = jsonObject.get(key);
        if (je != null && !je.isJsonNull()) {
            JsonArray jsonArray = je.getAsJsonArray();
            for (JsonElement linkJsonEl : jsonArray) {
                if ("string".equalsIgnoreCase(paramizedType)) {
                    result.add(linkJsonEl.getAsString());
                } else if ("long".equalsIgnoreCase(paramizedType)) {
                    result.add(linkJsonEl.getAsLong());
                } else if ("int".equalsIgnoreCase(paramizedType)) {
                    result.add(linkJsonEl.getAsInt());
                } else if ("biginteger".equalsIgnoreCase(paramizedType) || "bigint".equalsIgnoreCase(paramizedType)) {
                    result.add(linkJsonEl.getAsBigInteger());
                } else if ("double".equalsIgnoreCase(paramizedType)) {
                    result.add(linkJsonEl.getAsDouble());
                } else if ("decimal".equalsIgnoreCase(paramizedType)) {
                    result.add(linkJsonEl.getAsBigDecimal());
                } else {
                    result.add(linkJsonEl.getAsString());
                }
            }
        }

        return result;
    }

    public static Object getValue(JsonObject jsonObject, String key, String typeName, String paramizedType) {
        if ("string".equalsIgnoreCase(typeName)) {
            return getAsString(jsonObject, key);
        } else if ("long".equalsIgnoreCase(typeName)) {
            return getAsLong(jsonObject, key);
        } else if ("int".equalsIgnoreCase(typeName)) {
            return getAsInteger(jsonObject, key);
        } else if ("biginteger".equalsIgnoreCase(paramizedType) || "bigint".equalsIgnoreCase(typeName)) {
            return getAsLong(jsonObject, key);
        } else if ("double".equalsIgnoreCase(typeName)) {
            return getAsDouble(jsonObject, key);
        } else if ("decimal".equalsIgnoreCase(typeName)) {
            return getAsBigDecimal(jsonObject, key);
        } else if ("array".equalsIgnoreCase(typeName)) {
            List list = getAsArray(jsonObject, key, paramizedType);
            return list.toArray();
        } else {
            return null;
        }
    }

    public static boolean isBlank(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return true;
        }

        JsonElement je = jsonObject.get(key);

        return (je == null || je.isJsonNull());
    }
}
