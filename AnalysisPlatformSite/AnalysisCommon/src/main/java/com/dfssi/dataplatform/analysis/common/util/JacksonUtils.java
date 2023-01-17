package com.dfssi.dataplatform.analysis.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    public static JsonObject getAsJsonObject(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

//        JsonElement je = jsonObject.get(key);
//        if (je != null && !je.isJsonNull()) {
//            return new JsonParser().parse(je.getAsString()).getAsJsonObject();
//        }

        return jsonObject.getAsJsonObject(key);
    }

    public static JsonArray getAsJsonArray(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return null;
        }

//        JsonElement je = jsonObject.get(key);
//        if (je != null && !je.isJsonNull()) {
//            return new JsonParser().parse(je.getAsString()).getAsJsonArray();
//        }

        return jsonObject.getAsJsonArray(key);
    }

    public static boolean isBlank(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return true;
        }

        JsonElement je = jsonObject.get(key);

        return (je == null || je.isJsonNull());
    }
}
