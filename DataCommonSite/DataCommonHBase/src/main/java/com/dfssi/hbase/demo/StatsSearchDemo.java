package com.dfssi.hbase.demo;

import com.dfssi.hbase.v2.search.HBaseSearchHelper;
import com.dfssi.hbase.v2.search.HBaseSearcher;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/8/9 11:28
 */
public class StatsSearchDemo {

    public enum CountType{COUNT, UNIQUEDAYCOUNT, UNIQUECOUNT}

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private HBaseSearcher searcher = HBaseSearchHelper.getSearcher();

    public void searchStationMac(final String[] days, String field, CountType countType) throws InterruptedException {

        long t = System.currentTimeMillis();

        Map<String, Scan> scans = Maps.newHashMap();
        Scan scan;
        for(String day : days) {
            scan = new Scan();
            scan.setCaching(1000);
            scan.setRowPrefixFilter(Bytes.toBytes(String.format("%s,%s", StringUtils.reverse(day), field)));
            switch (countType){
                case UNIQUEDAYCOUNT:
                    scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("UNIQUEDAYCOUNT"));
                    break;
                case UNIQUECOUNT:
                    scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("UNIQUECOUNT"));
                    break;
                case COUNT:
                    scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("COUNT"));
                    break;
            }

            scans.put(day, scan);
        }

        scan = new Scan();
        scan.setCaching(1000);
        scan.setRowPrefixFilter(Bytes.toBytes(field));
        switch (countType){
            case UNIQUECOUNT:
            scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("UNIQUECOUNT"));
            break;
            case UNIQUEDAYCOUNT:
                //scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("UNIQUEDAYCOUNT"));
                //break;
            case COUNT:
                scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("COUNT"));
                break;
        }
        scans.put("total", scan);

        final Map<String, Long> countRes = Maps.newHashMap();

       final CountDownLatch countDownLatch = new CountDownLatch(scans.size());
        Set<Map.Entry<String, Scan>> entries = scans.entrySet();
        for(final Map.Entry<String, Scan> entry : entries){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Long rowCount = 0L;
                    try {
                        rowCount = searcher.sumForLong("TEST:ALL_COUNT", entry.getValue());
                    } catch (Throwable throwable) {
                        System.out.println(String.format("%s 的 STATIONMAC 今日流量查询失败。\n %s", entry.getKey(), throwable));
                    }finally {
                        countDownLatch.countDown();
                    }
                    countRes.put(entry.getKey(), rowCount);
                }
            });
        }

        countDownLatch.await();

        System.out.println("耗时：" + (System.currentTimeMillis() - t));
        System.out.println(field + " -> " + countRes);


    }

    public static void main(String[] args) throws InterruptedException {
        StatsSearchDemo statsSearchDemo = new StatsSearchDemo();
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "STATIONMAC", CountType.COUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "QQNUM", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "WECHAT", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "SJHM", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "USERNAME", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "IMSI", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "IMEI", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "AUTH_ACCOUNT", CountType.UNIQUECOUNT);
        statsSearchDemo.searchStationMac(new String[]{"20170713","20170712","20170711", "20170710", "20170709"}, "CLIENTMAC", CountType.UNIQUECOUNT);

        statsSearchDemo.executorService.shutdown();
    }

}
