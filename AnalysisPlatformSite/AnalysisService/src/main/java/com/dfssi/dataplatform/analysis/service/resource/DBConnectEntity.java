/**
 * 
 */
package com.dfssi.dataplatform.analysis.service.resource;

import java.io.Serializable;

/**
 * Description:根据DBConnectEntity获取数据
 * @author undate by pengwk
 * @version 2018/6/22 11:21
 */
public class DBConnectEntity implements Serializable{
	
	private DBType type;
	private String host;
	private String port;
	private String dbName;
	private String user;
	private String pwd;

	public DBConnectEntity() {
		super();
	}

	public DBConnectEntity(DBType type, String host, String port, String dbName,
						   String user, String pwd) {
		super();
		this.type = type;
		
		if(type == null ){
			throw new IllegalArgumentException("数据类类型不能识别或为空!");
		}

		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	public DBConnectEntity(String type, String host, String port, String dbName,
						   String user, String pwd) {
		
		this(DBType.newDBType(type), host, port, dbName, user, pwd);
		
	}

	public DBType getType() {
		return type;
	}

	public void setType(DBType type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("DBConnectEntity{");
		sb.append("type=").append(type);
		sb.append(", host='").append(host).append('\'');
		sb.append(", port='").append(port).append('\'');
		sb.append(", dbName='").append(dbName).append('\'');
		sb.append(", user='").append(user).append('\'');
		sb.append(", pwd='").append(pwd).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
