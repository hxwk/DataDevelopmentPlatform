package com.dfssi.dataplatform.datasync.plugin.sink.hbase.common;

public class ParameterConstant {
    public static final String EVENT_BATCH_SIZE = "event_batch_size";

    public static final String FLUSH_SIZE = "flush_size";

    public static final String DEFAULT_BATCH_SIZE = "default_batch_size";

    public static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";

    public static final String  IS_RUN_MODE="mode";

    public static final String TOPICLIST = "topicList";

    public static final String TOPIC = "topic";

    public static final String NAMESPACE = "namespace"; //默认值 空 把开发测试生产环境分开 空,dev,pro,test

    public static final String TABLENAME = "tableName";  //实际表名namespace:tableName

    public static final String TABLE="table";

    public static final String SERICES = "serices";//默认值f family

    public static final String ROWKEY = "rowkey";

    public static final String ROWKEY_RULE = "rule";
    public static final String ROWKEY_RULE_PARAM = "ruleParam";

    //rowkey规则 hash  ,mod 整数取模,random 随机数,range 整数区间,inverted time 毫秒数取反, substring_0_1(截取某个字段第几位到第几位),
    public static final String ROWKEY_RULE_MOD = "mod";

    public static final String ROWKEY_RULE_HASH = "hash";

    public static final String ROWKEY_RULE_MD5= "md5";

    public static final String ROWKEY_RULE_default= "default";//不做处理

    public static final String ROWKEY_RULE_RANDOM = "random";//seed 最大值

    public static final String ROWKEY_RULE_RANGE = "range";//seed

    public static final String ROWKEY_RULE_SUBSTRING = "substring";

    public static final String ROWKEY_RULE_INVERTED = "inverted";

    public static final String ROWKEY_RULE_INVERTED_TIME = "invertedtime";

    public static final String ROWKEY_RULE_INVERTED_STRING = "invertedstring";

    public static final String ROWKEYCONCAT = "rowkeyConcat";//连接字段,前面要处理的字段后面不需要处理

    public static final String ROWKEY_SEPARATOR ="separator";//分隔符

    public static final String COLUMN_MAPPING = "columnMappings";//字段映射

    public static final String PREFIX_LENGTH ="prefixLength"; //hbase分区的前缀长度

    public static final String MEMSTORE_FLUSH_SIZE ="flushSize"; //

    public static final String FIELD = "field";



}
