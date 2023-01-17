package com.dfssi.dataplatform.ide.service.bi.sources.database;


import com.dfssi.common.databases.ColumnTypes;
import com.dfssi.common.databases.DataBases;
import com.dfssi.dataplatform.ide.service.bi.converters.ConveterType;
import com.dfssi.dataplatform.ide.service.bi.converters.DataConverter;
import com.dfssi.dataplatform.ide.service.bi.sources.DataSource;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 11:36
 */
public abstract class DataBaseSource extends DataSource {
    private final Logger logger = LogManager.getLogger(DataBaseSource.class);

    protected DataBaseConf baseConf;
    private DataConverter dataConverter;

    public DataBaseSource(DataBaseConf baseConf) {
        super(baseConf);
        this.baseConf = baseConf;
        String type = baseConf.getConfigItemValue("chart.type");
        ConveterType conveterType = ConveterType.getConverterByName(type);
        //Preconditions.checkNotNull(conveterType, "图类型不能为空。");

        this.dataConverter = conveterType.converter(baseConf);
    }

    public List<String> listTables() throws SQLException {
        Connection connection = baseConf.getConnection();
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

    public List<Map<String, String>> getTableColumnAndTypes(String tableName) throws SQLException{
        Connection connection = baseConf.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getColumns(connection.getCatalog(), connection.getSchema(), tableName, null);

        List<Map<String, String>> columnAndTypes = Lists.newArrayList();
        Map<String, String> columnAndType;
        String columnName;
        String typeName;
        int dataType;
        while (resultSet.next()){
            //列名
            columnName = resultSet.getString("COLUMN_NAME");
            //字段数据类型(对应java.sql.Types中的常量)
            dataType  = resultSet.getInt("DATA_TYPE");
            typeName = ColumnTypes.jdbcTypeToJavaType(dataType).getSimpleName();

            columnAndType = Maps.newHashMap();
            columnAndType.put("field", columnName);
            columnAndType.put("type", typeName);

            columnAndTypes.add(columnAndType);
        }

        return columnAndTypes;

    }

    @Override
    public Object readData() {

        String readDataSql = createReadDataSql();
        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;

        Object res = Lists.newArrayList();
        try {
            connection = baseConf.getConnection();
            Preconditions.checkNotNull(connection, String.format("数据库%s的连接为null", baseConf.getDataBaseType()));

            statement = connection.createStatement();
            resultSet = statement.executeQuery(readDataSql);

            List<String> columns = baseConf.getColumns();
            List<Map<String, Object>> records = Lists.newArrayList();
            Map<String, Object> record;
            while (resultSet.next()){
                record = Maps.newHashMap();
                for(String column : columns){
                    record.put(column, resultSet.getObject(column));
                }
                records.add(record);
            }

            res = dataConverter.convet(records);

        } catch (SQLException e) {
            logger.error(String.format("查询数据库%s失败，sql = %s", baseConf.getDataBaseType(), readDataSql), e);
        }finally {
            logger.info(String.format("查询数据库%s成功，sql = %s", baseConf.getDataBaseType(), readDataSql));
            DataBases.close(resultSet);
            DataBases.close(statement);
        }

        return res;
    }

    protected String createReadDataSql(){
        StringBuilder sql = new StringBuilder("select ");
        sql.append(Joiner.on(",").skipNulls().join(baseConf.getColumns())).append(" from ");
        sql.append(baseConf.getTableName());

        String conditions = baseConf.getConditions();
        if(conditions != null){
            sql.append(" where ").append(conditions);
        }
        logger.info(String.format("生成查询语句成功：\n\t %s", sql));
        return sql.toString();
    }

}
