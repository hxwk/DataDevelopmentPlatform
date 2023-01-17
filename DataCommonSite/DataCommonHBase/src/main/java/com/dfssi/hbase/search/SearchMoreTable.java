package com.dfssi.hbase.search;

import com.dfssi.hbase.extractor.RowExtractor;
import org.apache.hadoop.hbase.client.Scan;

/**
  * Description:
  * 		多表同时查询的参数实体类。
  * 
  * History：
  * =============================================================
  * Date                      Version        Memo
  * 2015-10-7上午10:21:39            1.0            Created by LiXiaoCong
  * 
  * =============================================================
  * 
  * Copyright 2015, 武汉白虹软件科技有限公司 。
 */
public class SearchMoreTable<T> {
	
	private String tableName;
	private Scan scan;
	private RowExtractor<T> extractor;
	
	public SearchMoreTable() {
		super();
	}
	
	public SearchMoreTable(String tableName, Scan scan,
			RowExtractor<T> extractor) {
		super();
		this.tableName = tableName;
		this.scan = scan;
		this.extractor = extractor;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Scan getScan() {
		return scan;
	}
	public void setScan(Scan scan) {
		this.scan = scan;
	}
	public RowExtractor<T> getExtractor() {
		return extractor;
	}
	public void setExtractor(RowExtractor<T> extractor) {
		this.extractor = extractor;
	}
	
	

}
