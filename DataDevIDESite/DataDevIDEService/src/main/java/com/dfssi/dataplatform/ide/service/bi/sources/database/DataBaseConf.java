package com.dfssi.dataplatform.ide.service.bi.sources.database;

import com.dfssi.common.databases.DBType;
import com.dfssi.resources.ConfigDetail;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 14:43
 */
public abstract class DataBaseConf extends ConfigDetail{
    private final Logger logger = LogManager.getLogger(DataBaseConf.class);

    protected String server;
    protected String user;
    protected String password;
    protected String database;
    protected String tableName;

    private List<String> columns;

    private String conditions;

    private Connection connection;

    public DataBaseConf(Map<String, String> config){
        super(config);
        init();
        this.connection = createConnnection();
    }

    protected void init(){

        this.server = getConfigItemValue("database.server");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(server), "database.server不能为空。");

        this.user = getConfigItemValue("database.user");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user), "database.user 不能为空。");

        this.password = getConfigItemValue("database.password");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "database.password 不能为空。");

        this.database = getConfigItemValue("database.database");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(database), "database.database 不能为空。");

        this.tableName = getConfigItemValue("database.tableName");
        //Preconditions.checkArgument(!Strings.isNullOrEmpty(tableName), "database.tableName 不能为空。");

        this.columns = getConfigItemList("database.columns");
        //Preconditions.checkArgument(!columns.isEmpty(), "表的抽取字段不能为空。");

        this.conditions = getConfigItemValue("database.conditions");

    }

    public abstract DBType getDataBaseType();

    public abstract Connection createConnnection();

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getConditions() {
        return conditions;
    }

    public Connection getConnection() throws SQLException {
        if(connection == null || !connection.isValid(5)){
            connection = createConnnection();
        }
        return connection;
    }

}
