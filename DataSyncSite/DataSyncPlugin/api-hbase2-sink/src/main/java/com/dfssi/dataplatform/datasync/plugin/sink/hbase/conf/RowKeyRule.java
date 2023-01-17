package com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf;



public class RowKeyRule {

    private String rule;
    private String field;
    private String separator;
    private String getPrefixLength;
    private String concat;


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

    public String getGetPrefixLength() {
        return getPrefixLength;
    }

    public void setGetPrefixLength(String getPrefixLength) {
        this.getPrefixLength = getPrefixLength;
    }

    public String getConcat() {
        return concat;
    }

    public void setConcat(String concat) {
        this.concat = concat;
    }
}
