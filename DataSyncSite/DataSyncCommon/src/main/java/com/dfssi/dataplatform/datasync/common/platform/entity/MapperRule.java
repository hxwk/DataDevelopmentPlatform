package com.dfssi.dataplatform.datasync.common.platform.entity;

import java.io.Serializable;

/**
 * @author JianKang
 * @date 2018/5/2
 * @description 对每个字段调整定义规则
 */
public class MapperRule implements Serializable {
    /**带因子和偏移量*/
    private String factor; //k
    private String offset; //b
    /**查找和替换*/
    private String searchPattern; //源字段
    private String replaceString; //目标字段
    /**脱敏替换*/
    private String maskStart; //起始字段
    private String maskEnd; //结束字段
    private String insertString; //插入的字符

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public String getReplaceString() {
        return replaceString;
    }

    public void setReplaceString(String replaceString) {
        this.replaceString = replaceString;
    }

    public String getMaskStart() {
        return maskStart;
    }

    public void setMaskStart(String maskStart) {
        this.maskStart = maskStart;
    }

    public String getMaskEnd() {
        return maskEnd;
    }

    public void setMaskEnd(String maskEnd) {
        this.maskEnd = maskEnd;
    }

    public String getInsertString() {
        return insertString;
    }

    public void setInsertString(String insertString) {
        this.insertString = insertString;
    }
}
