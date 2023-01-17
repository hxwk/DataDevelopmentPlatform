package com.dfssi.hbase.demo;

import com.dfssi.hbase.HBaseCellCommon;
import com.dfssi.hbase.extractor.RowExtractor;
import com.dfssi.hbase.v2.search.HBaseSearchHelper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/8/7 13:31
 */
public class StatsReport {

    public void searchForReport(String day, final Set<String> fields) throws IOException {

        final String total = "total";
        final ReportCount reportCount = new ReportCount();

        //统计设备在线
        Scan scan = new Scan();
        scan.setCaching(2000);
        scan.setRowPrefixFilter(Bytes.toBytes(StringUtils.reverse(day)));
        scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("COUNT"));
        scan.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("UNIQUEDAYCOUNT"));

        HBaseSearchHelper.scanForSet("JIEYANG:ALL_COUNT", scan,
                new RowExtractor<Object>() {
            @Override
            public Object extractRowData(Result result, int rowNum) throws IOException {

                String rowKey = Bytes.toString(result.getRow());
                String[] split = rowKey.split(",");

                String areaCode = split[5].split("#")[1];
                String firm = split[3].split("#")[1];

                if(rowKey.contains("AUTH_TYP")){
                    //上线认证
                    long cellLongValue = HBaseCellCommon.getCellLongValue(
                            result.getColumnLatestCell(Bytes.toBytes("CF"), Bytes.toBytes("COUNT")));

                    reportCount.addCount(total, firm, "accountOnLine", cellLongValue);
                    reportCount.addCount(areaCode, firm, "accountOnLine", cellLongValue);

                }else {

                    //设备在线
                    String device = split[2].split("#")[1];
                    reportCount.addDevice(total, firm, device);
                    reportCount.addDevice(areaCode, firm, device);

                    String field = split[1];
                    if(fields.contains(field)) {

                        //流量
                        long countValue = HBaseCellCommon.getCellLongValue(
                                result.getColumnLatestCell(Bytes.toBytes("CF"), Bytes.toBytes("COUNT")));
                        reportCount.addCount(total, firm, field, countValue);
                        reportCount.addCount(areaCode, firm, field, countValue);

                        //去重
                        Cell columnLatestCell = result.getColumnLatestCell(Bytes.toBytes("CF"), Bytes.toBytes("UNIQUEDAYCOUNT"));
                        long uniqueCountValue = (columnLatestCell == null) ? 0L : HBaseCellCommon.getCellLongValue(columnLatestCell);
                        reportCount.addCount(total, firm, "unique" + field, uniqueCountValue);
                        reportCount.addCount(areaCode, firm, "unique" + field, uniqueCountValue);
                    }
                }

                return null;
            }
        });


        reportCount.countDevice();
        System.out.println(reportCount);

    }

    public class ReportCount{

        private Map<String, AreaCount> counts;

       private ReportCount(){
            this.counts = Maps.newHashMap();
        }

        private void addCount(String areaName, String firmName, String field, long count){
            AreaCount areaCount = counts.get(areaName);
            if(areaCount == null){
                areaCount = new AreaCount(areaName);
                counts.put(areaName, areaCount);
            }
            areaCount.addCount(firmName, field, count);
        }

        private void addDevice(String areaName, String firmName, String device){

            AreaCount areaCount = counts.get(areaName);
            if(areaCount == null){
                areaCount = new AreaCount(areaName);
                counts.put(areaName, areaCount);
            }
            areaCount.addDevice(firmName, device);

        }

        private void countDevice(){
            Set<Map.Entry<String, AreaCount>> entries = counts.entrySet();
            for(Map.Entry<String, AreaCount> entry : entries){
                entry.getValue().countDevice();
            }
        }


        public Map<String, AreaCount> getCounts() {
            return counts;
        }

        @Override
        public String toString() {

            StringBuilder res = new StringBuilder();
            StringBuilder sb;
            String areaCode;
            String firmCode;

            Set<Map.Entry<String, AreaCount>> areaEntries = counts.entrySet();
            Set<Map.Entry<String, FirmCount>> firmEntries;
            Set<Map.Entry<String, AtomicLong>> countEntries;
            for(Map.Entry<String, AreaCount> areaEntry : areaEntries){
               areaCode = areaEntry.getKey();
                firmEntries = areaEntry.getValue().getCounts().entrySet();
                for (Map.Entry<String, FirmCount> firmEntry : firmEntries){
                    firmCode = firmEntry.getKey();
                    sb = new StringBuilder();
                    sb.append(areaCode).append("\t").append(firmCode);

                    countEntries = firmEntry.getValue().getCounts().entrySet();
                    for(Map.Entry<String, AtomicLong> countEntry : countEntries){
                        sb.append("\t").append(countEntry.getKey()).append(":").append(countEntry.getValue().get());
                    }
                    res.append(sb).append("\n");
                }
                res.append("************************************************\n");
            }

            return res.toString();
        }
    }

    public class AreaCount{

        private String areaCount;
        private Map<String, FirmCount> counts;

        private AreaCount(String areaCount){
            this.areaCount = areaCount;
            this.counts = Maps.newHashMap();
        }

        private void addCount(String firmName, String field, long count){
            FirmCount firmCount = counts.get(firmName);
            if(firmCount == null){
                firmCount = new FirmCount(firmName);
                counts.put(firmName, firmCount);
            }
            firmCount.addCount(field, count);
        }

        private void addDevice(String firmName, String device){

            FirmCount firmCount = counts.get(firmName);
            if(firmCount == null){
                firmCount = new FirmCount(firmName);
                counts.put(firmName, firmCount);
            }
            firmCount.addDevice(device);
        }

        private void countDevice(){
            Set<Map.Entry<String, FirmCount>> entries = counts.entrySet();
            for(Map.Entry<String, FirmCount> entry : entries){
                entry.getValue().countDevice();
            }
        }

        public String getAreaCount() {
            return areaCount;
        }

        public Map<String, FirmCount> getCounts() {
            return counts;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("AreaCount{");
            sb.append("areaCount='").append(areaCount).append('\'');
            sb.append(", counts=").append(counts);
            sb.append('}');
            return sb.toString();
        }
    }

    public class FirmCount{

        private String firmCode;
        private Map<String, AtomicLong> counts;
        private Set<String> devices;

        private FirmCount(String firmCode){
            this.firmCode = firmCode;
            this.counts = Maps.newHashMap();
            this.devices = Sets.newHashSet();
        }

        private void addCount(String field, long count){
            AtomicLong atomicLong = counts.get(field);
            if(atomicLong == null){
                atomicLong = new AtomicLong(0);
                counts.put(field, atomicLong);
            }
            atomicLong.addAndGet(count);
        }

        private void addDevice(String device){
            devices.add(device);
        }

        private void countDevice(){
            addCount("deviceOnline", devices.size());
            devices = null;
        }


        public String getFirmCode() {
            return firmCode;
        }

        public Map<String, AtomicLong> getCounts() {
            return counts;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FirmCount{");
            sb.append("firmCode='").append(firmCode).append('\'');
            sb.append(", counts=").append(counts);
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        StatsReport statsReport = new StatsReport();
        //statsReport.searchForReport("20170711", Sets.newHashSet("STATIONMAC,CLIENTMAC,USERNAME,SJHM,QQNUM,WECHAT".split(",")));
        statsReport.searchForReport(args[0], Sets.newHashSet(args[1].split(",")));
    }

}
