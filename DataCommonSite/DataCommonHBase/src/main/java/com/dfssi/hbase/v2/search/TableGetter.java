package com.dfssi.hbase.v2.search;

import com.dfssi.hbase.extractor.RowExtractor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Get;

import java.util.*;

/**
  * Description:
  *  单表表同时查询的参数实体类。
 */
public class TableGetter<T> extends TableSearcher<T> {

	private List<Get> gets = Lists.newArrayList();

	public TableGetter(String tableName, Get get,
                       RowExtractor<T> extractor) {

		super(tableName, extractor);

		Preconditions.checkNotNull(get,  String.format("查询表 %s get 不能为空。", tableName));
		this.gets.add(get);
	}

	public TableGetter(String tableName, Collection<Get> gets,
                       RowExtractor<T> extractor) {

		super(tableName, extractor);

		Preconditions.checkArgument((gets != null && !gets.isEmpty()),  String.format("查询表 %s 的 gets 不能为空。", tableName));
		this.gets.addAll(gets);
	}

	@Override
	public ResultEntry<T> call() throws Exception {

		ResultEntry<T> tEntry;
		try {
			Collection<T> emptySyncResultContainer;
			if (useSet4Result) {
				emptySyncResultContainer = Collections.synchronizedSet(new HashSet<T>());
			} else {
				emptySyncResultContainer = Collections.synchronizedList(new ArrayList<T>());
			}

			int rowCount;
			if(skipNull) {
				rowCount = searcher.getAndSkipNull(tableName, gets, extractor, emptySyncResultContainer);
			}else {
				rowCount = searcher.get(tableName, gets, extractor, emptySyncResultContainer);
			}

			tEntry = new ResultEntry(emptySyncResultContainer, rowCount);

		}finally {
			if(cdl != null)cdl.countDown();
		}

		return tEntry;
	}

	public void addGet(Get get){
		this.gets.add(get);
	}

}
