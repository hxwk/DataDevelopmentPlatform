package com.dfssi.dataplatform.analysis.fuel;

import com.dfssi.common.databases.DBCommon;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Description:
 *     检查库中是否存在对应的表， 不存在则创建
 * @author LiXiaoCong
 * @version 2018/1/22 15:27
 */
public class FuelTables {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuelTables.class);

    public static void checkAndCreateFuelTable(String table, Connection connection){

        if(!checkTableExist(table, connection)){

            StringBuilder sql = new StringBuilder();
            sql.append("create table ").append(table).append(" (");

            sql.append("id text NOT NULL,");
            sql.append("tripid varchar(64) NOT NULL,");
            sql.append("sim varchar(16),");
            sql.append("vid varchar(64) NOT NULL,");
            sql.append("lon text NOT NULL,");
            sql.append("lat text NOT NULL,");
            sql.append("totalmile float8 NOT NULL,");
            sql.append("mile float8 NOT NULL,");
            sql.append("isplateau int2 DEFAULT 0,");
            sql.append("alt text,");
            sql.append("totalfuel float8 NOT NULL,");
            sql.append("fuel float8 NOT NULL,");
            sql.append("iseco int2 DEFAULT 0,");
            sql.append("uploadtime text NOT NULL,");
            sql.append("routeid varchar(64),");
            sql.append("starttime int8 NOT NULL,");
            sql.append("endtime int8 NOT NULL,");
            sql.append("interval int8 NOT NULL,");
            sql.append("rjsk int8 NOT NULL");

            sql.append(")");

            LOGGER.info(String.format("库中不存在油耗数据表， 开始创建：\n\t sql = %s", sql));

            executeCreateTableSql(table, sql.toString(), connection, new String[]{"vid"});
        }
    }

    public static void checkAndCreateTripTable(String table, Connection connection){

        if(!checkTableExist(table, connection)){

            StringBuilder sql = new StringBuilder();
            sql.append("create table ").append(table).append(" (");

            sql.append("id varchar(64) NOT NULL,");
            sql.append("vid varchar(64) NOT NULL,");
            sql.append("sim varchar(32),");
            sql.append("starttime int8 NOT NULL,");
            sql.append("endtime int8 NOT NULL,");
            sql.append("interval int8 NOT NULL,");
            sql.append("totalmile float8 NOT NULL,");
            sql.append("totalfuel float8 NOT NULL,");
            sql.append("starttotalmile float8 NOT NULL,");
            sql.append("starttotalfuel float8 NOT NULL,");
            sql.append("startlat float8 NOT NULL,");
            sql.append("startlon float8 NOT NULL,");
            sql.append("endlat float8 NOT NULL,");
            sql.append("endlon float8 NOT NULL,");
            sql.append("iseco int2 DEFAULT 0,");
            sql.append("isover int2 DEFAULT 0,");
            sql.append("isvalid int2 DEFAULT 1,");
            sql.append("orderid varchar(32),");
            sql.append("endtotalmile float8 NOT NULL,");
            sql.append("endtotalfuel float8 NOT NULL,");
            sql.append("isprocessed int2 DEFAULT 0,");
            sql.append("processtime int8");

            sql.append(")");

            LOGGER.info(String.format("库中不存在行程数据表， 开始创建：\n\t sql = %s", sql));
            executeCreateTableSql(table, sql.toString(), connection, new String[]{"vid"});
        }

    }

    //vehicle_abnormal_fuel

    public static void checkAndCreateAbnormalFuelTable(String table, Connection connection){

        if(!checkTableExist(table, connection)){

            StringBuilder sql = new StringBuilder();
            sql.append("create table ").append(table).append(" (");

            sql.append("id varchar(64) NOT NULL,");
            sql.append("vid varchar(64) NOT NULL,");
            sql.append("starttime int8 NOT NULL,");
            sql.append("endtime int8 NOT NULL,");
            sql.append("positions text NOT NULL,");
            sql.append("count int4 NOT NULL");
            sql.append(")");

            LOGGER.info(String.format("库中不存在油耗异常数据表， 开始创建：\n\t sql = %s", sql));
            executeCreateTableSql(table, sql.toString(), connection, new String[]{"vid"});
        }

    }

    public static void checkAndCreateAbnormalDrivingTable(String table, Connection connection){

        if(!checkTableExist(table, connection)){

            StringBuilder sql = new StringBuilder();
            sql.append("create table ").append(table).append(" (");

            sql.append("id varchar(64) NOT NULL,");
            sql.append("vid varchar(64) NOT NULL,");
            sql.append("alarm varchar(32) NOT NULL,");
            sql.append("alarmlabel int2 NOT NULL,");
            sql.append("starttime int8 NOT NULL,");
            sql.append("endtime int8 NOT NULL,");
            sql.append("lon float8 NOT NULL,");
            sql.append("lat float8 NOT NULL,");
            sql.append("speed float8 NOT NULL,");
            sql.append("degree int4,");
            sql.append("alarmtype int4,");
            sql.append("alarmcode int4,");
            sql.append("count int4 NOT NULL");
            sql.append(")");

            LOGGER.info(String.format("库中不存在驾驶异常数据表， 开始创建：\n\t sql = %s", sql));
            executeCreateTableSql(table, sql.toString(), connection, new String[]{"vid", "alarmlabel"});
        }

    }


    public static void checkAndCreateTotalFuelTable(String table, Connection connection){

        if(!checkTableExist(table, connection)){

            StringBuilder sql = new StringBuilder();
            sql.append("create table ").append(table).append(" (");

            sql.append("vid varchar(64) NOT NULL,");
            sql.append("starttime int8 NOT NULL,");
            sql.append("endtime int8 NOT NULL,");
            sql.append("totaltime int8 NOT NULL,");
            sql.append("totalmile float8 NOT NULL,");
            sql.append("totalfuel float8 NOT NULL");

            sql.append(")");

            LOGGER.info(String.format("库中不存在总计油耗数据表， 开始创建：\n\t sql = %s", sql));
            executeCreateTableSql(table, sql.toString(), connection, new String[]{"vid"});
        }

    }


    private static void executeCreateTableSql(String table, String sql, Connection connection, String[] indexFields){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);

            createIndex(indexFields, table, connection);
        } catch (SQLException e) {
            Preconditions.checkArgument(false, e);
        }finally {
            DBCommon.close(statement);
        }
    }

    private static boolean checkTableExist(String table, Connection connection){
        boolean exist = false;
        ResultSet resultSet = null;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(),
                    null, new String[]{"TABLE"});

            while ((!exist && resultSet.next())){
                exist = table.equalsIgnoreCase(resultSet.getString("TABLE_NAME"));
            }
            resultSet.close();
        } catch (SQLException e) {
            Preconditions.checkArgument(false, e);
        }finally {
            DBCommon.close(resultSet);
        }
        return exist;
    }

    private static void createIndex(String[] indexFields,
                                    String tableName,
                                    Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String indexSQL;
        for(String field: indexFields){
            indexSQL = String.format("CREATE INDEX %s_%s ON %s (%s)", tableName, field, tableName, field);
            statement.addBatch(indexSQL);
        }
        statement.executeBatch();
        statement.close();
    }

}
