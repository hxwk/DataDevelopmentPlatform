package com.dfssi.dataplatform.datasync.plugin.sqlsource.common;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * oracle
 * dbhelper class ,executor query database by connection pool
 * Created by jian on 2017/12/11.
 * @author JianKang
 */
@Deprecated
public class DBHelper {
    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    public DBHelper(String jdbcUrl, String username, String password, int poolSize, int maxPoolSize, int maxIdleTime) {
        ConnectionPool.build(jdbcUrl, username, password, poolSize, maxPoolSize, maxIdleTime);
    }

    /**
     * @param table
     * @param startRowNum
     * @param endNumber
     * @param columns
     * @return
     */
    public List<JSONObject> query(String table, int startRowNum, int endNumber, Set<String> columns) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from (select ROWNUM AS ROWNO, ");
        for (String column : columns) {
            sqlBuilder.append(column).append(",");
        }
        sqlBuilder = new StringBuilder(sqlBuilder.toString().substring(0, sqlBuilder.length() - 1));
        sqlBuilder.append("  from ").append(table).append(" where ROWNUM <").append(endNumber).append(") " +
                "TABLE_ALIAS where TABLE_ALIAS.ROWNO >=").append(startRowNum);

        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            List<JSONObject> list = Lists.newArrayList();
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sqlBuilder.toString());
            resultSet = statement.executeQuery();
            //JSONArray jsonArray = new JSONArray();
            while(resultSet.next()){
                System.out.println("ha");
                for (String column : columns) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(column, resultSet.getObject(column));
                    list.add(jsonObject);
                }
            }

            return list;
        } catch (Exception e) {
            logger.error("result set to jsonObject error", e.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("result set close error", e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("prepare statements close error", e.getMessage());

                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        logger.error("connection pool close error", e.getMessage());
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public void close(){
        ConnectionPool.close();
    }
}

