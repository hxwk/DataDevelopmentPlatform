package com.dfssi.dataplatform.manager.monitor.task.entity;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public abstract class AbstractEntity {

    private static final long serialVersionUID = 1L;
    private transient static long index = 0;

    public String buidId() {
        long index = this.nextIndex();
        String indexStr = StringUtils.right("000000" + index, 6);

        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + indexStr;
    }

    public long nextIndex() {
        if (index == 999999) {
            index = 0;
            return index;
        } else {
            return index++;
        }
    }

}
