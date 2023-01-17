package com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf;


import java.util.List;

public class EventTopicConfig {
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

    private String topic;
    private String table;
    private String family;
    private String namespace;
    private String param;
    private String rule;
    private String field;
    private String separator;
    private Integer prefixLength;
    private List concat;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }



    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public List getConcat() {
        return concat;
    }

    public void setConcat(List concat) {
        this.concat = concat;
    }

    public Integer getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(Integer prefixLength) {
        this.prefixLength = prefixLength;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
