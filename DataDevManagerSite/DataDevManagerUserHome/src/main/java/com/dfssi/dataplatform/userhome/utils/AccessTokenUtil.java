package com.dfssi.dataplatform.userhome.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 访问令牌工具类
 * @author wanlong
 */
public class AccessTokenUtil {


    /**
     * 根据用户token和微服务id生成访问令牌
     * @param userToken
     * @param serviceId
     * @return
     */
    public static String serviceAccessToken(String userToken, String serviceId){
        return MD5(userToken+"."+serviceId+"."+System.currentTimeMillis());
    }

    /**
     * 根据用户名，密码，当前时间戳生成访问令牌
     * @param userName
     * @param password
     * @return
     */
    public static String userAccessToken(String userName, String password){
        return MD5(userName+"."+password+"."+System.currentTimeMillis());
    }

    public static String MD5(String target){
        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(target.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

    }

    public static void main(String[] args) throws Exception {
        System.out.println("加密后:" + userAccessToken("username", MD5("password")));
    }
}
