package com.dfssi.hbase.v2.builder;

import com.dfssi.hbase.v2.HContext;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.regionserver.BloomType;

import java.io.IOException;

public class HTableDescriptorBuilder {

    private TableName table;
    private HTableDescriptor tableDescriptor;

    public HTableDescriptorBuilder(String table) throws Exception {
        this(null, table);
    }

    public HTableDescriptorBuilder(String nameSpace, String table) throws Exception {

        if(nameSpace == null){
            this.table = HContext.newTableName(table);
        }else {
            this.table = HContext.newTableName(nameSpace, table);
        }

        this.tableDescriptor = new HTableDescriptor(this.table);

        addCoprocessor("org.apache.hadoop.hbase.coprocessor.AggregateImplementation");
    }


    public void addCoprocessor(String clazz) throws IOException {
        if(!this.tableDescriptor.hasCoprocessor(clazz))
            this.tableDescriptor.addCoprocessor(clazz);
    }

    /**
     * 设置全局跳过 wal日志
     */
    public void skipWal(){
        this.tableDescriptor.setDurability(Durability.SKIP_WAL);
    }

    /**
     * 设置为异步写 wal日志
     */
    public void writeWalAsync(){
        this.tableDescriptor.setDurability(Durability.ASYNC_WAL);
    }

    public void setConfiguration(String key, String value){
        this.tableDescriptor.setConfiguration(key, value);
    }

    public String getTable() {
        return table.getNameAsString();
    }

    public HColumnDescriptorBuilder newHColumnDescriptorBuilder(String columnFamily) {
        HColumnDescriptorBuilder builder = new HColumnDescriptorBuilder(columnFamily);
        this.tableDescriptor.addFamily(builder.build());
        return builder;
    }

    public HTableDescriptor build() {
        return tableDescriptor;
    }

    public class HColumnDescriptorBuilder {

        private HColumnDescriptor hcd;

        private HColumnDescriptorBuilder(String columnFamily) {
            this.hcd = new HColumnDescriptor(columnFamily);

            //能够减少特定访问模式下的查询时间，但是增加了内存和存储的负担。hbase默认关闭
            //这里为了提高查询效率 默认开启  以空间换时间
            this.hcd.setBloomFilterType(BloomType.ROWCOL);

            //默认全局复制
            this.hcd.setScope(1);

            //默认开启压缩
            openCompression();
        }


        public void setMaxVersion(int maxVersion) {
            if (maxVersion > 0)
                this.hcd.setMaxVersions(maxVersion);
        }

        /**
         * 设置复制范围
         * 0为本地, 1为全局
         */
        public void setScope(int scope) {
            this.hcd.setScope(scope == 0 ? 0 : 1);
        }


        /**
         * 设置为内存表
         */
        public void setInMemory() {
            this.hcd.setInMemory(true);
        }

        /**
         * 开启压缩，并使用 SNAPPY 算法
         * 数据量大，边压边写也会提升性能的，毕竟IO是大数据的最严重的瓶颈，哪怕使用了SSD也是一样。
         * 众多的压缩方式中，推荐使用SNAPPY。从压缩率和压缩速度来看，性价比最高。
         */
        public void openCompression() {
            this.hcd.setCompressionType(Compression.Algorithm.SNAPPY);
        }


        /**
         * 默认为NONE
         * 如果数据存储时设置了编码， 在缓存到内存中的时候是不会解码的，这样和不编码的情况相比，相同的数据块，编码后占用的内存更小， 即提高了内存的使用率
         * 如果设置了编码，用户必须在取数据的时候进行解码， 因此在内存充足的情况下会降低读写性能。
         * 在任何情况下开启PREFIX_TREE编码都是安全的
         * 不要同时开启PREFIX_TREE和SNAPPY
         * 通常情况下 SNAPPY并不能比 PREFIX_TREE取得更好的优化效果
         */
        public void openDataBlockEncoding() {
            this.hcd.setDataBlockEncoding(DataBlockEncoding.PREFIX_TREE);
        }


        /**
         * 默认为64k     65536   单位为字节
         * 随着blocksize的增大， 系统随机读的吞吐量不断的降低，延迟也不断的增大，
         * 64k大小比16k大小的吞吐量大约下降13%，延迟增大13%
         * 128k大小比64k大小的吞吐量大约下降22%，延迟增大27%
         * 对于随机读取为主的业务，可以考虑调低blocksize的大小
         * <p>
         * 随着blocksize的增大， scan的吞吐量不断的增大，延迟也不断降低，
         * 64k大小比16k大小的吞吐量大约增加33%，延迟降低24%
         * 128k大小比64k大小的吞吐量大约增加7%，延迟降低7%
         * 对于scan为主的业务，可以考虑调大blocksize的大小
         * <p>
         * 如果业务请求以Get为主，则可以适当的减小blocksize的大小
         * 如果业务是以scan请求为主，则可以适当的增大blocksize的大小
         * 系统默认为64k, 是一个scan和get之间取的平衡值
         */
        public void setBlocksize(int blocksize) {
            this.hcd.setBlocksize(blocksize);
        }

        /**
         * 版本数据的过期时间 单位为秒
         *
         * @param timeToLive
         */
        public void setTimeToLive(int timeToLive) {
            if (timeToLive > 0) this.hcd.setTimeToLive(timeToLive);
        }

        public void set(String key, String value) {
            this.hcd.setValue(key, value);
        }

        public HColumnDescriptor build() {
            return hcd;
        }
    }
}