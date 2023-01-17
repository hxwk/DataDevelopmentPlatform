package com.dfssi.common;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/2/6 12:17
 */
public enum DateSuffixAppender {
    DAY{
        @Override
        public String append(String baseName, Date date){
            Preconditions.checkNotNull(date, String.format("%s对应的日期不能为空。", baseName));
            String tablePrefix = Dates.nowStr(date, "yyyyMMdd");
            return String.format("%s_%s", baseName, tablePrefix);
        }
    },

    MONTH{
        @Override
        public String append(String baseName, Date date){
            Preconditions.checkNotNull(date, String.format("%s对应的日期不能为空。", baseName));
            String tablePrefix = Dates.nowStr(date, "yyyyMM");
            return String.format("%s_%s", baseName, tablePrefix);
        }

    },

    YEAR{
        @Override
        public String append(String baseName, Date date) {
            Preconditions.checkNotNull(date, String.format("%s对应的日期不能为空。", baseName));
            String tablePrefix = Dates.nowStr(date, "yyyy");
            return String.format("%s_%s", baseName, tablePrefix);
        }
    };

    public abstract String append(String baseName, Date date);

    public static DateSuffixAppender newInstance(String appender) throws IllegalArgumentException{

        if(StringUtils.isBlank(appender)){
            throw new IllegalArgumentException("类型不能为空。");
        }

        switch (appender.toUpperCase()){
            case "DAY"   : return DAY;
            case "MONTH" : return MONTH;
            case "YEAR"  : return YEAR;
            default:
                throw new IllegalArgumentException(String.format("不识别的类型：%s", appender));
        }

    }
}
