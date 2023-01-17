package com.dfssi.dataplatform.userhome.utils;

import java.util.UUID;

/**
 * 主键工具类
 * @author wanlong
 */
public class UUIDUtil {

    /**
     * 随机生成主键
     * @return
     */
    public static String getTableKey(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
