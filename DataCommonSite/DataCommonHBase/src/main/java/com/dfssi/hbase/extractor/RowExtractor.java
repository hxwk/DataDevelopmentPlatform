package com.dfssi.hbase.extractor;

import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;


public interface RowExtractor<T>  {

	
	/**
	  * description:
	  * @param result  单行 result 
	  * @param rowNum  
	  * @return
	  * @throws Exception
	  * T
	  * 2014-1-30 上午10:54:27
	  * by Lixc
	 */
	T extractRowData(Result result, int rowNum) throws IOException;
	
}