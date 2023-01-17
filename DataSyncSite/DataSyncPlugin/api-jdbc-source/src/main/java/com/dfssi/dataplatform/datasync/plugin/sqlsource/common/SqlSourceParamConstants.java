package com.dfssi.dataplatform.datasync.plugin.sqlsource.common;

/**
 * A Source to read data from a SQL database. This source ask for new data in a table each configured time.<p>
 * @author Marcelo Valle JianKang
 * @modify time 20171211
 * */
public class SqlSourceParamConstants {
    public static final String STATUS_FILE_NAME = "sqlSource.status";

    public static final String LINUX_STATUS_FILE_PATH = "/var/lib/flume";

    public static final String WINDOWS_STATUS_FILE_PATH= "D:\\test\\data";

    public static final String DELIMITER_ENTRY =",";

    public static final String ALL_COLUMNS_SELECT = "*";

    public static final String START_FROM = "0";

    public static final String RUN_QUERY_DELAY = "10000";

    public static final String BATCH_SIZE ="100";

    public static final String MAX_ROWS ="10000";

    public static final String READ_ONLY ="false";

    public static final String ORCL_CONN_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

    public static final String MYSQL_CONN_DRIVER_CLASS = "com.mysql.jdbc.Driver";

    public static final String C3P0_MINSIZE = "1";

    public static final String C3P0_MAXSIZE = "10";

    public static final String MYSQL_DIALECT = "org.hibernate.dialect.MySQL5Dialect";

    public static final String ORCL_DIALECT = "com.dfssi.dataplatform.datasync.plugin.sqlsource.source.Oracle10gDialectNew";

    public static final String AUTO_COMMIT ="TRUE";

    public static final String CONN_PROVIDER_CLASS = "org.hibernate.connection.C3P0ConnectionProvider";

}
