package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtil {

    public static long getOneDayStartTimeStamp(String targetTime) {
        try {
            long result = -1l;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.parse(targetTime).getTime();
        } catch (ParseException e) {
            return 0l;
        }
    }
}
