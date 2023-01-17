package com.dfssi.dataplatform.analysis.task.resource;

import org.apache.commons.lang.StringUtils;

/**
 * Description:根据DBConnectEntity获取数据
 * @author undate by pengwk
 * @version 2018/6/22 11:21
 */
public enum DBType {

	/**
	 * mysql
	 */
	MYSQL(){
		// default port : 3306
		@Override
		public String getUrl(String host, String port, String dbName ){
			return String.format("jdbc:mysql://%s:%s/%s"
							+ "?characterEncoding=utf8&useSSL=true&autoReconnect=true&zeroDateTimeBehavior=convertToNull",
							   getHostWithDefault(host), getPortWithDefault(port),dbName);
		}
		@Override
		public String getDriver(){
			return "com.mysql.jdbc.Driver";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "172.16.1.241";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "172.16.1.241" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "3306";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "3306" : s;
			}
		}

	},SQLSERVER(){
		// default port : 1433
		@Override
		public String getUrl(String host, String port, String dbName ){
			return String.format("jdbc:jtds:sqlserver://%s:%s/%s", getHostWithDefault(host), getPortWithDefault(port), dbName);
		}
		@Override
		public String getDriver(){
			return "net.sourceforge.jtds.jdbc.Driver";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "1433";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "1433" : s;
			}
		}

	},ORACLE(){
		// default port : 1521
		@Override
		public String getUrl(String host, String port, String dbName ){
			return String.format("jdbc:oracle:thin:@%s:%s:%s", getHostWithDefault(host), getPortWithDefault(port), dbName);
		}
		@Override
		public String getDriver(){
			return "oracle.jdbc.driver.OracleDriver";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "172.16.1.243";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "172.16.1.243" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "1521";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "1521" : s;
			}
		}

	},DB2(){
		// default port : 5000
		@Override
		public String getUrl(String host, String port, String dbName ){
			return String.format("jdbc:db2://%s:%s/%s", getHostWithDefault(host), getPortWithDefault(port), dbName);
		}
		@Override
		public String getDriver(){
			return "com.ibm.db2.jdbc.app.DB2Driver ";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "5000";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "5000" : s;
			}
		}

	},SYBASE(){
		// default port : 5007
		@Override
		public String getUrl(String host, String port, String dbName ){
			return String.format("jdbc:sybase:Tds:%s:%s/%s", getHostWithDefault(host), getPortWithDefault(port), dbName);
		}
		@Override
		public String getDriver(){
			return "com.sybase.jdbc.SybDriver";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "5007";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "5007" : s;
			}
		}

	},POSTGRESQL(){
		// default port : 5432
		@Override
		public String getUrl(String host, String port, String dbName ){
			return String.format("jdbc:postgresql://%s:%s/%s", getHostWithDefault(host), getPortWithDefault(port), dbName);
		}
		@Override
		public String getDriver(){
			return "org.postgresql.Driver";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "172.16.1.221";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "172.16.1.221" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "5432";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "5432" : s;
			}
		}

	},HIVE(){
		// jdbc:hive2://172.16.1.210:10000/
		@Override
		public String getUrl(String host, String port, String dbName){
			return String.format("jdbc:hive2://%s:%s/%s", getHostWithDefault(host), getPortWithDefault(port), dbName);
		}
		@Override
		public String getDriver(){
			return "org.apache.hive.jdbc.HiveDriver";
		}
		@Override
		public String getHostWithDefault(String host) {
			if(host == null){
				return "172.16.1.210";
			}else{
				String s = String.valueOf(host);
				return StringUtils.isBlank(s) ? "172.16.1.210" : s;
			}
		}
		@Override
		public String getPortWithDefault(String port) {
			if(port == null){
				return "10000";
			}else{
				String s = String.valueOf(port);
				return StringUtils.isBlank(s) ? "10000" : s;
			}
		}

	};

	public static DBType newDBType(String dbTypeName){

		if("mysql".equalsIgnoreCase(dbTypeName)){
			return MYSQL;
		}

		if("sqlserver".equalsIgnoreCase(dbTypeName)){
			return SQLSERVER;
		}

		if("oracle".equalsIgnoreCase(dbTypeName)){
			return ORACLE;
		}

		if("db2".equalsIgnoreCase(dbTypeName)){
			return DB2;
		}

		if("sybase".equalsIgnoreCase(dbTypeName)){
			return SYBASE;
		}

		if("postgresql".equalsIgnoreCase(dbTypeName)){
			return POSTGRESQL;
		}

		if("hive".equalsIgnoreCase(dbTypeName)){
			return HIVE;
		}
		return null;
	}

	public abstract String getUrl(String host, String port, String dbName);
	public abstract String getDriver();
	public abstract String getHostWithDefault(String host);
	public abstract String getPortWithDefault(String port);

	String getPort(Object port, String defualtPort){
		if(port == null){
			return defualtPort;
		}else{
			String s = String.valueOf(port);
			return StringUtils.isBlank(s) ? defualtPort : s;
		}
	}
}
