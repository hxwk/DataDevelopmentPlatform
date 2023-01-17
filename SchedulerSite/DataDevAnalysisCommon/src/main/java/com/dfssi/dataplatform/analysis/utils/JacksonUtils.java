package com.dfssi.dataplatform.analysis.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public static boolean isBlank(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return true;
        }

        JsonElement je = jsonObject.get(key);

        return (je == null || je.isJsonNull());
    }
}
