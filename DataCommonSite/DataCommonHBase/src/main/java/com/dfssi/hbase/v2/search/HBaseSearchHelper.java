package com.dfssi.hbase.v2.search;

import com.dfssi.hbase.extractor.RowExtractor;
import com.dfssi.hbase.v2.exception.HBaseInitException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Description:
 *
 *      hbase的查询相关的操作
 *
 * @author LiXiaoCong
 * @version 1.0
 * @date 2016/10/11 14:34
 */
public class HBaseSearchHelper implements Serializable {

    private static HBaseSearcher searcher;

    static { getSearcher(); }

    private HBaseSearchHelper(){}

    public static <T> Set<T> scanForSet(String tableName, Scan scan, RowExtractor<T> extractor) throws IOException {
        Set<T> res = Sets.newHashSet();
        searcher.scan(tableName, scan, extractor, res);
        return res;
    }

    public static <T> List<T> scanForList(String tableName, Scan scan, RowExtractor<T> extractor) throws IOException {
        List<T> res = Lists.newArrayList();
        searcher.scan(tableName, scan, extractor, res);
        return res;
    }

    public static <T> Set<T> scanForSet(String tableName, Collection<Scan> scans, RowExtractor<T> extractor) throws Exception {
        Set<T> res = Collections.synchronizedSet(new HashSet<T>());
        searcher.scan(tableName, scans, extractor, res);
        return res;
    }

    public static <T> List<T> scanForList(String tableName, Collection<Scan> scans, RowExtractor<T> extractor) throws Exception {
        List<T> res = Collections.synchronizedList(new ArrayList<T>());
        searcher.scan(tableName, scans, extractor, res);
        return res;
    }

    public static <T> Set<T> getForSet(String tableName, List<Get> gets, RowExtractor<T> extractor) throws IOException {
        Set<T> res = Sets.newHashSet();
        searcher.get(tableName, gets, extractor, res);
        return res;
    }

    public static <T> List<T> getForList(String tableName, List<Get> gets, RowExtractor<T> extractor) throws IOException {
        List<T> res = Lists.newArrayList();
        searcher.get(tableName, gets, extractor, res);
        return res;
    }

    public static <T> Set<T> getForSet(String tableName, List<Get> gets,
                                  int perThreadGetNum, RowExtractor<T> extractor) throws Exception {
        Set<T> res = Collections.synchronizedSet(new HashSet<T>());
        searcher.get(tableName, gets, perThreadGetNum, extractor, res);
        return res;
    }

    public static <T> List<T> getForList(String tableName, List<Get> gets,
                                    int perThreadGetNum, RowExtractor<T> extractor) throws Exception {
        List<T> res = Collections.synchronizedList(new ArrayList<T>());
        searcher.get(tableName, gets, perThreadGetNum, extractor, res);
        return res;
    }

    public static HBaseSearcher getSearcher(){

        if(searcher == null) {
            try {
                searcher = new HBaseSearcher();
            } catch (Exception e) {
                HBaseInitException initException = new HBaseInitException();
                initException.addSuppressed(e);
                throw initException;
            }
            Preconditions.checkNotNull(searcher, "HBaseSearcher 初始化失败。");
        }

        return searcher;
    }
}
