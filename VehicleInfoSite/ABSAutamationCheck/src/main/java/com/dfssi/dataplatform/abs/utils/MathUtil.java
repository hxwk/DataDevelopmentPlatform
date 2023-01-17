package com.dfssi.dataplatform.abs.utils;

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


    //计算球面距离（公里、千米）
    public static double calDistance(double lat1,
                                     double lon1,
                                     double lat2,
                                     double lon2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6371.393;

        return  rounding(s, 4);
    }

    private static double rad(Double dDegree){
        return dDegree * Math.PI / 180.0;
    }

}
