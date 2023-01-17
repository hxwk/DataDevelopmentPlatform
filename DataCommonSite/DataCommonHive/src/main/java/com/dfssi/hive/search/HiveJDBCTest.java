package com.dfssi.hive.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


/**
 * 
 * @author Administrator
 * 
 *${HIVE_HOME}/bin/hiveserver2  HiveServer2
 *   $ hive --service hiveserver2
 *
 */
public class HiveJDBCTest {
	private static final Logger LOGGER = LogManager.getLogger(HiveJDBCTest.class);
	
	//private static final Logger LOG = Logger.getLogger(HiveJDBCTest.class);
	private static Connection conn ;
	
	public void testHiveSearch() throws SQLException{
		
		try {
			//Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			//conn = DriverManager.getConnection("jdbc:hive://172.16.11.5:10000/default","hive","");
			conn = DriverManager.getConnection("jdbc:hive2://172.16.1.230:10000/default","hive","");
			//conn = DriverManager.getConnection("jdbc:hive2://172.16.11.5:21050/;auth=noSasl","hive","");
			
		} catch (ClassNotFoundException e) {
			System.err.println("hive加载jdbc驱动HiveDriver失败");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String hql = "show tables";
		
		PreparedStatement prepareStatement = conn.prepareStatement(hql);
		prepareStatement.execute();
		
		ResultSet resultSet = prepareStatement.getResultSet();
		
		while(resultSet.next()){
			LOGGER.warn(resultSet.getString(1));
		}
		
	}
	
	public void testImpalaSearch() throws SQLException{
		
		try {
			//Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			//conn = DriverManager.getConnection("jdbc:hive://172.16.11.5:10000/default","hive","");
			//conn = DriverManager.getConnection("jdbc:hive2://172.16.11.5:10000/default","hive","");
			conn = DriverManager.getConnection("jdbc:hive2://172.16.11.5:21050/;auth=noSasl","hive","");
			
		} catch (ClassNotFoundException e) {
			System.err.println("hive加载jdbc驱动HiveDriver失败");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//String hql = "select * from t_iminfo where clientmac = 'B0-5B-67-77-40-79' or sendaccount = '1157748532'";
		//String hql = "select count(*) from t_iminfo";
		String hql = "select * from t_iminfo where key = '509_2747903604_B0-5B-67-77-40-79_0_0_1439699136000' ";
		
		PreparedStatement prepareStatement = conn.prepareStatement(hql);
		prepareStatement.execute();
		
		ResultSet resultSet = prepareStatement.getResultSet();
		System.out.println(resultSet.getFetchSize());
		
		//while(resultSet.next()){
			//System.out.println(resultSet.getString("key") );
		//}
		
	}
	public static void main(String[] args) throws SQLException {

		new HiveJDBCTest().testHiveSearch();
	}

}
