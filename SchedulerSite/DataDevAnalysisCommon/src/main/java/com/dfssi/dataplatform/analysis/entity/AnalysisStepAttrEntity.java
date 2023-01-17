package com.dfssi.dataplatform.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

//@Alias("AnalysisStepAttrEntity")
public class AnalysisStepAttrEntity extends AbstractAnalysisEntity {

    private static volatile long index = 0;

    private String id;
    private String modelStepId;
    private String code;
    private int nRow;
    private String valueStr;
    private BigDecimal valueNum;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelStepId() {
        return modelStepId;
    }

    public void setModelStepId(String modelStepId) {
        this.modelStepId = modelStepId;
    }

    public int getnRow() {
        return nRow;
    }

    public void setnRow(int nRow) {
        this.nRow = nRow;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    public BigDecimal getValueNum() {
        return valueNum;
    }

    public void setValueNum(BigDecimal valueNum) {
        this.valueNum = valueNum;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public long nextIndex() {
        if (index == 9999) {
            index = 0;
            return index;
        } else {
            return index++;
        }
    }

}
