package com.dfssi.dataplatform.datasync.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";

    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_YYYYMMDDHHMMSS_XX = "yyyyMMddHHmmss";

    public static final String DATE_FORMAT_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";

    public static final String DATE_FORMAT_HHMM = "HH:mm";

    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, DATE_FORMAT_YYYYMMDD);
    }

    public static Date parseDate(String dateStr, String format) {

        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }

        if (format == null) {
            format = DATE_FORMAT_YYYYMMDD;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT_YYYYMMDD);
    }

    public static String formatDate(Date date, String format) {

        if (date == null) {
            return null;
        }

        if (format == null || format.trim().length() == 0) {
            format = DATE_FORMAT_YYYYMMDD;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public boolean isDate(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return false;
        }


        return parseDate(dateStr) != null;


    }

    public static Date getBeforeDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - days);

        return calendar.getTime();
    }

    public static Date getAfterDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + days);

        return calendar.getTime();
    }

    public static int[] getDatetimeArray(Date date) {
        if (date == null) {
            return null;
        }

        int tmp[] = new int[6];
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        tmp[0] = c.get(Calendar.YEAR);
        tmp[1] = c.get(Calendar.MONTH);
        tmp[2] = c.get(Calendar.DAY_OF_MONTH);
        tmp[3] = c.get(Calendar.HOUR);
        tmp[4] = c.get(Calendar.MINUTE);
        tmp[5] = c.get(Calendar.SECOND);

        return tmp;
    }

    public static int getTwoDateDays(Date sDate, Date eDate) {
        if (sDate == null || eDate == null) {
            return 0;
        }

        long t1 = eDate.getTime() - sDate.getTime();
        long t2 = 24 * 60 * 60 * 1000;

        return (int) (t1 / t2);
    }

    public static boolean isTwoDateBetween(Date sDate, Date eDate, Date date) {
        if (sDate == null || eDate == null || date == null) {
            return false;
        }


        return sDate.compareTo(date) <= 0 && eDate.compareTo(date) >= 0;


    }


    /**
     * 处理从数据库查询的日期串带 .0的情况
     */
    public static String formatDateStringWithDot(String timeStr) {
        if (timeStr != null) {
            return timeStr.endsWith(".0") ? timeStr.substring(0, timeStr.length() - 2) : timeStr;
        } else {
            return timeStr;
        }

    }

    public static String formatDateStandard(Date date) {
        return formatDate(date, DATE_FORMAT_YYYYMMDDHHMMSS);
    }

    public static Date getStandardDate(Date date) {
        return parseDate(formatDateStandard(date), DATE_FORMAT_YYYYMMDDHHMMSS);
    }

    //时间格式:yyyymmddHHMMSS
    public static String formatDateforUaac(Date date) {
        return formatDate(date, DATE_FORMAT_YYYYMMDDHHMMSS_XX);
    }

    //将身份证转换为生日
    public static Date formatIdentifyIdToBirthday(String identifyId) throws ParseException {
        Date date = null;
        String birthdayStr = identifyId.substring(6, 14);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        date = sdf.parse(birthdayStr);
        return date;
    }

    public static Integer TransferBirthdayToAge(Date birthday) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String cYear = sdf.format(new Date()).substring(0, 4);
        //得到生日年份
        String birthYear = sdf.format(birthday).substring(0, 4);
        int age = Integer.parseInt(cYear) - Integer.parseInt(birthYear);
        return age;
    }

    //根据数据库取出的日期转换为年月日 for example 20151111 -> 2015年11月11日
    public static String TransferStringDateToNYR(String date) {
        if (StringUtil.isEmpty(date) || (date.length() < 8)) {
            return "";
        }
        String yyyy = date.substring(0, 4);
        String mm = date.substring(4, 6);
        String dd = date.substring(6, 8);
        return yyyy + "年" + mm + "月" + dd + "日";
    }

    //根据数据库取出的日期转换为年月日 时 分 for example 201511271125 -> 2015-11-27 11:28
    public static String TransferStringDateToNYRSF(String date) {
        if (StringUtil.isEmpty(date) || (date.length() != 12)) {
            return "";
        }
        String yyyy = date.substring(0, 4);
        String mm = date.substring(4, 6);
        String dd = date.substring(6, 8);
        String hh = date.substring(8, 10);
        String mi = date.substring(10, 12);
        return yyyy + "-" + mm + "-" + dd + " " + hh + ":" + mi;
    }

    //根据数据库取出的日期转换为月日 for example 20151127 -> 11/27 yyyy-mm-dd
    public static String TransferStringDateToYR(String date) {
        if (StringUtil.isEmpty(date) || (date.length() != 10)) {
            return "";
        }
        String mm = date.substring(5, 7);
        String dd = date.substring(8, 10);
        return mm + "/" + dd;
    }


    //2015-12-8 变成 2015年12月8日
    public static String TransferStringRodDateToNYRSF(String date) {
        if (StringUtil.isEmpty(date)) {
            return "";
        }
        String yyyy = date.substring(0, 4).toString();
        int mm = Integer.parseInt(date.substring(5, 7));
        int dd = Integer.parseInt(date.substring(8, 10));
        return yyyy + "年" + mm + "月" + dd + "日";
    }

    public static String timeTransformation(Integer time) {
        if (null == time) {
            return "";
        }
        if (time < 60) {
            return time + "分钟";
        }
        if (0 == time % 60) {
            return time / 60 + "小时";
        } else {
            return time / 60 + "小时" + time % 60 + "分钟";
        }
    }

    public static String timeTransformationToMinute(Integer time) {
        if (null == time || time < 60) {
            return "0小时";
        }
        return time / 60 + "小时";
    }

    public static String timeTransformationFromString(String timeStr) {
        Integer time = Integer.valueOf(timeStr);
        return timeTransformation(time);
    }

    //计算两个时间的时间差，如果小于0 则改为0
    public static long getMinuteFromTwoDay(String dateOfOne, String dateOfTwo) {
        Date date1 = parseDate(dateOfOne, DATE_FORMAT_YYYYMMDDHHMM);
        Date date2 = parseDate(dateOfTwo, DATE_FORMAT_YYYYMMDDHHMM);
        if (date1 == null || (date2 == null)) {
            return 0;
        }
        long t1 = date2.getTime() - date1.getTime();
        return t1 / 1000 / 60 < 0 ? 0 : t1 / 1000 / 60;
    }


    //String 2015-2-8 HH：MM:ss 变成 2015-02-08
    public static String TransferStringDateToBarDate(String date) {
        if (StringUtil.isEmpty(date)) {
            return "";
        }
        String yyyy = date.substring(0, 4).toString();
        String mm = date.substring(5, 7);
        String dd = date.substring(8, 10);
        return yyyy + "-" + mm + "-" + dd;


    }


    /**
     * 判断一个日期是星期几
     *
     * @param date 格式为yyyy-MM-dd
     * @return
     */
    public static String getWeek(String date) {
        String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar c = Calendar.getInstance();// 获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return dayNames[c.get(Calendar.DAY_OF_WEEK) - 1];
    }


}
