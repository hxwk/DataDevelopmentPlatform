import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yaxon.vn.nd.tas.common.BitParser;
import com.yaxon.vn.nd.tas.handler.PositionAndAlarmPH;
import com.yaxon.vn.nd.tas.util.ByteBufCustomTool;
import com.yaxon.vn.nd.tas.util.X0200BitParse;
import com.yaxon.vn.nd.tbp.si.GpsVo;
import com.yaxon.vn.nd.tbp.si.Req_0704;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.bcd2Str;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.readTime;

/**
 * Created by jian on 2018/1/31.
 */
public class TestX02001 {
    PositionAndAlarmPH alarm = new PositionAndAlarmPH();
    static final Logger logger = LoggerFactory.getLogger(TestX02001.class);
    public static void main(String[] args) {
        /*String hexWord = "00 00 01 00 00 0C 00 00 02 62 5A 06 06 EA 05 41 00 00 00 00 00 00 13 08 06 09 18 39 01 04 00 00 00 00 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 00 00 30 01 11 31 01 00 E1 02 00 06 E2 04 00 00 11 C6";
        ByteBufCustomTool bbt = new ByteBufCustomTool();
        ByteBuf bb = bbt.hexStringToByteBuf(hexWord);
        do_0200(bb);*/
        String hexWord = "00 00 00 00 00 0C 00 01 01 FE 7D 5E 06 97 39 6A 00 00 00 00 00 00 18 03 19 09 52 33 01 04 00 17 AA 35 02 02 00 3D 03 02 02 58 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C E1 30 01 1A 31 01 06 E1 02 01 17 E2 04 03 A7 E9 4E";
        ByteBufCustomTool bbt = new ByteBufCustomTool();
        ByteBuf bb = bbt.hexStringToByteBuf(hexWord);
        //databuf2Bytes(bb);
        object2Bytes();
        /*do_0200(bb);
        Map<String,String> ss = new HashMap();
        ss.toString();*/
    }

    private static class DataBufs{
        int sim;
        ByteBuf dataBuf;
        byte[] dataBufBytes;

        public byte[] getDataBufBytes() {
            return dataBufBytes;
        }

        public void setDataBufBytes(byte[] dataBufBytes) {
            this.dataBufBytes = dataBufBytes;
        }

        public int getSim() {
            return sim;
        }

        public void setSim(int sim) {
            this.sim = sim;
        }

        public ByteBuf getDataBuf() {
            return dataBuf;
        }

        public void setDataBuf(ByteBuf dataBuf) {
            this.dataBuf = dataBuf;
        }

        @Override
        public String toString() {
            return "DataBufs{" +
                    "sim=" + sim +
                    ", dataBuf=" + dataBuf +
                    '}';
        }
    }

    private static void object2Bytes(){
        String hexWord = "00 00 00 00 00 0C 00 01 01 FE 7D 5E 06 97 39 6A 00 00 00 00 00 00 18 03 19 09 52 33 01 04 00 17 AA 35 02 02 00 3D 03 02 02 58 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C E1 30 01 1A 31 01 06 E1 02 01 17 E2 04 03 A7 E9 4E";
        ByteBufCustomTool bbt = new ByteBufCustomTool();
        ByteBuf bb = bbt.hexStringToByteBuf(hexWord);

        DataBufs dataBufs = new DataBufs();
        dataBufs.setSim(1234567899);
        dataBufs.setDataBuf(bb);
        if (bb.hasArray()) {
            dataBufs.setDataBufBytes(bb.array());
        }
        byte[] body = JSON.toJSONString(dataBufs).getBytes();
        //收到字节数组转成对象获取ByteBuf
        if(JSON.parseObject(body,DataBufs.class) instanceof DataBufs){
        TestX02001.DataBufs dataObj = JSON.parseObject(body, DataBufs.class);
            ByteBuf hexword = Unpooled.copiedBuffer(dataObj.getDataBufBytes());
            do_0200(hexword);
        }
    }

    private static void databuf2Bytes(ByteBuf data){
        ByteBuf dd = data;
        do_0200(dd);
        byte[] arrs=null;
        if (data.hasArray()){
            arrs = data.array();
        }
        ByteBuf hexword = Unpooled.copiedBuffer(arrs);
        do_0200(hexword);
    }

    private static void do_0200(final ByteBuf bb) {
        byte[] bytes;
        GpsVo gps;
        //异常驾驶行为告警
        List<Object> abnormalData;
        List<String> abnormalDrivingType;
        Short abnormalDrivingDegree;

        //解析上行请求协议
        try {
            ByteBuf reqBuf = bb;
            gps = new GpsVo();
            //告警
            int alarm = reqBuf.readInt();
            gps.setAlarm(alarm);
            if (0 != alarm) {
                gps.setAlarms(X0200BitParse.parseAlarm(alarm));
            }
            //车辆状态
            int vstate = reqBuf.readInt();
            gps.setState(vstate);
            if (0 != vstate) {
                gps.setVehicleStatus(X0200BitParse.getStateDesp(vstate));
            }
            gps.setLat(reqBuf.readInt());
            gps.setLon(reqBuf.readInt());
            gps.setAlt(reqBuf.readShort());
            gps.setSpeed(reqBuf.readShort());
            gps.setDir(reqBuf.readShort());
            gps.setGpsTime(readTime(reqBuf.readBytes(6)));

            List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
            while (reqBuf.readableBytes() > 0) {
                short extraInfoId = reqBuf.readUnsignedByte();
                if (extraInfoId == 0x01) {//里程
                    reqBuf.skipBytes(1);
                    gps.setMile(reqBuf.readInt());
                } else if (extraInfoId == 0x02) {//油耗
                    reqBuf.skipBytes(1);
                    int shortValue = reqBuf.readUnsignedShort();
                    gps.setFuel(shortValue / 100.0f);
                } else if (extraInfoId == 0x03) {//行驶记录仪速度
                    reqBuf.skipBytes(1);
                    gps.setSpeed1(reqBuf.readShort());
                } else if (extraInfoId == 0x18) { //异常驾驶行为报警
                    reqBuf.skipBytes(1);
                    if (reqBuf.readableBytes() > 0) {
                        ByteBuf tmpBuf = reqBuf.readBytes(3);
                        //异常驾驶行为类型 判空
                        abnormalData = BitParser.parseAbnormalDrivingData(tmpBuf);
                        if (abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData) {
                            abnormalDrivingType = (List<String>) abnormalData.get(0);
                            gps.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                        }
                        //异常驾驶行为程度 判空
                        if (abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData) {
                            abnormalDrivingDegree = (Short) abnormalData.get(1);
                            //判断是不是有效值
                            if (abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100) {
                                gps.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
                            } else {
                                logger.warn("不是有效异常驾驶行为程度值");
                            }
                        }
                    }
                } else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                    reqBuf.skipBytes(1);
                    int signalState = reqBuf.readInt();
                    gps.setSignalState(signalState);
                    if (0 != signalState) {
                        gps.setSignalStates(X0200BitParse.parseVehicleStatus(signalState));
                    }
                } else if (extraInfoId == 0x2A) {//IO状态位
                    reqBuf.skipBytes(1);
                    gps.setIoState(reqBuf.readShort());
                } else if (extraInfoId == 0xE1) { //电平电压
                    reqBuf.skipBytes(1);
                    gps.setBatteryVoltage(reqBuf.readShort());
                } else if (extraInfoId == 0xE2) { //累计油耗
                    reqBuf.skipBytes(1);

                    long cumulativeOilCons = reqBuf.readUnsignedInt();
                    gps.setCumulativeOilConsumption(cumulativeOilCons);
                } else if (extraInfoId == 0xF1) { //总计油耗
                    reqBuf.skipBytes(1);
                    gps.setTotalFuelConsumption(reqBuf.readUnsignedInt());
                } else if (extraInfoId == 0xF2) { //油箱液位
                    reqBuf.skipBytes(1);
                    gps.setHydraulicTank(reqBuf.readUnsignedByte());
                } else if (extraInfoId == 0xF3) { //车载重量
                    reqBuf.skipBytes(1);
                    gps.setVehicleWeight(reqBuf.readUnsignedShort());
                } else if (extraInfoId == 0xF4) { //0位:CAN状态 1位：油量液位状态
                    reqBuf.skipBytes(1);
                    gps.setCanAndHydraulicTankStatus(reqBuf.readUnsignedInt());
                } else {
                    GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                    item.setId(extraInfoId);
                    int byteLength = reqBuf.readUnsignedByte();
                    bytes = new byte[byteLength];
                    item.setData(bytes);
                    reqBuf.skipBytes(byteLength);
                    extraInfoItems.add(item);
                }
            }
            System.out.println(" gps: "+gps.toString());
        }catch (Exception e) {
            logger.warn("协议解析失败:" , e);
            return;
        }
        System.out.println();
    }

    public static void do_0704(ByteBuf byteBuf){
        Req_0704 q = new Req_0704();
        //解析上行请求协议
        try {
            q.setSim("12345678901");
            q.setVid("12345678901");
            List<GpsVo> gpsList = Lists.newArrayList();
            GpsVo gps;
            ByteBuf msgData = byteBuf;
            int dataCount = msgData.readUnsignedShort();
            q.setPositionDataType(msgData.readByte());
            for (int i = 0; (i < dataCount)&&(msgData.readableBytes()>28); i++) {
                short dataLen = msgData.readShort();
                if (dataLen > 0) {
                    gps = new GpsVo();
                    int startReadIndex = msgData.readerIndex();
                    gps.setAlarm(msgData.readInt());
                    int cState = msgData.readInt();
                    gps.setState(cState);
                    gps.setLat(msgData.readInt());
                    gps.setLon(msgData.readInt());
                    gps.setAlt(msgData.readShort());
                    gps.setSpeed(msgData.readShort());
                    gps.setDir(msgData.readShort());
                    gps.setGpsTime(readTime(msgData.readBytes(6)));
                    //gps.setSim(upMsg.sim);
                    List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
                    while (dataLen > msgData.readerIndex() - startReadIndex) {
                        int extraInfoId = msgData.readUnsignedByte();
                        if (extraInfoId == 0x01) {//里程
                            msgData.skipBytes(1);
                            gps.setMile(msgData.readInt());
                        } else if (extraInfoId == 0x02) {//油耗
                            msgData.skipBytes(1);
                            int shortValue = msgData.readUnsignedShort();
                            gps.setFuel(shortValue / 100.0f);
                        } else if (extraInfoId == 0x03) {//行驶记录仪速度
                            msgData.skipBytes(1);
                            gps.setSpeed1(msgData.readShort());
                        } else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                            msgData.skipBytes(1);
                            gps.setSignalState(msgData.readInt());
                        } else if (extraInfoId == 0x2A) {//IO状态位
                            msgData.skipBytes(1);
                            gps.setIoState(msgData.readShort());
                        } else if(extraInfoId ==0xE1){ //电平电压
                            msgData.skipBytes(1);
                            gps.setBatteryVoltage(msgData.readShort());
                        }else if(extraInfoId == 0xE2) { //累计油耗
                            msgData.skipBytes(1);
                            gps.setCumulativeOilConsumption(msgData.readUnsignedInt());
                        }else if(extraInfoId == 0xF1){ //总计油耗
                            msgData.skipBytes(1);
                            gps.setTotalFuelConsumption(msgData.readUnsignedInt());
                        }else if(extraInfoId == 0xF2){ //油箱液位
                            msgData.skipBytes(1);
                            gps.setHydraulicTank(msgData.readUnsignedByte());
                        }else if(extraInfoId == 0xF3) { //车载重量
                            msgData.skipBytes(1);
                            gps.setVehicleWeight(msgData.readUnsignedShort());
                        }else if(extraInfoId == 0xF4){ //0位:CAN状态 1位：油量液位状态
                            msgData.skipBytes(1);
                            gps.setCanAndHydraulicTankStatus(msgData.readUnsignedInt());
                        }else {
                            GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                            item.setId(extraInfoId);
                            int byteLength = msgData.readUnsignedByte();
                            byte[] bytes = new byte[byteLength];
                            item.setData(bytes);
                            msgData.skipBytes(byteLength);
                            extraInfoItems.add(item);
                        }
                    }
                    gps.setExtraInfoItems(extraInfoItems);
                    gpsList.add(gps);
                }
            }
            q.setGpsVos(gpsList);
        } catch (Exception e) {
            logger.warn("协议解析失败:", e);
            return;
        }
    }


    public void test(){
        String msg = "0911232200";
        ByteBufCustomTool tool = new ByteBufCustomTool();
        ByteBuf bb = tool.hexStringToByteBuf(msg);
        String time = readTimeCAN(bb).toString();
        Date date = null;
        try {
            date = DateUtils.parseDate(time,"HH:mm:ss.SSS");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("time "+ date);
    }
    public static Long readTimeCAN(ByteBuf buf) {
        try {
            byte[] data = new byte[5];
            buf.readBytes(data);
            String t = bcd2Str(data);
            logger.info("readTimeCAN t = " + t);
            Calendar now = Calendar.getInstance();
            int nowHour = now.get(Calendar.HOUR_OF_DAY);
            Calendar canDate = Calendar.getInstance();
            canDate.setTimeInMillis(new SimpleDateFormat("HHmmssSSS").parse(t).getTime());
            if (nowHour < canDate.get(Calendar.HOUR_OF_DAY)) {//此时是凌晨的时候可能存在跨天的情况
                now.add(Calendar.DATE, -1);
            }

            now.set(Calendar.HOUR_OF_DAY, canDate.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, canDate.get(Calendar.MINUTE));
            now.set(Calendar.SECOND, canDate.get(Calendar.SECOND));
            now.set(Calendar.MILLISECOND, canDate.get(Calendar.MILLISECOND));

            return now.getTimeInMillis();
        } catch (Exception e) {
            throw new RuntimeException("解析BCD时间异常", e);
        }
    }
}
