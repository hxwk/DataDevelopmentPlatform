package com.dfssi.dataplatform.datasync.plugin.source.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * DBHelper, crawler to content to mysql connector
 * @author jianKang
 * @date 2017/11/29
 */
public class DBHelper {
    static final Logger logger = LoggerFactory.getLogger(DBHelper.class);
    private static final String driverName = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://172.16.1.241:3306/sourcedata";
    //private static final String url = "jdbc:mysql://192.168.253.140:3306/sourcedata";
    private static final String userName = "kangj";
    //private static final String userName = "root";
    private static final String password = "112233";
    //private static final String password = "123456";

    private static Connection connection;
    PreparedStatement pst = null;

    public DBHelper() {
    }

    public void insertAndDelete(String sql){
        Connection conn = null;
        try{
            logger.info("sql is "+sql);
            conn = DBHelper.getConnection(url, userName,password);
            try {
                pst = conn.prepareStatement(sql);
                pst.executeUpdate(sql);
            } catch (SQLException e) {
                logger.error("insert and delete data executeupdate error, please check.",e.getMessage());
            }
        } catch (Exception e) {
            logger.error("DBHelper class insertAndDelete error, please check.",e.getMessage());
        } finally {
            try {
                pst.close();
            } catch (SQLException e) {
                logger.error("statements close error, please check.",e.getMessage());
            }
        }
    }

    /**
     * 单例模式返回数据库连接对象，供外部调用
     */
    public static Connection getConnection(String url, String username, String password) throws Exception {
        if (connection == null) {
            connection = DriverManager.getConnection(url, username, password);
            return connection;
        }
        return connection;
    }

    public void close() {
        try {
            connection.close();
            this.pst.close();
        } catch (SQLException e) {
            logger.error("connection close error, please check.",e.getMessage());
        }
    }
}
