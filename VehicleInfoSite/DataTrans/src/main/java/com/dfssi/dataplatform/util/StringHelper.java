package com.dfssi.dataplatform.util;

import java.util.UUID;

public class StringHelper {
    public static boolean isEmpty(String str) {
        if ("".equals(str) || str == null){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isNotEmpty(String str) {
        if (!"".equals(str) && str != null){
            return true;
        }else{
            return false;
        }
    }

    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        //String uuidStr=str.replace("-", "");
        return str;
    }

    public static boolean equalsIgnoreCase(String content1,String content2){
        if (content1.toLowerCase().equals(content2.toLowerCase())){
            return true;
        }else{
            return false;
        }
    }
}
