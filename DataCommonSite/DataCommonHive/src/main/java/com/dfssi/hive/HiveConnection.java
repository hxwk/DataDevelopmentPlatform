package com.dfssi.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Description:
 *   hive连接
 * @author LiXiaoCong
 * @version 2017/4/11 16:25
 */
public class HiveConnection {

    private String hostPorts;
    private String database;
    private String user;
    private String password;
    private String driver;
    private boolean autoCreate;

    private volatile Connection connection;

    public HiveConnection(String hostPorts,
                          String database,
                          String user,
                          String password,
                          String driver,
                          boolean autoCreate) {
        this.hostPorts = hostPorts;
        this.database = database;
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.autoCreate = autoCreate;
    }

    void init() throws Exception {
        getConnection();
    }

    public Connection getConnection() throws Exception {
        if(connection == null || connection.isClosed()){
            synchronized (HiveConnection.class){
                if(connection == null || connection.isClosed()){
                    connection = getConnection(true);
                }
            }
        }
        return connection;
    }

    public Connection getConnection(boolean forceNew) throws Exception {
        if(forceNew) {
            return newConnection();
        }else {
            return getConnection();
        }
    }

    public void close(Connection connection){

            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void close(){
        try {
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection newConnection() throws Exception {
        Class.forName(driver);
        String jdbcurl = String.format("jdbc:hive2://%s/%s", hostPorts, database);
        Connection connection = DriverManager.getConnection(jdbcurl, user, password == null ? "" : password);
        return connection;
    }

    public String getHostPorts() {
        return hostPorts;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    @Override
    public String toString() {
        return "HiveConnection{" +
                "hostPorts='" + hostPorts + '\'' +
                ", database='" + database + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", driver='" + driver + '\'' +
                ", autoCreate=" + autoCreate +
                ", connection=" + connection +
                '}';
    }
}