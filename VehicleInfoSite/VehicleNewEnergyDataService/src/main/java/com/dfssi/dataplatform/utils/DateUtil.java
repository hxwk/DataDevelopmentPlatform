package com.dfssi.dataplatform.utils;

import org.joda.time.DateTime;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 13:01
 */
public class DateUtil {
    private DateUtil(){}

    public static String getCurrentMonthFirstDay(String format){
        return DateTime.now().dayOfMonth().withMinimumValue().toString(format);
    }

    public static Long getCurrentMonthFirstDayForLong(){
        return DateTime.now().dayOfMonth().withMinimumValue().getMillis();
    }

    public static String getCurrentMonthLastDay(String format){
        return DateTime.now().dayOfMonth().withMaximumValue().toString(format);
    }

    public static long getCurrentMonthLastDayForLong(){
        return DateTime.now().dayOfMonth().withMaximumValue().getMillis();
    }

    public static long getTodayStartForLong(){
        return DateTime.now().withTimeAtStartOfDay().getMillis();
    }

    public static String getNow(String format){
        return DateTime.now().toString(format);
    }

    public static String getDateStr(long timestamp, String format){
        return new DateTime(timestamp).toString(format);
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.getCurrentMonthLastDay("yyyyMMdd"));
    }


}

