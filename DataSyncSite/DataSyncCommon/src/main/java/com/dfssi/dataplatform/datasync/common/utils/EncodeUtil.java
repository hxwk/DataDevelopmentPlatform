package com.dfssi.dataplatform.datasync.common.utils;

import java.util.Base64;

/**
 * Created by cxq on 2018/1/17.
 */
public class EncodeUtil {
    public static  String encode(String numStr,int salt){
        numStr = numStr.substring(1);
        int length = numStr.length();
        StringBuffer stringBuffer = new StringBuffer();
        while(length/4>0){
            String intStr = numStr.substring(0, 4);
            String hexStr = Integer.toHexString(Integer.parseInt(intStr)^salt);
            numStr = numStr.substring(4);
            length=numStr.length();
            stringBuffer.append(hexStr);
        }
        if(length!=0){
            String hexStr = Integer.toHexString(Integer.parseInt(numStr)^salt);
            stringBuffer.append(hexStr);
        }
        Base64.Encoder encoder = Base64.getMimeEncoder(8, new String(",").getBytes());
        String encoderStr = encoder.encodeToString(stringBuffer.toString().getBytes());
        if(encoderStr.length()<8){
            encoderStr+=encoder.encodeToString(String.valueOf(salt).getBytes());
        }
        if(encoderStr.length()>8){
            encoderStr = encoderStr.substring(0,8);
        }
        return encoderStr;
    }
}
