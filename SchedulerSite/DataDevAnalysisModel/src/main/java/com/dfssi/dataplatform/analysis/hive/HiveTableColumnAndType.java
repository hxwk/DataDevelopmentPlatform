package com.dfssi.dataplatform.analysis.hive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/05/25 9:56
 */
public class HiveTableColumnAndType {
    static Logger logger = Logger.getLogger(HiveTableColumnAndType.class);

    private final static String DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
    private final static String BASE_URL = "jdbc:hive2://172.16.1.210:10000/";
    private final static String USER_NAME = "hive";
    private final static String PASS_WORD = "";

    public static HashMap<String, String> getHiveColumnAndType(String dataBaseName, String tableName) throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        HashMap<String, String> columnMap = null;
        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(BASE_URL + dataBaseName, USER_NAME, PASS_WORD);
            stmt = con.createStatement();
            String sql = "desc " + tableName;
            resultSet = stmt.executeQuery(sql);
            columnMap = new HashMap<String, String>();
            while (resultSet.next()) {
                columnMap.put(resultSet.getString(1), resultSet.getString(2));
            }
        } catch (ClassNotFoundException e) {
            logger.error("没有找到驱动类:" + DRIVER_NAME);
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("连接Hive的信息错误");
            e.printStackTrace();
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }
        return columnMap;
    }

    //预览hive表前N行数据，add by pengwk，2018-05-31
    public static List<Map<String, Object>> getHiveHeadN(String dataBaseName, String tableName, int n) throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> records = Lists.newArrayList();

        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(BASE_URL + dataBaseName, USER_NAME, PASS_WORD);
            stmt = con.createStatement();


            String sql = "SELECT " + "*" + " FROM " + tableName + " LIMIT " + n;
            resultSet = stmt.executeQuery(sql);
            if(null != resultSet){
                int m = resultSet.getMetaData().getColumnCount();
                Map<String, Object> record;
                while (resultSet.next()) {
                    record = Maps.newHashMap();
                    for (int i = 0; i < m; i++) {
                        record.put(resultSet.getMetaData().getColumnName(i + 1).split("\\.")[1],
                                resultSet.getObject(i + 1));
                    }
                    records.add(record);
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("没有找到驱动类:" + DRIVER_NAME);
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("连接Hive的信息错误");
            e.printStackTrace();
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }
        return records;
    }

    //获取hive表指定列N行数据
    public static List<Map<String, Object>> getHiveHeadNByCol(String dataBaseName,
                                                              String tableName,
                                                              String colNames,
                                                              int n) throws Exception {
        if (colNames.equals("*")) {
            return getHiveHeadN(dataBaseName, tableName, n);
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> records = Lists.newArrayList();

        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(BASE_URL + dataBaseName, USER_NAME, PASS_WORD);
            stmt = con.createStatement();
            String sql = "SELECT " + colNames.replace(';', ',') + " FROM " + tableName + " LIMIT " + n;
            resultSet = stmt.executeQuery(sql);

            if(null != resultSet){
                int m = resultSet.getMetaData().getColumnCount();
                Map<String, Object> record;
                while (resultSet.next()) {
                    record = Maps.newHashMap();
                    for (int i = 0; i < m; i++) {
                        record.put(resultSet.getMetaData().getColumnName(i + 1),
                                resultSet.getObject(i + 1));
                    }
                    records.add(record);
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("没有找到驱动类:" + DRIVER_NAME);
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("连接Hive的信息错误");
            e.printStackTrace();
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }
        return records;
    }

    //获取数据库所有表名
    public static HashMap<String, String> getAllTables(String dataBaseName) throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        HashMap<String, String> columnMap = null;
        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(BASE_URL + dataBaseName, USER_NAME, PASS_WORD);
            stmt = con.createStatement();
            String sql = "show tables";
            resultSet = stmt.executeQuery(sql);
            columnMap = new HashMap<String, String>();
            int i = 1;
            while (resultSet.next()) {
                columnMap.put(i + "", resultSet.getString(1));
                i++;
            }
        } catch (ClassNotFoundException e) {
            logger.error("没有找到驱动类:" + DRIVER_NAME);
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("连接Hive的信息错误");
            e.printStackTrace();
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }
        return columnMap;
    }

    //ResultSet转List
    private static List convertList(ResultSet rs) throws SQLException{
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            List rowData = new ArrayList(columnCount);
            for (int i = 0; i < columnCount; i++) {
                rowData.add(i, rs.getObject(i + 1));
            }
            list.add(rowData);
        }
        return list;
    }

    //ResultSet转Object
    private static List convertObject(ResultSet rs, String colNames) throws SQLException{
        List<Map<String, Object>> records = Lists.newArrayList();
        Map<String, Object> record;
        while (rs.next()){
            record = Maps.newHashMap();
            for(String column : colNames.split(";")){
                record.put(column, rs.getObject(column));
            }
            records.add(record);
        }
        return  records;
    }
}
