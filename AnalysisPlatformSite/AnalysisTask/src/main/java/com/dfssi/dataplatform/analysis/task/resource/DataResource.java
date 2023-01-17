package com.dfssi.dataplatform.analysis.task.resource;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:根据DBConnectEntity获取数据
 * @author pengwk
 * @version 2018/6/22 11:21
 */
public class DataResource {
    private final Logger logger = LogManager.getLogger(DataResource.class);

    private DBConnectEntity dbConnectEntity;

    public DataResource(DBConnectEntity dbConnectEntity) {
        this.dbConnectEntity = dbConnectEntity;
    }

    public List<String> listTables() throws SQLException, ClassNotFoundException {
        Connection connection = DBCommon.getConn(dbConnectEntity);
        DatabaseMetaData metaData = connection.getMetaData();

        ResultSet resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(),
                null, new String[]{"TABLE", "VIEW"});

        List<String> tables = Lists.newArrayList();
        while (resultSet.next()){
            tables.add(resultSet.getString("TABLE_NAME"));
        }
        resultSet.close();

        return tables;
    }

    public void delectTable(String tableName)  {
        Connection connection;
        Statement statement = null;
        String sql = "drop table if exists " + tableName;

        try {
            connection = DBCommon.getConn(dbConnectEntity);
            Preconditions.checkNotNull(connection, String.format("数据库%s的连接为null", dbConnectEntity.getType()));

            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            logger.error(String.format("删除%s表失败，sql = %s", dbConnectEntity.getType(), sql), e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DataBases.close(statement);
        }
    }

    public Map<String, String> getTableColumnInfo(String tableName) throws ClassNotFoundException {

        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "desc " + tableName;

        Map<String, String> records = Maps.newHashMap();
        try {
            connection = DBCommon.getConn(dbConnectEntity);
            Preconditions.checkNotNull(connection, String.format("数据库%s的连接为null", dbConnectEntity.getType()));

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                records.put(resultSet.getString(1), resultSet.getString(2));
            }

        } catch (SQLException e) {
            logger.error(String.format("查询数据库%s失败，sql = %s", dbConnectEntity.getType(), sql), e);
        }finally {
            logger.info(String.format("查询数据库%s成功，sql = %s", dbConnectEntity.getType(), sql));
            DataBases.close(resultSet);
            DataBases.close(statement);
        }

        return records;
    }

    public List<LinkedHashMap<String, Object>> getTableData(String tableName, String colNames, String condition, int number) throws ClassNotFoundException {

        String readDataSql = createReadDataSql(tableName, colNames, condition, number);
        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;

        List<LinkedHashMap<String, Object>> records = Lists.newArrayList();
        try {
            connection = DBCommon.getConn(dbConnectEntity);
            Preconditions.checkNotNull(connection, String.format("数据库%s的连接为null", dbConnectEntity.getType()));

            statement = connection.createStatement();
            resultSet = statement.executeQuery(readDataSql);

            LinkedHashMap<String, Object> record;

            if ("*".equals(colNames)) {
                int m = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    record = Maps.newLinkedHashMap();
                    for (int i = 0; i < m; i++) {
                        record.put(resultSet.getMetaData().getColumnName(i + 1),
                                resultSet.getObject(i + 1));
                    }
                    records.add(record);
                }
            }
            else{
                List<String> columns = Arrays.asList(colNames.split(";"));


                while (resultSet.next()){
                    record = Maps.newLinkedHashMap();
                    for(String column : columns){
                        record.put(column, resultSet.getObject(column));
                    }
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("查询数据库%s失败，sql = %s", dbConnectEntity.getType(), readDataSql), e);
        }finally {
            logger.info(String.format("查询数据库%s成功，sql = %s", dbConnectEntity.getType(), readDataSql));
            DataBases.close(resultSet);
            DataBases.close(statement);
        }

        return records;
    }

    //拼接sql
    private String createReadDataSql(String tableName, String colNames, String condition, int number){
        StringBuilder sql = new StringBuilder("SELECT ");
        if ("*".equals(colNames)) {
            sql.append("*").append(" FROM ");
        }
        else {
            sql.append("`").append(Joiner.on("`,`").skipNulls().join(colNames.split(";"))).append("`").append(" from ");
        }
        sql.append(tableName);
        if (!condition.isEmpty()) {
            sql.append(" WHERE ").append(condition);
        }
        if (number != 0) {
            sql.append(" LIMIT ").append(number);
        }

        return sql.toString().replace("`''`","''");
    }
}
