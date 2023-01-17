package com.dfssi.dataplatform.quartz.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/17 8:48
 */
public class MathUtil {
    private MathUtil(){}

    public static double rounding(double value, int p) {
        BigDecimal b = new BigDecimal(value);
        return b.setScale(p, RoundingMode.HALF_UP).doubleValue();
    }
}
