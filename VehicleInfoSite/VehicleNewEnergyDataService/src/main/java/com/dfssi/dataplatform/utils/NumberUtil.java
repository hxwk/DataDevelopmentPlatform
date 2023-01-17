package com.dfssi.dataplatform.utils;

import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 15:17
 */
public class NumberUtil {
    private NumberUtil(){}

    /**
     *       四舍五入
     * @param value
     * @param digits  保留位数
     * @return
     */
    public static double rounding(double value, int digits){
        BigDecimal bg = new BigDecimal(value);
        return bg.setScale(digits, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double getAndRounding(Object value, int digits){
        return rounding(Double.parseDouble(value.toString()), digits);
    }

    public static int asInt(Object value){
        if(value instanceof Integer){
            return (int)value;
        }else {
            return Integer.parseInt(value.toString());
        }
    }
}
