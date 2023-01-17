package com.dfssi.dataplatform.utils;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Description:
 *   根据 传入的时间戳 以及默认的时间长度 计算边界
 *   lower 和 upper 可以为null,  取值精确到毫秒
 *   获取边界逻辑：
 *       1. lower != null && upper != null，则直接格式化为对应边界
 *       2. lower != null && upper == null, 则令upper等于当前时间戳，再格式化为对应边界
 *       3. lower == null && upper != null, 则令lower等于upper减去指定的时间间隔，再格式化为对应边界
 *       4. lower == null && upper == null, 则先令upper等于当前时间戳，再令lower等于upper减去指定的时间间隔，最后格式化为对应边界
 * @author LiXiaoCong
 * @version 2018/4/26 15:28
 */
public class DateTimeBoundary {

    public enum TimeIntervalUnit{
        SECONDS,MINUTES,HOURS,DAYS,WEEKS,MONTHS,YEARS
    }

    private String lower;
    private String upper;

    public DateTimeBoundary(Long lower,
                            Long upper,
                            int interval,
                            TimeIntervalUnit timeUnit,
                            String format){


        DateTime endDate;
        if(upper == null){
            endDate = DateTime.now();
        }else{
            endDate = new DateTime(upper);
        }

        DateTime startDate;
        if(lower == null){
            startDate = endDate.minus(getTimePeriod(interval, timeUnit));

        }else{
            startDate = new DateTime(lower);
        }

        Preconditions.checkArgument(!endDate.isBefore(startDate),
                String.format("lower = %s >  upper = %s", lower, upper));

        this.lower = startDate.toString(format);
        this.upper = endDate.toString(format);
    }

    private Period getTimePeriod(int interval,
                                 TimeIntervalUnit timeUnit){

        switch (timeUnit){
            case SECONDS:
                return Period.seconds(interval);
            case MINUTES:
                return Period.minutes(interval);
            case HOURS:
                return Period.hours(interval);
            case DAYS:
                return Period.days(interval);
            case WEEKS:
                return Period.weeks(interval);
            case MONTHS:
                return Period.months(interval);
            case YEARS:
                return Period.years(interval);
        }
        return null;
    }

    public String getLower() {
        return lower;
    }

    public String getUpper() {
        return upper;
    }
}
