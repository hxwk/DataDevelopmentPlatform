package com.dfssi.dataplatform.ide.service.bi;

import com.dfssi.common.databases.DBCommon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Objects;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/12 8:33
 */
public class ConnectEntity {
    private final Logger logger = LogManager.getLogger(ConnectEntity.class);

    private String url;
    private String driver;
    private String user;
    private String password;
    private Connection connection;

    public ConnectEntity(String url, String driver, String user, String password) {
        this.url = url;
        this.driver = driver;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public synchronized Connection getConnection(){
        try {
            if(connection == null || !connection.isValid(5)){
                connection = DBCommon.getConn(url, driver, user, password);
            }
        } catch (Exception e) {
            logger.error(String.format("创建数据库连接失败：\n\t %s", toString()), e);
        }
        return connection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectEntity that = (ConnectEntity) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(driver, that.driver) &&
                Objects.equals(user, that.user) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, driver, user, password);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConnectEntity{");
        sb.append("url='").append(url).append('\'');
        sb.append(", driver='").append(driver).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
