package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.utils.UUIDUtil;
import com.dfssi.dataplatform.datasync.plugin.interceptor.common.X0200BitParse;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * GpsVo 位置基本信息数据格式 （位置基本信息+部分附加信息）
 * @author jianKang
 * @date 2017/12/14
 */
public class GpsVo implements Serializable{
    SimpleDateFormat sdf ;

    private String id;
    /**
     * get message time
     */
    private long getMsgTime;

    private String msgId="0200";
    /**
     * sim卡
     */
    private long sim=0;
    /**
     * 报警标志
     */
    private long alarmCode =0;
    /**
     * 状态位
     */
    private long state=Long.MIN_VALUE;
    /**
     * 经度
     */
    private long lon=0;
    /**
     * 纬度
     */
    private long lat=0;
    /**
     * 高程
     */
    private int alt=0;
    /**
     * 速度
     */
    private int speed=0;
    /**
     * 方向
     */
    private int dir=0;
    /**
     * gps时间
     */
    private Date gpsTime= null;
    /**
     * 里程
     */
    private long mile=0;
    /**
     * 油量
     */
    private Float fuel=0.00f;
    /**
     * 行驶记录仪速度
     */
    private int speed1=0;
    /**
     * 信号状态
     */
    private long signalState=0;
    /*
    车辆扩展状态
    */
    private List<String> signalStates;
    /**
     * IO状态位
     */
    private int ioState=0;
    /**
     * 千里眼协议上传标识 1：是
     */
    private byte fromQly=0;
    /**
     * 电子运单内容
     */
    private String content= StringUtils.EMPTY;

    /**
     * 电源电压
     */
    private double supplyVoltage =0.00f;

    /**
     * 累计油耗
     */
    private double cumulativeOilConsumption = 0L;

    /**
     * 报警信息列表
     */
    private List<String> alarms ;

    /**
     * 车辆状态列表
     */
    private List<String> vehicleStatus;

    /**
     * 人工确认告警
     */
    private int artificialConfirmedAlarm;

    /**
     * 模拟量，bit0-15，AD0;
     *       bit16-31，AD1
     */
    private long analog;

    /**
     * 无线通信网络信号强度
     */
    private short signalStrength;

    /**
     * GNSS 定位卫星数
     */
    private short positioningSatelliteNumber;

    private long totalFuelConsumption; //总计油耗

    private int batteryVoltage; //电瓶电压

    private int hydraulicTank; //油箱液压

    private float vehicleWeight; //车载重量

    private long canAndHydraulicTankStatus; //can and 油箱液压状态
    //驾驶行为异常警报类型
    private List<String> abnormalDrivingBehaviorAlarmType;
    //驾驶行为异常警报程度
    private Short abnormalDrivingBehaviorAlarmDegree;

    public GpsVo() {
        //x0200BitParse = new X0200BitParse();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public String getMsgId() {
        return msgId==null?"0200":msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getGetMsgTime() {
        return getMsgTime==0? System.currentTimeMillis():getMsgTime;
    }

    public void setGetMsgTime(long getMsgTime) {
        this.getMsgTime = getMsgTime;
    }

    public List<String> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<String> alarms) {
        this.alarms = alarms;
    }

    public List<String> getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(List<String> vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte getFromQly() {
        return fromQly;
    }

    public void setFromQly(byte fromQly) {
        this.fromQly = fromQly;
    }

    public long getMile() {
        return mile;
    }

    public void setMile(long mile) {
        this.mile = mile;
    }

    public Float getFuel() {
        return fuel;
    }

    public void setFuel(Float fuel) {
        this.fuel = fuel;
    }

    public int getSpeed1() {
        return speed1;
    }

    public void setSpeed1(int speed1) {
        this.speed1 = speed1;
    }

    public long getSignalState() {
        return signalState;
    }

    public void setSignalState(long signalState) {
        this.signalState = signalState;
    }
    public List<String> getSignalStates() {
        return signalStates;
    }

    public void setSignalStates(List<String> signalStates) {
        this.signalStates = signalStates;
    }

    public int getIoState() {
        return ioState;
    }

    public void setIoState(int ioState) {
        this.ioState = ioState;
    }

    private List<ExtraInfoItem> extraInfoItems;

    public String getId() {
        return id==null?UUIDUtil.getRandomUuidByTrim():id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public long getAlarmCode() {
        if(alarmCode == 0){
            alarmCode = 0;
        }
        return alarmCode;
    }

    public void setAlarmCode(long alarmCode) {
        this.alarmCode = alarmCode;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public List<ExtraInfoItem> getExtraInfoItems() {
        return extraInfoItems;
    }

    public void setExtraInfoItems(List<ExtraInfoItem> extraInfoItems) {
        this.extraInfoItems = extraInfoItems;
    }

    public double getSupplyVoltage() {
        return supplyVoltage;
    }

    public void setSupplyVoltage(double supplyVoltage) {
        this.supplyVoltage = supplyVoltage;
    }

    public double getCumulativeOilConsumption() {
        return cumulativeOilConsumption;
    }

    public void setCumulativeOilConsumption(double cumulativeOilConsumption) {
        this.cumulativeOilConsumption = cumulativeOilConsumption;
    }

    public int getArtificialConfirmedAlarm() {
        return artificialConfirmedAlarm;
    }

    public void setArtificialConfirmedAlarm(int artificialConfirmedAlarm) {
        this.artificialConfirmedAlarm = artificialConfirmedAlarm;
    }

    public long getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public int getAlt() {
        return alt;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    public long getAnalog() {
        return analog;
    }

    public void setAnalog(long analog) {
        this.analog = analog;
    }

    public short getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(short signalStrength) {
        this.signalStrength = signalStrength;
    }

    public short getPositioningSatelliteNumber() {
        return positioningSatelliteNumber;
    }

    public void setPositioningSatelliteNumber(short positioningSatelliteNumber) {
        this.positioningSatelliteNumber = positioningSatelliteNumber;
    }

    public long getTotalFuelConsumption() {
        return totalFuelConsumption;
    }

    public void setTotalFuelConsumption(long totalFuelConsumption) {
        this.totalFuelConsumption = totalFuelConsumption;
    }

    public int getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(int batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public int getHydraulicTank() {
        return hydraulicTank;
    }

    public void setHydraulicTank(int hydraulicTank) {
        this.hydraulicTank = hydraulicTank;
    }

    public float getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(float vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }

    public long getCanAndHydraulicTankStatus() {
        return canAndHydraulicTankStatus;
    }

    public void setCanAndHydraulicTankStatus(long canAndHydraulicTankStatus) {
        this.canAndHydraulicTankStatus = canAndHydraulicTankStatus;
    }

    public List<String> getAbnormalDrivingBehaviorAlarmType() {
        return abnormalDrivingBehaviorAlarmType;
    }

    public void setAbnormalDrivingBehaviorAlarmType(List<String> abnormalDrivingBehaviorAlarmType) {
        this.abnormalDrivingBehaviorAlarmType = abnormalDrivingBehaviorAlarmType;
    }

    public Short getAbnormalDrivingBehaviorAlarmDegree() {
        return abnormalDrivingBehaviorAlarmDegree;
    }

    public void setAbnormalDrivingBehaviorAlarmDegree(Short abnormalDrivingBehaviorAlarmDegree) {
        this.abnormalDrivingBehaviorAlarmDegree = abnormalDrivingBehaviorAlarmDegree;
    }

    public static class ExtraInfoItem implements Serializable {
        private int id;
        private byte[] data;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ExtraInfoItem{" +
                    "id=" + id +
                    ", data=L" + (data == null ? 0 : data.length) +
                    '}';
        }
    }

    @Override
    public String toString() {
        String timestamp = (gpsTime!=null?String.valueOf(gpsTime.getTime()):String.valueOf(0L));
        alarms = X0200BitParse.parseAlarm(alarmCode);
        vehicleStatus = X0200BitParse.getStateDesp(state);
        //String res=null;
        try {
            /*res = "Location[" +
                    "id=" + id +
                    ", sim=" + sim +
                    ", alarms=" + alarms +
                    ", vehicleStatus=" + vehicleStatus +
                    ", lon=" + lon +
                    ", lat=" + lat +
                    ", alt=" + alt +
                    ", speed=" + speed +
                    ", dir=" + dir +
                    ", gpsTime=" + timestamp +
                    ", mile=" + mile +
                    ", fuel=" + fuel +
                    ", gpsSpeed=" + speed1 +
                    ", signalState=" + signalState +
                    ", ioState=" + ioState +
                    ", fromQly=" + fromQly +
                    ", content=" + content +
                    ", extraInfoItems=" + extraInfoItems +
                    "]";*/

        }catch (Exception ex){
            System.err.print(ex.getMessage());
        }
        //return res;
        return JSON.toJSONString(this);
    }
}
