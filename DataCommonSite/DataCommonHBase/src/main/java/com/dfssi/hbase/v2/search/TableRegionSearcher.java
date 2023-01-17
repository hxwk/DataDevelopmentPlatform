package com.dfssi.hbase.v2.search;


import com.dfssi.hbase.extractor.RowExtractor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/31 14:59
 */
public class TableRegionSearcher<T> {

    private Logger logger;

    private String tableName;
    private Scan model;
    private  ExecutorService threadPool;

    private String startRow;
    private String stopRow;

    private boolean reversed;

    private Filter filter;
    private PageFilter pageFilter;
    private int pageSize;

    private HBaseSearcher searcher;

    private RowExtractor<T> extractor;
    private List<T> result;

    private boolean hasNext = true;

    //表示 true 可以改变查询参数, false 则不可以
    private boolean open = true;

    private List<Scan> scans;
    private Map<String, byte[]> startMap;
    private AtomicInteger pageNow;

    public TableRegionSearcher(String tableName, RowExtractor<T> extractor) throws Exception {
      this(tableName, 8, extractor);
    }

    public TableRegionSearcher(String tableName, int threads, RowExtractor<T> extractor) throws Exception {
        this.searcher = new HBaseSearcher(threads);
        Preconditions.checkNotNull(searcher);

        this.threadPool = Executors.newFixedThreadPool(threads);
        this.extractor = extractor;

        this.tableName = tableName;
        this.model = new Scan();
    }


    public boolean hasNext(){
        return this.hasNext;
    }

    public Collection<T> next() throws Exception {
        if(open)this.open = false;

        if(pageFilter == null){
            searchAll();
        }else{
            searchPage();
        }
        return result;
    }

    private void searchPage() throws Exception {

       final List<T> result = Collections.synchronizedList(new LinkedList<T>());

        if(scans == null){
            /*
             * 注意：hbase的过滤器的添加要注意顺序 , 在MUST_PASS_ALL的情况下：
             *       hbase中过滤是按行过滤的，即每一行按添加顺序经过过滤器，假如在pass_all的情况下，一旦遇到不满足的情况则会跳过下面的所有过滤器，直接进入下一个cell的过滤。
             *       也就是说后面的过滤器是在前面的过滤器的基础上过滤的
             *       filterList 是在 pageFilter 的基础上过滤。
             * */
            long t = System.currentTimeMillis();
            FilterList fl = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            if(this.filter != null)fl.addFilter(filter);
            fl.addFilter(pageFilter);
            this.filter = fl;
            scans = Lists.newLinkedList(getRegionScans());
            getLogger().info(String.format("构造表%s的分区查询scan耗时%s秒，scan个数为：%s，基础Scan为：",
                    tableName, (System.currentTimeMillis() - t)/1000, scans.size(), model));
            pageNow = new AtomicInteger(0);
        }else{
            for(Scan scan : scans){
                scan.setStartRow(startMap.get(scan.getId()));
            }
        }

        long t = System.currentTimeMillis();
        int size = scans.size();
        final CountDownLatch countDownLatch = new CountDownLatch(size);
        getLogger().info(String.format("开始查询表%s中满足条件的第%s页数据。", tableName, pageNow.incrementAndGet()));
        for(int i = 0; i < size; i++){

            final Scan scan = scans.get(i);
            final SwapperExtractor swapperExtractor = new SwapperExtractor(extractor);

            threadPool.execute(

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            LinkedList<T> res = Lists.newLinkedList();
                            int n = searcher.scan(tableName, scan, swapperExtractor, res);
                            //resultSize.addAndGet(n);
                            if(n <= pageSize){
                                // 对应的分区查询完了
                                scans.remove(scan);
                                startMap.remove(scan.getId());
                            }else{
                                startMap.put(scan.getId(), swapperExtractor.getLatestRow());
                                res.removeLast();
                            }
                            result.addAll(res);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            countDownLatch.countDown();
                        }
                    }
                }));
        }

        countDownLatch.await();
        getLogger().info(String.format("查询表%s中满足条件的第%s页数据耗时%s秒, 记录数为：%s 。",
                tableName, pageNow.get(), (System.currentTimeMillis() - t)/1000, result.size()));

        this.result = result;
        this.hasNext = (this.scans.size() > 0);
    }

    private void searchAll() throws Exception {

        List<Scan> scans = getRegionScans();
        result = Collections.synchronizedList(new ArrayList<T>());
        searcher.scan(tableName, scans, extractor, result);
        this.hasNext = false;
    }

    private List<Scan> getRegionScans() throws Exception {

        if(filter != null)model.setFilter(filter);
        return ScanHelper.newScansByRegions(tableName, startRow, stopRow,  reversed, model);
    }

    public void setTimeRange(long minStamp, long maxStamp) throws IOException {
        checkOpen();
      this.model.setTimeRange(minStamp, maxStamp);
    }

    public void setTimeStamp(long timestamp) throws IOException {
        checkOpen();
        this.model.setTimeStamp(timestamp);
    }

    public void setMaxVersions() {
        checkOpen();
        this.model.setMaxVersions();
    }

    public void setMaxVersions(int maxVersions) {
        checkOpen();
        this.model.setMaxVersions(maxVersions);
    }

    public void setBatch(int batch) {
        checkOpen();
       this.model.setBatch(batch);
    }

    public void setMaxResultsPerColumnFamily(int limit) {
        checkOpen();
        this.model.setMaxResultsPerColumnFamily(limit);
    }

    public void setRowOffsetPerColumnFamily(int offset) {
        checkOpen();
       this.model.setRowOffsetPerColumnFamily(offset);
    }

    public void setCacheBlocks(boolean cacheBlocks) {
        checkOpen();
        this.model.setCacheBlocks(cacheBlocks);
    }

    public void setReversed(boolean reversed) {
        checkOpen();
       this.reversed = reversed;
    }

    public void setCaching(int caching) {
        checkOpen();
        this.model.setCaching(caching);
    }

    public void setStartRow(String startRow) {
        checkOpen();
        this.startRow = startRow;
    }

    public void setStopRow(String stopRow) {
        checkOpen();
        this.stopRow = stopRow;
    }

    public void setFilter(Filter filter){
        checkOpen();
       this.filter = filter;
    }

    public void setPageSize(int pageSize){
        checkOpen();
        this.pageFilter = new PageFilter(pageSize + 1);
        this.pageSize = pageSize;
        this.startMap = Maps.newConcurrentMap();
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private Logger getLogger() {
        if(logger == null){
            logger = Logger.getLogger("TableRegionSearcher");
        }
        return logger;
    }

    public void close(){
        if(threadPool != null)
            threadPool.shutdown();
    }

    private void checkOpen(){
        Preconditions.checkArgument(open, "TableRegionSearcher参数修改已关闭，不能修改任何参数");
    }


    private class SwapperExtractor implements RowExtractor<T>{

        private RowExtractor<T> extractor;

        private byte[] latestRow;

        private SwapperExtractor(RowExtractor<T> extractor){
            this.extractor = extractor;
        }

        @Override
        public T extractRowData(Result result, int rowNum) throws IOException {
            latestRow = result.getRow();
            return extractor.extractRowData(result, rowNum);
        }

        public byte[] getLatestRow() {
            return latestRow;
        }
    }

}
