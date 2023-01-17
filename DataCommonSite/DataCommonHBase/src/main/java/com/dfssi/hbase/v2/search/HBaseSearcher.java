/**
  * Copyright (c) 2016, jechedo All Rights Reserved.
  *
 */
package com.dfssi.hbase.v2.search;

import com.dfssi.hbase.extractor.RowExtractor;
import com.dfssi.hbase.v2.HContext;
import com.dfssi.hbase.v2.HTableHelper;
import com.dfssi.hbase.v2.exception.ScanException;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.DoubleColumnInterpreter;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Description:
 * 
 * 	hbase 搜索执行类， 主要实现通用的是hbase的 scan 和 get 等方法
 *                  
 * @author  LiXiaoCong
 * @version 1.0
 * @date    2016-9-6 上午9:16:04  
 */
public class HBaseSearcher implements Serializable {

	private static final long serialVersionUID = 1454451782448728557L;
	
	private int threadPoolSize;
	private HContext hContext;
	
	private volatile transient ExecutorService threadPool;
	
	public HBaseSearcher() throws Exception {
		this(8);
	}

	public HBaseSearcher(int  threadNum) throws Exception {
		this.hContext = HContext.get();
		this.threadPoolSize = threadNum;
	}

	/**
	  * description:
	  *  根据scan扫描数据，并将结果放入传入的结果集合emptyResultContainer中 ，并返回查询到的记录数
	  * @param emptyResultContainer    用于存放查询结果集的空集合
	  * @throws IOException
	  *
	  * @return int 满足条件的记录数
	  *
	  * @date   2016-9-6 上午10:40:22
	  * @author LiXiaoCong
	 */
	public <T> int scan(String tableName, Scan scan,
			RowExtractor<T> extractor, Collection<T> emptyResultContainer) throws IOException{

		Table table = newTable(tableName);
		ResultScanner scanner = table.getScanner(scan);
		
		int n = 0;
		T row;
		Result result;
		while((result = scanner.next()) != null){
			row = extractor.extractRowData(result, n++);
			emptyResultContainer.add(row);
		}

		HTableHelper.close(scanner);
		HTableHelper.close(table);

		return n;
	}

	/**
	 * 根据scan扫描数据，并将结果放入传入的结果集合emptyResultContainer中 ，并返回查询到的记录数
	 * 返回的结果过滤了为null的结果  因此emptyResultContainer的size和返回的记录数可能存在差异
	 * @return int 满足条件的记录数
	 * @throws IOException
	 */
	public <T> int scanAndSkipNull(String tableName, Scan scan, RowExtractor<T> extractor,
						Collection<T> emptyResultContainer) throws IOException{

		Table table = newTable(tableName);
		ResultScanner scanner = table.getScanner(scan);

		int n = 0;
		T row;
		Result result;
		while((result = scanner.next()) != null){
			row = extractor.extractRowData(result, n++);
			if(row != null)emptyResultContainer.add(row);
		}

		HTableHelper.close(scanner);
		HTableHelper.close(table);

		return n;
	}

	/**
	 * @param emptySyncResultContainer      因为是多线程查询的， 所以在这里需要传入线程同步的集合
	 * @return int 满足条件的记录数
	 * @throws IOException
	 */
	public <T> int scan(final String tableName,
						 Collection<Scan> scans,
						 final RowExtractor<T> extractor,
						 final Collection<T> emptySyncResultContainer) throws Exception {
		return scan(tableName, scans, extractor, emptySyncResultContainer, false);
	}

	/**
	 * @param emptySyncResultContainer      因为是多线程查询的， 所以在这里需要传入线程同步的集合
	 * @return int 满足条件的记录数
	 * @throws IOException
	 */
	public <T> int scanAndSkipNull(final String tableName,
						 Collection<Scan> scans,
						 final RowExtractor<T> extractor,
						 final Collection<T> emptySyncResultContainer) throws Exception {
		return scan(tableName, scans, extractor, emptySyncResultContainer, true);
	}

	/**
	 * @return int 满足条件的记录数
	 * @throws Exception
	 */
	private <T>  int scan(final String tableName,
						 Collection<Scan> scans,
						 final RowExtractor<T> extractor,
						 final Collection<T> emptySyncResultContainer,
						 final boolean skipNull) throws Exception {

		int size = scans.size();
		if(size == 1){
			return scan(tableName, scans.iterator().next(), extractor, emptySyncResultContainer);
		}

		final AtomicInteger count = new AtomicInteger(0);
		final CountDownLatch  cdl = new CountDownLatch(size);
		final List<Exception> ies = Collections.synchronizedList(new LinkedList<Exception>());

		ExecutorService executorService = getOrNewThreadPool();
		for(final Scan scan : scans){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
				try{
					if(ies.size() == 0) {
						int rowCount;
						if(skipNull) {
							rowCount = scanAndSkipNull(tableName, scan, extractor, emptySyncResultContainer);
						}else {
							rowCount = scan(tableName, scan, extractor, emptySyncResultContainer);
						}
						count.addAndGet(rowCount);
					}
				}catch(IOException e){
					ies.add(e);
				}finally{
					cdl.countDown();
				}
				}
			});
		}

		cdl.await();

		if(ies.size() > 0){
			ScanException scanException = new ScanException(String.format("扫描%s失败。", tableName));
			scanException.addSuppressed(ies.get(0));
			throw scanException;
		}

		return count.get();
	}

	
	/**
	  * description:
	  *  多表联查
	 *   相同表的结果会合并
	  *  结果中key为TableScaner中的tableName
	  * @date   2016-9-6 下午3:09:13
	  * @author LiXiaoCong
	 */
	public <T> Map<String, ResultEntry<T>> scan(Collection<TableScaner<T>> scaners) throws Exception {
		Preconditions.checkArgument((scaners != null && !scaners.isEmpty()), "scaners 不能为空。");
		return search(scaners);
	}


	/**
	 * description:
	 *  多表联查
	 *  相同表的结果会合并
	 *  结果中key为TableScaner中的tableName
	 * @date   2016-9-6 下午3:09:13
	 * @author LiXiaoCong
	 */
	public <T> Map<String, ResultEntry<T>>  get(Collection<TableGetter<T>> getters) throws Exception {
		Preconditions.checkArgument((getters != null && !getters.isEmpty()), "getters 不能为空。");
		return search(getters);
	}

	/**
	 * description:
	 *  多表联查
	 *  相同表的结果会合并
	 *  结果中key为TableScaner中的tableName
	 * @date   2016-9-6 下午3:09:13
	 * @author LiXiaoCong
	 */
	public <T> Map<String, ResultEntry<T>> search(Collection<? extends TableSearcher<T>> searchers) throws Exception {

		int size = searchers.size();
		CountDownLatch cdl = new CountDownLatch(size);
		HashMultimap<String, Future<ResultEntry<T>>> futureMap = HashMultimap.create();

		ExecutorService executorService = getOrNewThreadPool();
		Future<ResultEntry<T>> future;
		for (TableSearcher<T> t : searchers) {
			t.setSearcher(this);
			t.setCountDownLatch(cdl);

			future = executorService.submit(t);
			futureMap.put(t.getTableName(), future);
		}

		cdl.await();

		Map<String, ResultEntry<T>> data =  Maps.newHashMapWithExpectedSize(size);
		ResultEntry<T> resultEntry;
		for (Map.Entry<String, Future<ResultEntry<T>>> e : futureMap.entries()){
			resultEntry = data.get(e.getKey());
			if(resultEntry == null){
				resultEntry = e.getValue().get();
				data.put(e.getKey(), resultEntry);
			}else {
				resultEntry.addResut(e.getValue().get());
			}
		}

		return data;
	}


	/**
	 * @return int 满足条件的记录数
	 * @throws Exception
	 */
	public <T> int get(String tableName,
						List<Get> gets,
						RowExtractor<T> extractor,
						Collection<T> emptyResultContainer) throws IOException {
		
		Table table = newTable(tableName);
			
		int n = 0;
		T row;
		Result[] results = table.get(gets);
		for(Result result : results){
			row = extractor.extractRowData(result, n++);
			emptyResultContainer.add(row);
		}
		HTableHelper.close(table);

		return n;
	}

	/**
	 * @return int 满足条件的记录数
	 * @throws Exception
	 */
	public <T> int getAndSkipNull(String tableName,
						List<Get> gets,
						RowExtractor<T> extractor,
						Collection<T> emptyResultContainer) throws IOException {

		Table table = newTable(tableName);

		int n = 0;
		T row;
		Result[] results = table.get(gets);
		for(Result result : results){
			row = extractor.extractRowData(result, n++);
			if(row != null)emptyResultContainer.add(row);
		}
		HTableHelper.close(table);

		return n;
	}

	public <T> T get(String tableName, Get get, RowExtractor<T> extractor) throws IOException {

		Table table = newTable(tableName);
		Result result = table.get(get);
		T row = extractor.extractRowData(result, 0);
		HTableHelper.close(table);

		return row;
	}

	/**
	 * @return int 满足条件的记录数
	 * @throws Exception
	 */
	public <T> int get(String tableName,
					   List<Get> gets,
					   int perThreadGetSize,
					   RowExtractor<T> extractor,
					   Collection<T> emptySyncResultContainer) throws Exception{

		Preconditions.checkArgument((gets != null && !gets.isEmpty()), "gets 不能为空。");
		
		int size = gets.size();
		if(size <= perThreadGetSize){
			return get(tableName, gets, extractor, emptySyncResultContainer);
		}

		int threadNum = (int)Math.ceil(size / (double)perThreadGetSize);
		CountDownLatch  cdl = new CountDownLatch(threadNum);

		ExecutorService executorService = getOrNewThreadPool();
		List<Future<ResultEntry<T>>> futures = Lists.newArrayListWithExpectedSize(threadNum);
		Future<ResultEntry<T>> future;
		List<Get> tmp;
		TableGetter<T> tTableGetter;
		for(int i = 0; i < threadNum; i++){
			if(i == (threadNum - 1)){
				tmp = gets.subList(perThreadGetSize*i, size);
			}else{
				tmp = gets.subList(perThreadGetSize*i, perThreadGetSize*(i + 1));
			}

			tTableGetter = new TableGetter<>(tableName, tmp, extractor);
			tTableGetter.setSearcher(this);
			tTableGetter.setCountDownLatch(cdl);

			future = executorService.submit(tTableGetter);
			futures.add(future);
		}

		cdl.await();

		int rowCount = 0;
		ResultEntry<T> tResultEntry;
		for(Future<ResultEntry<T>> f : futures){
			tResultEntry = f.get();
			emptySyncResultContainer.addAll(tResultEntry.getResut());
			rowCount += tResultEntry.getRowCount();
		}

		return rowCount;
	}
	

	public boolean exists(String tableName, String rowKey) throws IOException {
		return exists(tableName, Bytes.toBytes(rowKey));
	}

	public boolean exists(String tableName, byte[] rowKey) throws IOException {
		Get get = new Get(rowKey);
		get.setCheckExistenceOnly(true);
		return exists(tableName, get);
	}

	public boolean exists(String tableName, Get get) throws IOException {
		return newTable(tableName).exists(get);
	}

	public boolean[] exists(String tableName, List<Get> gets) throws IOException {
		return newTable(tableName).existsAll(gets);
	}

	public long rowCount(String tableName, Scan scan) throws Throwable {
		return hContext
				.getAggregationClient()
				.rowCount(HContext.newTableName(tableName), new LongColumnInterpreter(), scan);
	}

	public long sumForLong(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.sum(HContext.newTableName(tableName), new LongColumnInterpreter(), scan);
	}

	public double sumForDouble(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.sum(HContext.newTableName(tableName), new DoubleColumnInterpreter(), scan);
	}

	public double avgForDouble(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.avg(HContext.newTableName(tableName), new DoubleColumnInterpreter(), scan);
	}

	public double avgForLong(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.avg(HContext.newTableName(tableName), new LongColumnInterpreter(), scan);
	}

	public long maxForLong(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.max(HContext.newTableName(tableName), new LongColumnInterpreter(), scan);
	}

	public double maxForDouble(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.max(HContext.newTableName(tableName), new DoubleColumnInterpreter(), scan);
	}

	public long minForLong(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.min(HContext.newTableName(tableName), new LongColumnInterpreter(), scan);
	}

	public double minForDouble(String tableName, Scan scan) throws Throwable{
		return hContext
				.getAggregationClient()
				.min(HContext.newTableName(tableName), new DoubleColumnInterpreter(), scan);
	}


	private Table newTable(String tableName) throws IOException {
		checkArgument(StringUtils.isNotBlank(tableName), "tableName不能为null。");
		return hContext.newTable(tableName);
	}
	
	private ExecutorService getOrNewThreadPool(){
		if(threadPool == null){
			synchronized(HBaseSearcher.class){
				if(threadPool == null)
					threadPool = Executors.newFixedThreadPool(threadPoolSize);
			}
		}
		return threadPool;
	}
}
