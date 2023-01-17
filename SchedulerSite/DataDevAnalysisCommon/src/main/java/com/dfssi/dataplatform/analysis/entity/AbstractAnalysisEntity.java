package com.dfssi.dataplatform.analysis.entity;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public abstract class AbstractAnalysisEntity {

    private static final long serialVersionUID = 1L;

    private String showName;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String buidId() {
        long index = this.nextIndex();
        String indexStr = StringUtils.right("000000" + index, 6);

        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + indexStr;
    }

    abstract public long nextIndex();

}
