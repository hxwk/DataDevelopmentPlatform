package com.dfssi.hbase.v2.search;

import com.dfssi.hbase.extractor.RowExtractor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Scan;

import java.util.*;

/**
  * Description:
  *  单表表同时查询的参数实体类。
 */
public class TableScaner<T> extends TableSearcher<T> {

	private List<Scan> scans = Lists.newArrayList();

	public TableScaner(String tableName, Scan scan,
                       RowExtractor<T> extractor) {

		super(tableName, extractor);
		Preconditions.checkNotNull(scan,  String.format("查询表 %s 的scan 不能为空。", tableName));
		this.scans.add(scan);

	}

	public TableScaner(String tableName, Collection<Scan> scans,
                       RowExtractor<T> extractor) {

		super(tableName, extractor);
		Preconditions.checkArgument((scans != null && !scans.isEmpty()),  String.format("查询表 %s 的 scan 不能为空。", tableName));

		this.scans.addAll(scans);

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
				rowCount = searcher.scanAndSkipNull(tableName, scans, extractor, emptySyncResultContainer);
			}else {
				rowCount = searcher.scan(tableName, scans, extractor, emptySyncResultContainer);
			}

			tEntry = new ResultEntry(emptySyncResultContainer, rowCount);

		}finally {
			if(cdl != null)cdl.countDown();
		}

		return tEntry;
	}

	public void addScan(Scan scan){
		this.scans.add(scan);
	}

}
