package com.dfssi.dataplatform.datasync.plugin.source.tcp;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile.ByteBufCustomTool;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.D004DataParse;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.yaxon.vn.nd.tbp.si.GpsAndCanDataPkg;
import com.yaxon.vn.nd.tbp.si.GpsAndCanDataStructure;
import com.yaxon.vn.nd.tbp.si.Req_D004;
import com.yaxon.vn.nd.tbp.si.SecondDataPkg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;

/**
 * @author JianKang
 * @date 2018/5/31
 * @description
 */
public class testD004 {
    private static final byte lowByte = 0x0F;
    static final Logger logger = LoggerFactory.getLogger(testD004.class);
    public static void main(String[] args) {
/*        String hexWord = "01 02 00 00 00 01 00 00 00 01 00 01 00 01 18 06 01 16 17 18 0A 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 00 00 01 00 00 00 01 00 01 00 01 18 06 01 16 17 18 0A 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01 00 01 00 01 00 01 00 01 01 01 01 01 01 00 01 00 01 00 01 01 01 01 00 01 01 00 01 00 01 00 01 01 00 01 01 00 00 00 01 01 00 01 01 00 01";
        ByteBufCustomTool bbt = new ByteBufCustomTool();
        ByteBuf bb = bbt.hexStringToByteBuf(hexWord);
        ProtoMsg upMsg = new ProtoMsg();
        upMsg.dataBuf = bb;
        do_D004(upMsg);*/


        BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);
        ByteBuf dataBuf=Unpooled.buffer(32);
       /* int speedOfRevolution = 800;
        speedOfRevolution *= 8;
        dataBuf.writeByte(speedOfRevolution&0x00FF);//转速 高字节
        dataBuf.writeByte((speedOfRevolution>>>8)&0x00FF);//转速 低字节*/

        dataBuf.writeByte(10+125);


        int count1 = dataBuf.readableBytes();
        byte[] outbuf = new byte[count1];
        dataBuf.readBytes(outbuf);
        System.err.println("ccc"+hex.encode(outbuf).toString());
    }

    private static void do_D004(ProtoMsg upMsg) {
        Req_D004 q = new Req_D004();
        GpsAndCanDataPkg gpsAndCanDataPkg;
        List<GpsAndCanDataPkg> gpsAndCanDataPkgs = Lists.newArrayList();
        SecondDataPkg seconDataPkg;
        List<SecondDataPkg> secondDataPgks;
        try{
            ByteBuf reqBuf = upMsg.dataBuf;
            GpsAndCanDataStructure gpsAndCan = new GpsAndCanDataStructure();
            gpsAndCan.setId(UUID.randomUUID().toString());
            gpsAndCan.setSim(upMsg.sim);
            gpsAndCan.setVid(upMsg.vid);
            gpsAndCan.setReceiveTime(System.currentTimeMillis());
            gpsAndCan.setMsgId("D004");
            gpsAndCan.setVersion(reqBuf.readUnsignedByte());
            int primaryPkgNum = reqBuf.readUnsignedByte();
            gpsAndCan.setPrimaryPkgNum(primaryPkgNum);
            //read data package
            while(reqBuf.readableBytes()>0 && primaryPkgNum-->0){
                gpsAndCanDataPkg = new GpsAndCanDataPkg();
                gpsAndCanDataPkg.setLatitude(reqBuf.readUnsignedInt());
                gpsAndCanDataPkg.setLongitute(reqBuf.readUnsignedInt());
                gpsAndCanDataPkg.setHeight(reqBuf.readUnsignedShort());
                gpsAndCanDataPkg.setDirection(reqBuf.readUnsignedShort());
                Long dateTime = readTime(reqBuf.readBytes(6)).getTime();
                gpsAndCanDataPkg.setDataTime(dateTime);
                int secondPkgNum = reqBuf.readUnsignedByte()&lowByte;
                logger.debug("secondPkgNum:{}",secondPkgNum);
                gpsAndCanDataPkg.setSecondPkgNum(secondPkgNum);
                secondDataPgks = Lists.newArrayList();
                while(reqBuf.readableBytes()>0 && secondPkgNum-->0) {
                    seconDataPkg = new SecondDataPkg();
                    seconDataPkg.setRpm(reqBuf.readUnsignedShort());
                    seconDataPkg.setInstrumentSpeed(reqBuf.readUnsignedShort());
                    seconDataPkg.setWheelSpeed(reqBuf.readUnsignedShort());
                    seconDataPkg.setGpsSpeed(reqBuf.readUnsignedShort());
                    seconDataPkg.setThrottleOpening(reqBuf.readUnsignedByte());
                    seconDataPkg.setPercentagetorque(reqBuf.readUnsignedByte());
                    Short switchCodes = reqBuf.readUnsignedByte();
                    seconDataPkg.setSwitchsCode(switchCodes);
                    seconDataPkg.setSwitchStatus(D004DataParse.getStatus(switchCodes));
                    seconDataPkg.setCurrentBlock(reqBuf.readUnsignedByte());
                    seconDataPkg.setTargetGear(reqBuf.readUnsignedByte());
                    seconDataPkg.setEngineFuelRate(reqBuf.readUnsignedShort());
                    seconDataPkg.setGrade(reqBuf.readUnsignedShort());
                    seconDataPkg.setLoad(reqBuf.readUnsignedShort());
                    seconDataPkg.setFuelLevel(reqBuf.readUnsignedByte());
                    seconDataPkg.setWaterTemp(reqBuf.readUnsignedByte());
                    seconDataPkg.setBarometricPressure(reqBuf.readUnsignedByte());
                    seconDataPkg.setIntakeAirTemp(reqBuf.readUnsignedShort());
                    seconDataPkg.setAirTemp(reqBuf.readUnsignedByte());
                    seconDataPkg.setExhaustTemp(reqBuf.readUnsignedShort());
                    seconDataPkg.setIntakeQGpressure(reqBuf.readUnsignedShort());
                    seconDataPkg.setRelativePress(reqBuf.readUnsignedShort());
                    seconDataPkg.setEngineTorqueMode(reqBuf.readUnsignedByte());
                    seconDataPkg.setOilPressure(reqBuf.readUnsignedShort());
                    seconDataPkg.setUreaLevel(reqBuf.readUnsignedByte());
                    seconDataPkg.setStateFlag(reqBuf.readUnsignedInt());
                    seconDataPkg.setBrakePedalOpen(reqBuf.readUnsignedByte());
                    seconDataPkg.setGpsDir(reqBuf.readUnsignedShort());
                    seconDataPkg.setAirCompressorStatus(reqBuf.readUnsignedByte());
                    seconDataPkg.setTransmissionOutputSpeed(reqBuf.readUnsignedShort());
                    secondDataPgks.add(seconDataPkg);

                }
                gpsAndCanDataPkg.setSecondDataPkgList(secondDataPgks);
                gpsAndCanDataPkgs.add(gpsAndCanDataPkg);
            }
            gpsAndCan.setPkgs(gpsAndCanDataPkgs);
            q.setGpsAndCan(gpsAndCan);
            System.out.println(JSON.toJSONString(q));
        }catch (Exception e){
            logger.warn("协议解析失败：:{},E:{}",upMsg,e);

            return;
        }
    }
}
