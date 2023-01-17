package com.dfssi.dataplatform.analysis.mysql;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/06/15 14:17
 */
public class MysqlTableColumnAndType {
    static Logger logger = Logger.getLogger(MysqlTableColumnAndType.class);

    private final static String DRIVER_NAME = "com.mysql.jdbc.Driver";
    private final static String BASE_URL = "jdbc:mysql://localhost:3306/";
    private final static String USER_NAME = "root";
    private final static String PASS_WORD = "123456";

    /**
     * @description:根据库名与表名，查询mysql表字段和类型
     * @param databaseName
     * @param tableName
     * @return column:columnType
     * @throws Exception
     */
    public static HashMap<String, String> getTableColumnAndType(String databaseName, String tableName) throws Exception {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> columnMap = null;
        String sql = "select column_name, data_type from information_schema.columns where table_schema ='" + databaseName + "'  and table_name = '" + tableName + "'";
        try {
            pstmt = conn.prepareStatement(sql);
            logger.info("根据库名与表名，查询mysql表字段和类型的执行sql:" + sql);
            rs = pstmt.executeQuery();
            columnMap = new HashMap<String, String>();
            while (rs.next()) {
                columnMap.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, rs, pstmt);
        }
        return columnMap;
    }

    /**
     * @description::根据指定列查询指定列的数据,最终显示n条数据
     * @param databaseName
     * @param tableName
     * @param colNames
     * @param number
     * @return List<Map<columnName, columnValue>>
     * @throws Exception
     */
    public static List<Map<String, Object>> getSpecialColumnDataOfDiffDB(String databaseName, String tableName, String colNames, int number) throws Exception {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Object> lineMap = null;
        List<Map<String, Object>> resultList = null;
        List columnNameList = new ArrayList();
        String sql = null;
        // 判断列名传入的是否是"*"
        if (!colNames.isEmpty()){
            if (!colNames.equals("*")) {
                columnNameList =  Arrays.asList(colNames.split(";"));
                String spliceColumn = Joiner.on(",").join(columnNameList);
                sql = "select " + spliceColumn + " from " + databaseName + "." + tableName + " limit " + number;
            } else {
                sql = "select * from " + databaseName + "." + tableName + " limit " + number;
            }
        }
        try {
            pstmt = conn.prepareStatement(sql);
            logger.info("获取指定列数据的执行sql:" + sql);
            rs = pstmt.executeQuery();

            resultList = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                lineMap = new HashMap<String, Object>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    lineMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                resultList.add(lineMap);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(conn, rs, pstmt);
        }
        return resultList;
    }

    /**
     * @description:根据指定列查询指定列的数据
     * @param databaseName
     * @param tableName
     * @param columnNameList
     * @return list<columnName:columnValue></>
     * @throws Exception
     */
    public static List<Map<String, Object>> getTableSpecifiedColumnData(String databaseName, String tableName, List<String> columnNameList) throws Exception {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Object> lineMap = null;
        List<Map<String, Object>> resultList = null;
        String spliceColumn = Joiner.on(",").join(columnNameList);

        String sql = "select " + spliceColumn + " from " + databaseName + "." + tableName;
        try {
            pstmt = conn.prepareStatement(sql);
            logger.info("获取指定列数据的执行sql:" + sql);
            rs = pstmt.executeQuery();

            resultList = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                lineMap = new HashMap<String, Object>();
                for (int i = 0; i < columnNameList.size(); i++) {
                    lineMap.put(columnNameList.get(i), rs.getObject(columnNameList.get(i)));
                }
                resultList.add(lineMap);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(conn, rs, pstmt);
        }
        return resultList;
    }

    /**
     * @description:根据库名获取全部的表名
     * @param databaseName
     * @return List<tableName></>
     * @throws Exception
     */
    public static List<String> getTableNameByDatabaseName(String databaseName) throws Exception {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> resultList = null;
        String sql = "select table_name from information_schema.tables where table_schema= '" + databaseName + "'";
        try {
            pstmt = conn.prepareStatement(sql);
            logger.info("根据库名获取表名的执行sql:" + sql);
            rs = pstmt.executeQuery();
            resultList = new ArrayList<String>();
            while (rs.next()) {
                resultList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(conn, rs, pstmt);
        }
        return resultList;
    }


//    public static void main(String[] args) {
//        try {
//            HashMap<String, String> tableColumnAndType = getTableColumnAndType("test", "tb");
//            tableColumnAndType.forEach((k, v) ->
//                    System.out.println(k + ":" + v)
//            );

//            List<String> columnList = new ArrayList<String>();
//            columnList.add("id");
//            columnList.add("name");

//            String columnStr = "id;name";
//            String[] columnList = columnStr.split(";");
//            List<Map<String, Object>> tableSpecifiedColumnData = getTableSpecifiedColumnData("test", "tb", Arrays.asList(columnList));
//            tableSpecifiedColumnData.forEach( columns ->
//                    columns.forEach((key, value) -> System.out.println(key + ":" + value))
//            );

//            String columnStr = "id;age";
//            List<Map<String, Object>> tableSpecifiedColumnData = getSpecialColumnDataOfDiffDB("test", "tb", columnStr, 3);
//            tableSpecifiedColumnData.forEach( columns ->
//                    columns.forEach((key, value) -> System.out.println(key + ":" + value))
//            );

//            List<String> test = getTableNameByDatabaseName("brush_taobao_order");
//            test.forEach(t ->
//                    System.out.println(t)
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取数据库连接
     *
     * @return 连接对象
     */
    protected static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(BASE_URL, USER_NAME, PASS_WORD);
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动类");
        } catch (SQLException e) {
            System.out.println("建立连接错误！");
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn  数据库连接
     * @param rs    结果集
     * @param pstmt 命令对象
     */
    public static void closeAll(Connection conn, ResultSet rs, Statement pstmt) {
        try {
            if (null != rs && !rs.isClosed()) {
                rs.close();
                rs = null;
            }
            if (null != pstmt && !pstmt.isClosed()) {
                pstmt.close();
                pstmt = null;
            }
            if (null != conn && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            logger.error("关闭流信息出错");
            e.printStackTrace();
        }
    }
}
