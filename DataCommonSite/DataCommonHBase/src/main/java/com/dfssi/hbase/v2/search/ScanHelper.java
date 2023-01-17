package com.dfssi.hbase.v2.search;

import com.dfssi.hbase.v2.HContext;
import com.dfssi.hbase.v2.HTableHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Description:
 *    Scan的帮助类：
 *    在默认情况下 scan的基础配置为：
 *    caching = 100
 *    cacheBloks = false
 * @author LiXiaoCong
 * @version 2017/5/5 14:48
 */
public class ScanHelper {

    private ScanHelper(){}

    public static Scan newEmptyScan(){
        return new Scan();
    }


    /**
     * 最简单的Scan，配置如下：
     *  caching = 100
     *  cacheBloks = false
     */
    public static Scan newSimpleScan(){

        Scan scan = newEmptyScan();
        scan.setCaching(100);
        scan.setCacheBlocks(false);
        return scan;
    }

    /**
     * 在 newSimpleScan 的基础上增加了配置：
     * Reversed = true 即查询反转
     */
    public static Scan newSimpleReversedScan(){
        Scan scan = newSimpleScan();
        scan.setReversed(true);
        return scan;
    }

    public static Scan newScanWithStr(String rowPrefix){
        return newScan(Bytes.toBytes(rowPrefix));
    }

    public static Scan newReversedScanWithStr(String rowPrefix){
        return newReversedScan(Bytes.toBytes(rowPrefix));
    }

    public static Scan newScan(byte[] rowPrefix){
        Scan scan = newSimpleScan();
        scan.setRowPrefixFilter(rowPrefix);
        return scan;
    }

    public static Scan newReversedScan(byte[] rowPrefix){
        Scan scan = newSimpleReversedScan();
        if (rowPrefix != null) {
           scan.setStartRow(calculateTheClosestNextRowKeyForPrefix(rowPrefix));
           scan.setStopRow(rowPrefix);
        }
        return scan;
    }

    public static Scan newScanWithStr(String startRow, String stopRow){
       return newScan(startRow == null ? null : Bytes.toBytes(startRow),
               stopRow == null ? null : Bytes.toBytes(stopRow));
    }

    public static Scan newReversedScanWithStr(String startRow, String stopRow){
       return newReversedScan(startRow == null ? null : Bytes.toBytes(startRow),
               stopRow == null ? null : Bytes.toBytes(stopRow));
    }

    public static Scan newScan(byte[] startRow, byte[] stopRow){
        Scan scan = newSimpleScan();

        if(startRow != null)
            scan.setStartRow(startRow);

        if(stopRow != null)
            scan.setStopRow(stopRow);

        return scan;
    }

    public static Scan newScan(byte[] startRow, byte[] stopRow, Scan model) throws IOException {
        Scan scan = new Scan(model);

        if(startRow != null)
            scan.setStartRow(startRow);

        if(stopRow != null)
            scan.setStopRow(stopRow);

        return scan;
    }

    public static Scan newReversedScan(byte[] startRow, byte[] stopRow, Scan model) throws IOException {
        Scan scan = new Scan(model);

        if(startRow != null)
            scan.setStopRow(startRow);

        if(stopRow != null)
            scan.setStartRow(stopRow);

        return scan;
    }

    public static Scan newReversedScan(byte[] startRow, byte[] stopRow){
        Scan scan = newSimpleScan();

        if(startRow != null)
            scan.setStopRow(startRow);

        if(stopRow != null)
            scan.setStartRow(stopRow);

        return scan;
    }

    public static Scan newScanWithStr(String startRow, String stopRow, int caching){
        return newScan(startRow == null ? null : Bytes.toBytes(startRow),
                       stopRow == null ? null : Bytes.toBytes(stopRow), caching);
    }

    public static Scan newReversedScanWithStr(String startRow, String stopRow, int caching){
        return newReversedScan(startRow == null ? null : Bytes.toBytes(startRow),
                       stopRow == null ? null : Bytes.toBytes(stopRow), caching);
    }

    public static Scan newScan(byte[] startRow, byte[] stopRow, int caching){
        Scan scan = newScan(startRow, stopRow);
        if(caching > 0)
            scan.setCaching(caching);
        return scan;
    }

    public static Scan newReversedScan(byte[] startRow, byte[] stopRow, int caching){
        Scan scan = newReversedScan(startRow, stopRow);
        if(caching > 0)
            scan.setCaching(caching);
        return scan;
    }

    public static Scan newScanWithStr(String startRow, String stopRow, boolean cacheBlocks){
        return newScan(startRow == null ? null : Bytes.toBytes(startRow),
                       stopRow == null ? null : Bytes.toBytes(stopRow), cacheBlocks);
    }

    public static Scan newReversedScanWithStr(String startRow, String stopRow, boolean cacheBlocks){
        return newReversedScan(startRow == null ? null : Bytes.toBytes(startRow),
                       stopRow == null ? null : Bytes.toBytes(stopRow), cacheBlocks);
    }

    public static Scan newScan(byte[] startRow, byte[] stopRow, boolean cacheBlocks){
        Scan scan = newScan(startRow, stopRow);
        scan.setCacheBlocks(cacheBlocks);
        return scan;
    }

    public static Scan newReversedScan(byte[] startRow, byte[] stopRow, boolean cacheBlocks){
        Scan scan = newReversedScan(startRow, stopRow);
        scan.setCacheBlocks(cacheBlocks);
        return scan;
    }


    public static Scan newMaxVersionScanWithStr(String startRow, String stopRow){
        return newMaxVersionScan(startRow == null ? null : Bytes.toBytes(startRow),
                stopRow == null ? null : Bytes.toBytes(stopRow));
    }

    public static Scan newMaxVersionReversedScanWithStr(String startRow, String stopRow){
        return newMaxVersionReversedScan(startRow == null ? null : Bytes.toBytes(startRow),
                stopRow == null ? null : Bytes.toBytes(stopRow));
    }

    public static Scan newMaxVersionScan(byte[] startRow, byte[] stopRow){
        Scan scan = newScan(startRow, stopRow);
        scan.setMaxVersions();
        return scan;
    }

    public static Scan newMaxVersionReversedScan(byte[] startRow, byte[] stopRow){
        Scan scan = newReversedScan(startRow, stopRow);
        scan.setMaxVersions();
        return scan;
    }


    public static Scan newMaxVersionScanWithStr(String startRow, String stopRow, int caching){
        return newMaxVersionScan(startRow == null ? null : Bytes.toBytes(startRow),
                stopRow == null ? null : Bytes.toBytes(stopRow), caching);
    }

    public static Scan newMaxVersionReversedScanWithStr(String startRow, String stopRow, int caching){
        return newMaxVersionReversedScan(startRow == null ? null : Bytes.toBytes(startRow),
                stopRow == null ? null : Bytes.toBytes(stopRow), caching);
    }

    public static Scan newMaxVersionScan(byte[] startRow, byte[] stopRow, int caching){
        Scan scan = newMaxVersionScan(startRow, stopRow);
        if(caching > 0)
            scan.setCaching(caching);
        return scan;
    }

    public static Scan newMaxVersionReversedScan(byte[] startRow, byte[] stopRow, int caching){
        Scan scan = newMaxVersionReversedScan(startRow, stopRow);
        if(caching > 0)
            scan.setCaching(caching);
        return scan;
    }

    public static Scan newScanWithStr(String startRow,
                                      String stopRow,
                                      int caching,
                                      boolean cacheBlocks,
                                      boolean maxVersion,
                                      boolean reversed){

        return newScan(startRow == null ? null : Bytes.toBytes(startRow),
                stopRow == null ? null : Bytes.toBytes(stopRow), caching, cacheBlocks, maxVersion, reversed);
    }

    public static Scan newScan(byte[] startRow,
                               byte[] endRow,
                               int caching,
                               boolean cacheBlocks,
                               boolean maxVersion,
                               boolean reversed){
        Scan scan;
        if(reversed){
            scan = newReversedScan(startRow, endRow, caching);
        }else {
            scan = newScan(startRow, endRow, caching);
        }

        scan.setCacheBlocks(cacheBlocks);
        if(maxVersion)
            scan.setMaxVersions();

        return scan;
    }

    public static Scan newScan(byte[] startRow,
                               byte[] endRow,
                               boolean reversed,
                               Scan model) throws IOException {
        Scan scan;
        if(reversed){
            scan = newReversedScan(startRow, endRow, model);
        }else {
            scan = newScan(startRow, endRow, model);
        }
        return scan;
    }


    /**
     * 根据表在hbase中分区 分别创建Scan
     * @param tableName
     * @param startRow
     * @param endRow
     * @throws IOException
     */
    public static List<Scan> newScansByRegions(String tableName,
                                               String startRow,
                                               String endRow,
                                               int caching,
                                               boolean cacheBlocks,
                                               boolean maxVersion,
                                               boolean reversed) throws Exception {

        HContext hContext = HContext.get();

        byte[] start = StringUtils.isNotBlank(startRow) ?
                Bytes.toBytes(String.format("%s,%s", tableName, startRow)) : Bytes.toBytes(String.format("%s,", tableName));

        byte[] end = StringUtils.isNotBlank(endRow) ?
                Bytes.toBytes(String.format("%s,%s", tableName, endRow)) : calculateTheClosestNextRowKeyForPrefix(start);

        Scan scan = newScan(start, end, 100);
        scan.setFilter(new KeyOnlyFilter());
        Table table = hContext.newTable("hbase:meta");
        ResultScanner scanner = table.getScanner(scan);

        List<Scan> scans = Lists.newArrayList();
        Result result;
        String regionStart;
        String rstart = null;
        String rstop = null;
        while((result = scanner.next()) != null){

            if(rstop != null){
                scan = newScan(Bytes.toBytes(rstart),
                        Bytes.toBytes(rstop), caching, cacheBlocks, maxVersion, reversed);
                scans.add(scan);
                rstart = rstop;
            }

            regionStart = Bytes.toString(result.getRow()).split(",")[1];
            if(rstart == null){
                rstart = regionStart;
                //在startRow 不等于 rstart 情况： begin---start----regionStart----...---stop---end
                //我们取的是 start 到 stop 的范围， begin到end是该表所有region的key的范围。
                if(startRow != null && !startRow.equals(regionStart)){
                    scan = newScan(Bytes.toBytes(startRow),
                            Bytes.toBytes(rstart), caching, cacheBlocks, maxVersion, reversed);
                    scans.add(scan);
                }
            }else {
                rstop = regionStart;
            }
        }

        if(scans.size() <= 1){

            if(rstart == null && StringUtils.isNotBlank(startRow)){
                rstart = startRow;
            }

            scan = newScan(StringUtils.isNotBlank(rstart) ? Bytes.toBytes(rstart) : HConstants.EMPTY_START_ROW,
                           StringUtils.isNotBlank(endRow) ? Bytes.toBytes(endRow) : HConstants.EMPTY_END_ROW,
                           caching, cacheBlocks, maxVersion, reversed);
            scans.add(scan);
        }else{
            scan = newScan(Bytes.toBytes(rstart),
                           Bytes.toBytes(rstop),
                           caching, cacheBlocks, maxVersion, reversed);
            scans.add(scan);

            if(!rstop.equals(endRow)){
                scan = newScan(Bytes.toBytes(rstop),
                        StringUtils.isNotBlank(endRow) ? Bytes.toBytes(endRow) : HConstants.EMPTY_END_ROW,
                        caching, cacheBlocks, maxVersion, reversed);
                scans.add(scan);
            }
        }

        HTableHelper.close(scanner);
        HTableHelper.close(table);

        return scans;
    }

    public static List<Scan> newScansByRegions(String tableName,
                                               String startRow,
                                               String endRow,
                                               boolean reversed,
                                               Scan model) throws Exception {

        HContext hContext = HContext.get();

        byte[] start = StringUtils.isNotBlank(startRow) ?
                Bytes.toBytes(String.format("%s,%s", tableName, startRow)) : Bytes.toBytes(String.format("%s,", tableName));

        byte[] end = StringUtils.isNotBlank(endRow) ?
                Bytes.toBytes(String.format("%s,%s", tableName, endRow)) : calculateTheClosestNextRowKeyForPrefix(start);

        Scan scan = newScan(start, end, 100);
        scan.setFilter(new KeyOnlyFilter());
        Table table = hContext.newTable("hbase:meta");
        ResultScanner scanner = table.getScanner(scan);

        List<Scan> scans = Lists.newArrayList();
        Result result;
        String regionStart;
        String rstart = null;
        String rstop = null;
        while((result = scanner.next()) != null){

            if(rstop != null){
                scan = newScan(Bytes.toBytes(rstart),
                        Bytes.toBytes(rstop), reversed, model);
                scan.setId(UUID.randomUUID().toString());
                scans.add(scan);
                rstart = rstop;
            }

            regionStart = Bytes.toString(result.getRow()).split(",")[1];
            if(rstart == null){
                rstart = regionStart;
                //在startRow 不等于 rstart 情况： begin---start----regionStart----...---stop---end
                //我们取的是 start 到 stop 的范围， begin到end是该表所有region的key的范围。
                if(startRow != null && !startRow.equals(regionStart)){
                    scan = newScan(Bytes.toBytes(startRow),
                            Bytes.toBytes(rstart), reversed, model);
                    scan.setId(UUID.randomUUID().toString());
                    scans.add(scan);
                }
            }else {
                rstop = regionStart;
            }
        }

        if(scans.size() <= 1){

            if(rstart == null && StringUtils.isNotBlank(startRow)){
                rstart = startRow;
            }

            scan = newScan(StringUtils.isNotBlank(rstart) ? Bytes.toBytes(rstart) : HConstants.EMPTY_START_ROW,
                           StringUtils.isNotBlank(endRow) ? Bytes.toBytes(endRow) : HConstants.EMPTY_END_ROW,
                           reversed, model);
            scan.setId(UUID.randomUUID().toString());
            scans.add(scan);
        }else{
            scan = newScan(Bytes.toBytes(rstart),
                           Bytes.toBytes(rstop),
                           reversed, model);
            scan.setId(UUID.randomUUID().toString());
            scans.add(scan);

            if(!rstop.equals(endRow)){
                scan = newScan(Bytes.toBytes(rstop),
                        StringUtils.isNotBlank(endRow) ? Bytes.toBytes(endRow) : HConstants.EMPTY_END_ROW,
                        reversed, model);
                scan.setId(UUID.randomUUID().toString());
                scans.add(scan);
            }
        }

        HTableHelper.close(scanner);
        HTableHelper.close(table);

        return scans;
    }






    /**
     * 根据所给的rowKey前缀(startRow) 计算出 查询所需的 stopRow
     * @param rowKeyPrefix
     * @return
     */
    public static byte[] calculateTheClosestNextRowKeyForPrefix(byte[] rowKeyPrefix) {

        int offset = rowKeyPrefix.length;
        while (offset > 0) {
            if (rowKeyPrefix[offset - 1] != (byte) 0xFF) break;
            offset--;
        }

        if (offset == 0) return HConstants.EMPTY_END_ROW;

        byte[] newStopRow = Arrays.copyOfRange(rowKeyPrefix, 0, offset);
        newStopRow[newStopRow.length - 1]++;

        return newStopRow;
    }

    public static void main(String[] args) throws Exception {

        List<Scan> scans = newScansByRegions("JIEYANG:Ge4Mac_ETL",
                "129348386D01",
                "EAEAAD398",
                100,
                false,
                false,
                false);

        int n = 0;
        for(Scan scan : scans){
            System.out.print(n++ + ", start ->" +  Bytes.toString(scan.getStartRow()));
            System.out.print(", end ->" +  Bytes.toString(scan.getStopRow()));
            System.out.println();
        }
    }

}
