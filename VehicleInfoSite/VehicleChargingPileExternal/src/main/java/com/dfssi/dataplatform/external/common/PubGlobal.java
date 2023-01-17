package com.dfssi.dataplatform.external.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/1 16:22
 */
public class PubGlobal {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //    public static String OperatorSecret = "1234567890abcdef";
//    public static String DataSecret = "1234567890abcdef";
//    public static String DataSecretIV = "1234567890abcdef";
//
//    public static String SignSecret = "1234567890abcdef";
    public static String token = "0ec584d3-8228-4da0-83ec-5623a6cc12b8";
    public static HashMap<String, HashMap<String, String>> secretMap = new HashMap<String, HashMap<String, String>>();

    /**
     * @author bin.Y
     * Description:缓存密匙,当入参initializeFlag=0代表是新增调用的,只需要取未联通的
     * Date:  2018/10/13 15:06
     */
    public void cacheSecretMap(int initializeFlag) throws Exception {
        String sql = "select operator_id,operator_secret,data_secret,data_secret_iv,sign_secret,url from charge_operator_info";
        if (initializeFlag == 0) {
            sql = sql + " where connectivity='0' ";
        }
        GpJdbcManger manger = new GpJdbcManger();
        List<HashMap<String, String>> hashMaps = manger.executeQuery(sql);
        for (HashMap<String, String> map : hashMaps) {
            HashMap<String, String> secret = new HashMap<String, String>();
            secret.put("OperatorSecret", map.get("OPERATOR_SECRET"));
            secret.put("DataSecret", map.get("DATA_SECRET"));
            secret.put("DataSecretIV", map.get("DATA_SECRET_IV"));
            secret.put("SignSecret", map.get("OPERATOR_SECRET"));
            secret.put("url", map.get("URL"));
            this.secretMap.put(map.get("OPERATOR_ID"), secret);
        }
        logger.info("初始化密匙成功:" + this.secretMap);
    }
}
