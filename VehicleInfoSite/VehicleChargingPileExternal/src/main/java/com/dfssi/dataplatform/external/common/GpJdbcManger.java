package com.dfssi.dataplatform.external.common;

import java.sql.*;
import java.util.*;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/4 18:49
 */
public class GpJdbcManger {
    private static String url = "";
    // 数据库用户名
    private static String username = "";

    // 数据库密码
    private static String password = "";
    private static String driver = "";
    private Connection conn = null;

    public void setParam() {
        Properties properties = PropertiesUtil.getProperties("application.properties");
        this.driver = properties.getProperty("spring.datasource.postgresql.driver-class-name");
        this.url = properties.getProperty("spring.datasource.postgresql.url");
        this.username = properties.getProperty("spring.datasource.postgresql.username");;
        this.password = properties.getProperty("spring.datasource.postgresql.password");;
    }

    public Connection getConnection() {
        try {
            // 加载驱动
            Class.forName(driver);
            // 获取连接
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public List<HashMap<String, String>> executeQuery(String sql) throws Exception{
        conn=getConnection();
        ResultSet result = null;
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                HashMap<String, String> map = new HashMap<String, String>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for(int i=1;i<=columnCount;i++){
                    String key = metaData.getColumnLabel(i).toUpperCase();
                    final String value = resultSet.getString(i);
                    map.put(key,value);
                }
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            conn.close();
        }
        return list;
    }
}
