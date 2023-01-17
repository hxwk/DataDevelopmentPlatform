package com.dfssi.hbase.v2.search;

import com.dfssi.hbase.extractor.RowExtractor;
import com.google.common.base.Preconditions;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/5 15:30
 */
public abstract class TableSearcher<T> implements Callable<ResultEntry<T>> {

    protected HBaseSearcher searcher;
    protected CountDownLatch cdl;

    protected String tableName;
    protected RowExtractor<T> extractor;

    protected boolean skipNull = false;
    protected boolean useSet4Result = false;

    public TableSearcher(String tableName, RowExtractor<T> extractor) {

        Preconditions.checkNotNull(tableName, "查询数据的tableName不能为空。");
        Preconditions.checkNotNull(extractor, String.format("查询表 %s 的extractor 不能为空。", tableName));

        this.tableName = tableName;
        this.extractor = extractor;
    }

    void setSearcher(HBaseSearcher searcher){
        this.searcher = searcher;
    }

    void setCountDownLatch(CountDownLatch cdl){
        this.cdl = cdl;
    }

    public void setSkipNull(boolean skipNull) {
        this.skipNull = skipNull;
    }

    public void setUseSet4Result(boolean useSet4Result) {
        this.useSet4Result = useSet4Result;
    }

    public String getTableName() {
        return tableName;
    }

    public RowExtractor<T> getExtractor() {
        return extractor;
    }

}
