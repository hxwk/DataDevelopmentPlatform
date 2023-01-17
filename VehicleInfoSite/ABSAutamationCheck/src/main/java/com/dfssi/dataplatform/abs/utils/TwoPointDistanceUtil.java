package com.dfssi.dataplatform.abs.utils;

/**
 * Description:
 *
 * @author Weijj
 * @version 2018/9/15 17:16
 */
public class TwoPointDistanceUtil {
    private static final double PI = 3.14159265;
    private static final double EARTH_RADIUS = 6378137;
    private static final double RAD = Math.PI / 180.0;

    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = lat1 * RAD;
        double radLat2 = lat2 * RAD;
        double a = radLat1 - radLat2;
        double b = (lng1 - lng2) * RAD;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

//    public static void main(String[] args) {
//        double distance = getDistance(114.175932, 30.354266, 114.175842, 30.354142);
//        System.out.println(distance);
//    }
}
