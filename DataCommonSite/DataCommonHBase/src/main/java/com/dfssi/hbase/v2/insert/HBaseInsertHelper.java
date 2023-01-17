/**
  * Copyright (c) 2016, jechedo All Rights Reserved.
  *
 */
package com.dfssi.hbase.v2.insert;

import com.dfssi.hbase.v2.HContext;
import com.dfssi.hbase.v2.HTableHelper;
import com.dfssi.hbase.v2.exception.HBaseInsertException;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * 
 *   hbase数据插入
 *                  
 * @author  LiXiaoCong
 * @version 1.0
 * @date    2016-9-6 下午3:46:04  
 */
public class HBaseInsertHelper implements Serializable{

	private HBaseInsertHelper(){}

	public static void put(String tableName, List<Put> puts) throws Exception {
		if(!puts.isEmpty()){
			Table table = HContext.get().newTable(tableName);
			try {
				table.put(puts);
			}finally {
				HTableHelper.close(table);
			}
		}
 	}

	public static void put(String tableName, Put put) throws Exception {
		put(tableName, Lists.newArrayList(put));
	} 
	
	public static void checkAndPut(String tableName, byte[] row, byte[] family, byte[] qualifier,
			byte[] value, Put put) throws Exception {
		 checkAndPut(tableName, row, family, qualifier, null, value, put);
	}
	
	public static void checkAndPut(String tableName, byte[] row, byte[] family, byte[] qualifier,
			 CompareOp compareOp, byte[] value, Put put) throws Exception {

		if(!put.isEmpty() ){
			Table table = HContext.get().newTable(tableName);
			try {
				if(compareOp == null){
					table.checkAndPut(row, family, qualifier, value, put);
				}else{
					table.checkAndPut(row, family, qualifier, compareOp, value, put);
				}
			}finally{
				HTableHelper.close(table);
			}
		}
	}

	public static void put(final String tableName, List<Put> puts, int perThreadPutSize) throws Exception {
		
		int size = puts.size();
		if(size > perThreadPutSize){

			int threadNum = (int)Math.ceil(size / (double)perThreadPutSize);
			ExecutorService executorService = Executors.newFixedThreadPool(6);

			final CountDownLatch  cdl = new CountDownLatch(threadNum);
			final List<Exception>  es = Collections.synchronizedList(new ArrayList<Exception>());

			try {
				for(int i = 0; i < threadNum; i++){
					final List<Put> tmp;
					if(i == (threadNum - 1)){
						tmp = puts.subList(perThreadPutSize*i, size);
					}else{
						tmp = puts.subList(perThreadPutSize*i, perThreadPutSize*(i + 1));
					}
					executorService.execute(new Runnable() {
						@Override
						public void run() {
							try {
								if(es.isEmpty()) put(tableName, tmp);
							} catch (Exception e) {
								es.add(e);
							}finally {
								cdl.countDown();
							}
						}
					});
				}
				cdl.await();
			}finally {
				executorService.shutdown();
			}

			if(es.size() > 0){
				HBaseInsertException insertException = new HBaseInsertException(String.format("put数据到表%s失败。"));
				insertException.addSuppresseds(es);
				throw insertException;
			}
		}else {
			put(tableName, puts);
		}
	}
}
