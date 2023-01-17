package com.dfssi.dataplatform.analysis.utils;

import com.dfssi.common.databases.DBType;
import com.google.common.collect.Maps;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 *    数据库连接管理
 * @author LiXiaoCong
 * @version 2018/1/24 19:20
 */
public class DataBaseConnectionManager implements Serializable {
    private transient Logger logger;
    private Map<String, DataBaseConnection> connectionCacheMap;

    public DataBaseConnectionManager(){
        this.logger = LoggerFactory.getLogger(DataBaseConnectionManager.class);
        this.connectionCacheMap = Maps.newConcurrentMap();
    }


    public void addConnection(String key,
                              DBType type,
                              String database,
                              String host,
                              int port,
                              String user,
                              String pwd){
        DataBaseConnection connection = new DataBaseConnection(type, database, host, port, user, pwd);
        addConnection(key, connection);
    }

    public void addConnection(String key, DataBaseConnection connection){
        this.connectionCacheMap.put(key, connection);
    }

    public DataBaseConnection getConnection(String key){
        return this.connectionCacheMap.get(key);
    }

    public void removeConnection(String key){
        closeConnection(key);
        this.connectionCacheMap.remove(key);
    }

    public void closeConnection(String key){
        DataBaseConnection connection = this.connectionCacheMap.get(key);
        if(connection != null){
            //DBCommon.close(connection.connection);
            //connection.connection = null;
            connection.cpds.close();
        }
    }


    public static class DataBaseConnection implements Serializable{
        private DBType type;
        private String database;
        private String host;
        private int port;
        private String user;
        private String pwd;

        private String url;
        private String driver;

        private ComboPooledDataSource cpds;

        private transient Logger logger;
        //private transient volatile Connection connection;

        public DataBaseConnection(DBType type,
                                  String database,
                                  String host,
                                  int port,
                                  String user,
                                  String pwd) {

            this(type,
                    type.getUrl(host, port, database),
                    type.getDriver(),
                    user,
                    pwd);

            this.database = database;
            this.host = host;
            this.port = port;
        }

        public DataBaseConnection(DBType type,
                                  String url,
                                  String driver,
                                  String user,
                                  String pwd){
            this.type = type;
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.pwd = pwd;

            this.cpds = new ComboPooledDataSource(true);
            try {
                cpds.setJdbcUrl(url);
                cpds.setDriverClass(driver);
                cpds.setUser(user);
                cpds.setPassword(pwd);
                cpds.setMaxPoolSize(18);
                cpds.setMinPoolSize(3);
                cpds.setAcquireIncrement(3);
                //最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0
                cpds.setMaxIdleTime(600);
                //每120秒检查所有连接池中的空闲连接。Default: 0
                cpds.setIdleConnectionTestPeriod(120);
                cpds.setMaxStatements(180);
            } catch(Exception e) {
                getLogger().error(null, e);
            }

            this.logger = LoggerFactory.getLogger(DataBaseConnection.class);
        }

        public String getUrl() {
            return url;
        }

        public String getDriver() {
            return driver;
        }

        public Connection getConnection(){
            Connection connection = null;
            try {
                /*
                if(this.connection == null || !this.connection.isValid(5)){
                    synchronized (DataBaseConnection.class){
                        if(this.connection == null || !this.connection.isValid(5)) {
                            DBCommon.close(this.connection);
                            this.connection = DBCommon.getConn(url, driver, user, pwd);
                        }
                    }
                }*/
                connection = cpds.getConnection();
            } catch (Exception e) {
                getLogger().error(String.format("创建数据库连接失败：\n\t %s", toString()), e);
            }
            return connection;
        }

        public DBType getType() {
            return type;
        }

        public String getDatabase() {
            return database;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getUser() {
            return user;
        }

        public String getPwd() {
            return pwd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataBaseConnection that = (DataBaseConnection) o;
            return type == that.type &&
                    Objects.equals(database, that.database) &&
                    Objects.equals(host, that.host) &&
                    Objects.equals(port, that.port) &&
                    Objects.equals(user, that.user) &&
                    Objects.equals(pwd, that.pwd);
        }

        @Override
        public int hashCode() {

            return Objects.hash(type, database, host, port, user, pwd);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DataBaseConnection{");
            sb.append("type=").append(type);
            sb.append(", database='").append(database).append('\'');
            sb.append(", url='").append(url).append('\'');
            sb.append(", driver='").append(driver).append('\'');
            sb.append(", user='").append(user).append('\'');
            sb.append(", pwd='").append(pwd).append('\'');
            sb.append('}');
            return sb.toString();
        }

        private Logger getLogger(){
            if(logger == null){
                this.logger = LoggerFactory.getLogger(DataBaseConnection.class);
            }
            return logger;
        }
    }

}
