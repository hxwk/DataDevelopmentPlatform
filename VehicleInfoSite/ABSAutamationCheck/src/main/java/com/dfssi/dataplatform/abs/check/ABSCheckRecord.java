package com.dfssi.dataplatform.abs.check;

import com.dfssi.dataplatform.abs.redis.Encoders;
import com.dfssi.dataplatform.abs.utils.AbsConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/20 10:29
 */
@Getter
@Slf4j
public class ABSCheckRecord {
    /**车辆id*/
    private String vid;
    /**传感器车速*/
    private double sensorSpeed;
    /**车轮车速*/
    private double wheelSpeed;
    /** gps速度 */
    private double gpsSpeed;
    /** 仪表盘速度 */
    private double speed;
    /**制动信号*/
    private int braking;
    /**档位信号*/
    private int gear;
    /**刹车开合状态*/
    private int brakeStatus;
    /**abs状态*/
    private int absStatus;
    /**经度*/
    private double lon;
    /**纬度*/
    private double lat;
    /**采集时间，毫秒数*/
    private long collTime;

    /**数据类型  区分abs测试区间数据（1） 和 其他数据（0）*/
    private int type;

    /**减速度*/
    private double lessSpeed;

    private int absCount;

    public ABSCheckRecord(String vid,
                          double sensorSpeed,
                          double wheelSpeed,
                          double gpsSpeed,
                          double speed,
                          int braking,
                          int gear,
                          int brakeStatus,
                          int absStatus,
                          double lon,
                          double lat,
                          long collTime) {
        this.vid = vid;
        this.sensorSpeed = sensorSpeed;
        this.wheelSpeed = wheelSpeed;
        this.gpsSpeed = gpsSpeed;
        this.speed = speed;
        this.braking = braking;
        this.gear = gear;
        this.brakeStatus = brakeStatus;
        this.absStatus = absStatus;
        this.lon = lon;
        this.lat = lat;
        this.collTime = collTime;
    }

    public void setLessSpeed(double lessSpeed) {
        this.lessSpeed = lessSpeed;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void increAbsCount(int count){
        this.absCount += count;
    }


    public int encodedLength(){
       return Encoders.Strings.encodedLength(vid)
                + Encoders.Strings.encodedLength(String.valueOf(sensorSpeed))
                + Encoders.Strings.encodedLength(String.valueOf(wheelSpeed))
                + Encoders.Strings.encodedLength(String.valueOf(gpsSpeed))
                + Encoders.Strings.encodedLength(String.valueOf(speed))
                + Encoders.Strings.encodedLength(String.valueOf(braking))
                + Encoders.Strings.encodedLength(String.valueOf(gear))
                + Encoders.Strings.encodedLength(String.valueOf(brakeStatus))
                + Encoders.Strings.encodedLength(String.valueOf(absStatus))
                + Encoders.Strings.encodedLength(String.valueOf(lon))
                + Encoders.Strings.encodedLength(String.valueOf(lat))
                + Encoders.Strings.encodedLength(String.valueOf(collTime));
    }

    public byte[] encode(){

        ByteBuf buf = Unpooled.buffer(encodedLength());
        Encoders.Strings.encode(buf, vid);
        Encoders.Strings.encode(buf, String.valueOf(sensorSpeed));
        Encoders.Strings.encode(buf, String.valueOf(wheelSpeed));
        Encoders.Strings.encode(buf, String.valueOf(gpsSpeed));
        Encoders.Strings.encode(buf, String.valueOf(speed));
        Encoders.Strings.encode(buf, String.valueOf(braking));
        Encoders.Strings.encode(buf, String.valueOf(gear));
        Encoders.Strings.encode(buf, String.valueOf(brakeStatus));
        Encoders.Strings.encode(buf, String.valueOf(absStatus));
        Encoders.Strings.encode(buf, String.valueOf(lon));
        Encoders.Strings.encode(buf, String.valueOf(lat));
        Encoders.Strings.encode(buf, String.valueOf(collTime));

        return buf.nioBuffer().array();
    }

    public static ABSCheckRecord decode(byte[] byteBuffer){

        ABSCheckRecord record = null;
        if(byteBuffer != null){
            ByteBuf buf = Unpooled.wrappedBuffer(byteBuffer);
            record = new ABSCheckRecord(
                    Encoders.Strings.decode(buf),

                    Encoders.Strings.decode2Double(buf),
                    Encoders.Strings.decode2Double(buf),
                    Encoders.Strings.decode2Double(buf),
                    Encoders.Strings.decode2Double(buf),

                    Encoders.Strings.decode2Int(buf),
                    Encoders.Strings.decode2Int(buf),
                    Encoders.Strings.decode2Int(buf),
                    Encoders.Strings.decode2Int(buf),

                    Encoders.Strings.decode2Double(buf),
                    Encoders.Strings.decode2Double(buf),
                    Encoders.Strings.decode2Long(buf));
        }
        return record;
    }


    public static ABSCheckRecord buildFromMap(String vid, Map<String, Object> record) throws Exception{

        ABSCheckRecord absCheckRecord = null;
        if(record != null){

            String sensorSpeedStr = getMapValue(record, AbsConstant.ABS_SENSOR_SPEED_NAME);
            double sensorSpeed = Short.parseShort(sensorSpeedStr) * 0.1;

            String wheelSpeedStr = getMapValue(record, AbsConstant.ABS_WHEEL_SPEED_NAME);
            double wheelSpeed = Short.parseShort(wheelSpeedStr) * 0.1;

            String gpsSpeedStr = getMapValue(record, "speed");
            double gpsSpeed = Short.parseShort(gpsSpeedStr) * 0.1;

            String SpeedStr = getMapValue(record, "speed1");
            double speed = Short.parseShort(SpeedStr) * 0.1;

            String brakingStr = getMapValue(record, AbsConstant.ABS_BRAKING_NAME);
            int braking = Integer.parseInt(brakingStr);

            String gearStr = getMapValue(record, AbsConstant.ABS_GEAR_NAME);
            int gear = Integer.parseInt(gearStr);

            String brakeStatusStr = getMapValue(record, AbsConstant.ABS_BRAKE_STATUS_NAME);
            int brakeStatus = Integer.parseInt(brakeStatusStr);

            String absStatusStr = getMapValue(record, AbsConstant.ABS_ABS_STATUS_NAME);
            int absStatus = Integer.parseInt(absStatusStr);

            double lon = Double.parseDouble(getMapValue(record, AbsConstant.ABS_LON_NAME)) / 1000000;
            double lat = Double.parseDouble(getMapValue(record, AbsConstant.ABS_LAT_NAME)) / 1000000;

            String collTime = getMapValue(record, "collTime");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");

            absCheckRecord = new ABSCheckRecord(vid,
                    sensorSpeed,
                    wheelSpeed,
                    gpsSpeed,
                    speed,
                    braking,
                    gear,
                    brakeStatus,
                    absStatus,
                    lon,
                    lat,
                    simpleDateFormat.parse(collTime).getTime());

        }

        return absCheckRecord;
    }

    private static String getMapValue(Map<String, Object> jsonMap, String key) {
        return jsonMap.get(key).toString();
    }

    public static void main(String[] args) throws ParseException {
        String time = "180917084943062";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        System.out.println(simpleDateFormat.parse(time));
    }

}
