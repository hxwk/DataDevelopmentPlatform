package com.dfssi.hbase.v2;

import com.dfssi.hbase.v2.builder.HTableDescriptorBuilder;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/3 11:43
 */
public class HTableHelper {

    private HTableHelper(){}

    public static boolean exists(String tableName) throws Exception {
        return exists(HContext.newTableName(tableName));
    }

    public static boolean exists(TableName tableName) throws Exception {
        Admin admin = HContext.get().getAdmin();
        if(admin == null){
            return false;
        }
        return admin.tableExists(tableName);
    }

    public static void disable(String tableName) throws Exception {
      disable(HContext.newTableName(tableName));
    }

    public static void disable(TableName tableName) throws Exception {
       HContext.get().getAdmin().disableTable(tableName);
    }

    public static void enable(String tableName) throws Exception {
      enable(HContext.newTableName(tableName));
    }

    public static void enable(TableName tableName) throws Exception {
       HContext.get().getAdmin().enableTable(tableName);
    }

    public static boolean isDisabled(String tableName) throws Exception {
       return isDisabled(HContext.newTableName(tableName));
    }

    public static boolean isDisabled(TableName tableName) throws Exception {
       return HContext.get().getAdmin().isTableDisabled(tableName);
    }

    public static void delete(String tableName) throws Exception{
        delete(HContext.newTableName(tableName));
    }

    public static void delete(TableName tableName) throws Exception {

        Admin admin = HContext.get().getAdmin();

        if(exists(tableName)){
            if(!admin.isTableDisabled(tableName)){
                admin.disableTable(tableName);
            }
            admin.deleteTable(tableName);
        }
    }

    public static void truncate(String tableName) throws Exception {
        truncate(HContext.newTableName(tableName));
    }

    public static void truncate(TableName tableName) throws Exception {

        Admin admin = HContext.get().getAdmin();

        if(exists(tableName)){
            if(!admin.isTableDisabled(tableName)){
                admin.disableTable(tableName);
            }
            admin.truncateTable(tableName, true);
        }
    }

    public static String[] listTableNamesAsString() throws Exception{
        return listTableNamesAsString(null);
    }

    public static String[] listTableNamesAsString(String reg) throws Exception{

        TableName[] tableNames = listTableNames(reg);
        String[] list = new String[tableNames.length];

        for (int i = 0; i < tableNames.length; i++) {
            list[i] = tableNames[i].getNameAsString();
        }
        return list;
    }

    public static TableName[] listTableNames() throws Exception {
        return listTableNames(null);
    }

    public static TableName[] listTableNames(String reg) throws Exception{
        Admin admin = HContext.get().getAdmin();

        TableName[] tableNames;
        if(reg == null){
            tableNames = admin.listTableNames();
        }else {
            tableNames = admin.listTableNames(reg);
        }
        return tableNames;
    }

    public static void deleteNameSpace(String nameSpace) throws Exception {
        HContext.get().getAdmin().deleteNamespace(nameSpace);
    }

    public static boolean existNameSpace(String nameSpace) throws Exception {
        NamespaceDescriptor[] namespaceDescriptors = HContext.get().getAdmin().listNamespaceDescriptors();
        boolean exist = false;
        for(NamespaceDescriptor namespaceDescriptor : namespaceDescriptors){
            if(namespaceDescriptor.getName().equals(nameSpace)){
                exist = true;
                break;
            }
        }
        return exist;
    }

    public static void createNameSpace(String nameSpace) throws Exception {

        Admin admin = HContext.get().getAdmin();
        NamespaceDescriptor[] listNamespaceDescriptors = admin.listNamespaceDescriptors();
        boolean exist = false;
        for(NamespaceDescriptor namespaceDescriptor : listNamespaceDescriptors) {
            if (namespaceDescriptor.getName().equals(nameSpace)) {
                exist = true;
            }
        }

        if(!exist)
            admin.createNamespace(NamespaceDescriptor.create(nameSpace).build());
    }

    public static HTableDescriptorBuilder newHTableDescriptorBuilder(String tableName) throws Exception {
        return new HTableDescriptorBuilder(tableName);
    }

    public static HTableDescriptorBuilder newHTableDescriptorBuilder(String namescpace,
                                                                     String tableName) throws Exception {
      return  new HTableDescriptorBuilder(namescpace, tableName);
    }

    public static void createTable(HTableDescriptorBuilder builder,
                                   byte[] startKey,
                                   byte[] endKey,
                                   int numRegions) throws Exception {
        createTable(builder.build(), startKey, endKey, numRegions);
    }

    public static void createTable(HTableDescriptor htableDesc,
                                   byte[] startKey,
                                   byte[] endKey,
                                   int numRegions) throws Exception {
        Admin admin = HContext.get().getAdmin();
        admin.createTable(htableDesc, startKey, endKey, numRegions);
    }

    public static void createTable(HTableDescriptorBuilder builder,
                                   byte[][] splitKeys) throws Exception {

        createTable(builder.build(), splitKeys);
    }

    public static void createTable(HTableDescriptor htableDesc,
                                   byte[][] splitKeys) throws Exception {

        Admin admin = HContext.get().getAdmin();
        admin.createTable(htableDesc, splitKeys);
    }

    public static void createTable(HTableDescriptorBuilder builder) throws Exception {
        createTable(builder.build());
    }

    public static void createTable(HTableDescriptor htableDesc) throws Exception {
        Admin admin = HContext.get().getAdmin();
        admin.createTable(htableDesc);
    }


    public static void createTable(String tableName,
                                   String columnFamily,
                                   boolean inMemory,
                                   int ttl,
                                   int maxVersion) throws Exception {

        HTableDescriptor tableDescriptor =
                newHTableDescriptor(tableName, columnFamily, inMemory, ttl, maxVersion);
        createTable(tableDescriptor);
    }

    public static void createTable(String tableName,
                                   String columnFamily,
                                   boolean inMemory,
                                   int ttl,
                                   int maxVersion,
                                   byte[] startKey,
                                   byte[] endKey,
                                   int numRegions) throws Exception {

        HTableDescriptor tableDescriptor =
                newHTableDescriptor(tableName, columnFamily, inMemory, ttl, maxVersion);
        createTable(tableDescriptor, startKey, endKey, numRegions);
    }

    public static void createTable(String tableName,
                                   String columnFamily,
                                   boolean inMemory,
                                   int ttl,
                                   int maxVersion,
                                   byte[][] splitKeys) throws Exception {

        HTableDescriptor tableDescriptor =
                newHTableDescriptor(tableName, columnFamily, inMemory, ttl, maxVersion);
        createTable(tableDescriptor, splitKeys);
    }

    public static void createTable(String tableName,
                                   String columnFamily,
                                   boolean inMemory,
                                   int ttl,
                                   int maxVersion,
                                   boolean openCompression,
                                   boolean openDataBlockEncoding) throws Exception {

        HTableDescriptor tableDescriptor =
                newHTableDescriptor(tableName, columnFamily, inMemory, ttl,
                        maxVersion, openCompression, openDataBlockEncoding);

        createTable(tableDescriptor);
    }

    public static void createTable(String tableName,
                                   String columnFamily,
                                   boolean inMemory,
                                   int ttl,
                                   int maxVersion,
                                   boolean openCompression,
                                   boolean openDataBlockEncoding,
                                   byte[][] splitKeys) throws Exception {

        HTableDescriptor tableDescriptor =
                newHTableDescriptor(tableName, columnFamily, inMemory, ttl,
                        maxVersion, openCompression, openDataBlockEncoding);

        createTable(tableDescriptor, splitKeys);
    }

    public static void createTable(String tableName,
                                   String columnFamily,
                                   boolean inMemory,
                                   int ttl,
                                   int maxVersion,
                                   boolean openCompression,
                                   boolean openDataBlockEncoding,
                                   byte[] startKey,
                                   byte[] endKey,
                                   int numRegions) throws Exception {

        HTableDescriptor tableDescriptor =
                newHTableDescriptor(tableName, columnFamily, inMemory, ttl,
                        maxVersion, openCompression, openDataBlockEncoding);

        createTable(tableDescriptor, startKey, endKey, numRegions);
    }



    public static HTableDescriptor newHTableDescriptor(String tableName,
                                                       String columnFamily,
                                                       boolean inMemory,
                                                       int ttl,
                                                       int maxVersion) throws Exception {
        return newHTableDescriptor(tableName,
                columnFamily,
                inMemory, ttl,
                maxVersion, true, false);
    }

    public static HTableDescriptor newHTableDescriptor(String tableName,
                                                       String columnFamily,
                                                       boolean inMemory,
                                                       int ttl,
                                                       int maxVersion,
                                                       boolean openCompression,
                                                       boolean openDataBlockEncoding) throws Exception {

        HTableDescriptorBuilder tableDescriptorBuilder = newHTableDescriptorBuilder(tableName);

        HTableDescriptorBuilder.HColumnDescriptorBuilder columnDescriptorBuilder =
                tableDescriptorBuilder.newHColumnDescriptorBuilder(columnFamily);
        columnDescriptorBuilder.setTimeToLive(ttl);
        columnDescriptorBuilder.setMaxVersion(maxVersion);
        if(inMemory)columnDescriptorBuilder.setInMemory();
        if(openCompression) columnDescriptorBuilder.openCompression();
        if(openDataBlockEncoding)columnDescriptorBuilder.openDataBlockEncoding();

        return tableDescriptorBuilder.build();
    }


    /**
     * 为已存在的表增加协处理器
     * @param tableName
     * @param clazz
     * @throws Exception
     */
    public static void addCoprocessor(String tableName, String clazz) throws Exception {
        TableName name = HContext.newTableName(tableName);
        Admin admin = HContext.get().getAdmin();

        HTableDescriptor htd = admin.getTableDescriptor(name);

        if(!htd.hasCoprocessor(clazz)) {
            htd.addCoprocessor(clazz);

            admin.disableTable(name);
            admin.modifyTable(name, htd);
            admin.enableTable(name);
        }
    }

    public static void modifyTable(String tableName, HTableDescriptor htd) throws Exception {

        TableName name = HContext.newTableName(tableName);
        Admin admin = HContext.get().getAdmin();

        admin.disableTable(name);
        admin.modifyTable(name, htd);
        admin.enableTable(name);
    }

    public static void close(Table table){
        if(table != null) {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultScanner scanner){
        if(scanner != null)
            scanner.close();
    }

    public static void close(BufferedMutator mutator){
        if(mutator != null) {
            try {
                mutator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        System.out.println(listTableNames());
    }

}
