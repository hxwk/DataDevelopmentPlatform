package com.dfssi.hbase.v2;

import com.dfssi.common.logging.SimpleLoggerFactory;
import com.dfssi.resources.Resources;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/3 10:28
 */
public class HContext implements Serializable {

    private volatile static HContext hContext;
    private HConfiguration hConfiguration;

    private transient Connection hConnection;
    private transient Admin hAdmin;
    private transient AggregationClient aggregationClient;

    private HContext() throws Exception {
        this.hConfiguration = new HConfiguration(Resources.Env.NONE);
        if(hConfiguration.isNeedInitHbaseConn()) {
            getHConnection();
            getAdmin();
            getAggregationClient();
        }
    }

    public static HContext get() throws Exception {
        if(hContext == null){
            synchronized (HContext.class){
                if(hContext == null)
                    hContext = new HContext();
            }
        }
        return hContext;
    }

    private synchronized Connection getHConnection() throws IOException {
        if(hConnection == null ||  hConnection.isClosed()) {
            if (hConfiguration.isNeedInitHbaseConn()) {
                hConnection = ConnectionFactory.createConnection(hConfiguration.getConfiguration());
            }
        }
        return hConnection;
    }

    public Configuration getConfiguration(){
        return hConfiguration.getConfiguration();
    }

    public Table newTable(TableName tableName) throws IOException {
        Table table = null;
        if(hbaseConnIsReady()){
            table = getHConnection().getTable(tableName);
        }
        return table;
    }

    public Table newTable(String tableName) throws IOException {
        return newTable(newTableName(tableName));
    }

    public HTable newHTable(TableName tableName) throws IOException {
        HTable table = null;
        if(hbaseConnIsReady()){
            table = (HTable) getHConnection().getTable(tableName);
        }
        return table;
    }

    public HTable newHTable(String tableName) throws IOException {
        return newHTable(newTableName(tableName));
    }

    public Logger newLogger(){
        return SimpleLoggerFactory.getLogger(hConfiguration.getLogPath(), "bh-hbase");
    }

    public void close(){
        if(hConnection != null){
            try {
                hConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Admin getAdmin() throws IOException {
        if(hAdmin == null) {
          if (hbaseConnIsReady()) {
                hAdmin = getHConnection().getAdmin();
            }
        }
        return hAdmin;
    }

    public AggregationClient getAggregationClient(){
        if(aggregationClient == null) {
            aggregationClient = new AggregationClient(getConfiguration());
        }
        return aggregationClient;
    }

    public RegionLocator getRegionLocator(TableName tableName) throws IOException {
        RegionLocator regionLocator = null;
        if(hbaseConnIsReady()){
           regionLocator = getHConnection().getRegionLocator(tableName);
        }
        return regionLocator;
    }

    public RegionLocator getRegionLocator(String tableName) throws IOException {
        return getRegionLocator(newTableName(tableName));
    }

    public BufferedMutator getBufferedMutator(TableName tableName) throws IOException {
        BufferedMutator bufferedMutator = null;
        if(hbaseConnIsReady()){
            bufferedMutator = getHConnection().getBufferedMutator(tableName);
        }
        return bufferedMutator;
    }

    public BufferedMutator getBufferedMutator(String tableName) throws IOException {
        return getBufferedMutator(newTableName(tableName));
    }

    public HConfiguration gethConfiguration() {
        return hConfiguration;
    }

    public boolean hbaseConnIsReady(){
        return hConfiguration.isNeedInitHbaseConn();
    }

    public static TableName newTableName(String tableName){
        return TableName.valueOf(tableName);
    }

    public static TableName newTableName(byte[] tableName){
        return TableName.valueOf(tableName);
    }

    public static TableName newTableName(String namespace, String tableName){
        return TableName.valueOf(namespace, tableName);
    }

    public static TableName newTableName(byte[] namespace, byte[] tableName){
        return TableName.valueOf(namespace, tableName);
    }

    public static void main(String[] args) throws Exception {

        HContext hContext = HContext.get();
       // Table table = hContext.newTable("test3");
        //table.incrementColumnValue(Bytes.toBytes("100"), Bytes.toBytes("info"), Bytes.toBytes("age"), 30L);

        HTableHelper.truncate("test001");
        HTableHelper.truncate("test002");

        //HTableHelper.createTable("test001", "cf", false, -1, -1, false, true);
        //HTableHelper.createTable("test002", "cf", false, -1, -1, true, false);

        HTable test001 = hContext.newHTable("test001");
        test001.setAutoFlushTo(false);

        HTable test002 = hContext.newHTable("test002");
        test002.setAutoFlushTo(false);

        Put put1;
        Put put2;
        int n = 0;
        for (int i = 0; i < 10000; i++) {

            put1 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
            put1.addColumn(Bytes.toBytes("cf"),
                    Bytes.toBytes("name"),
                    Bytes.toBytes(UUID.randomUUID().toString().replaceAll("-", "")));
            put1.addColumn(Bytes.toBytes("cf"),
                    Bytes.toBytes("age"),
                    Bytes.toBytes(String.valueOf(i)));


            put2 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
            put2.addColumn(Bytes.toBytes("cf"),
                    Bytes.toBytes("name"),
                    Bytes.toBytes(UUID.randomUUID().toString().replaceAll("-", "")));
            put2.addColumn(Bytes.toBytes("cf"),
                    Bytes.toBytes("age"),
                    Bytes.toBytes(String.valueOf(i)));

            test001.put(put1);
            test002.put(put2);

            if(++n > 1000){
                test001.flushCommits();
                test002.flushCommits();
                n = 0;
            }

        }

        test001.flushCommits();
        test002.flushCommits();
    }
}
