package com.dfssi.dataplatform.plugin.tcpnesource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.newen.entity.*;
import com.dfssi.dataplatform.plugin.tcpnesource.common.Constants;
import com.dfssi.dataplatform.plugin.tcpnesource.common.NEStateCode2Name;
import com.dfssi.dataplatform.plugin.tcpnesource.common.NEStateConstant;
import com.dfssi.dataplatform.plugin.tcpnesource.exception.ParseException;
import com.dfssi.dataplatform.plugin.tcpnesource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.plugin.tcpnesource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.plugin.tcpnesource.util.ProtoUtil;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.dfssi.dataplatform.plugin.tcpnesource.common.ByteBufUtils.bytes2ShortArr;

/**
 * @author JianKang
 * @description ʵʱ���߲�����Ϣ�ϱ�
 */
public class InformationHandler extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(InformationHandler.class);
    private static final byte realCommandSign = 0x02;
    private static final byte addCommandSign = 0x03;
    private static long collecteTime;
    private static short informationType;
    private static String inforType;
    private static int hasValidNum = Integer.MIN_VALUE;//��ǰ��Ч������
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    //KafkaDataTrans kafka = new KafkaDataTrans();
    //�ն��ϴ����ݵ�ʱ���һ�ݸ�����kafka��Topic
    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        /*��ʱ��������ʱ�ַ���kafkaTopic �����ı���ȱ�ٰ�ͷ�Ͱ�β1
        ByteBuf msg = upMsg.dataBuf;
        msg.markReaderIndex();
        int count = msg.readableBytes();
        byte[] out = new byte[count];
        msg.readBytes(out);
        msg.resetReaderIndex();
        String body = hex.encode(out);
        logger.info("��kafka������Ϣbody��"+body);
        kafka.sendMessageToKafka(body);*/

        /*ByteBuf msg = upMsg.dataBuf;
        msg.markReaderIndex();
        int count = msg.readableBytes();
        byte[] out = new byte[count];
        msg.readBytes(out);
        msg.resetReaderIndex();
        logger.info("����������ccccccccccccccccc"+hex.encode(out));*/
        //ʵʱ��Ϣ�ϱ�
        if (upMsg.commandSign == realCommandSign) {
            do_02(upMsg, taskId, channelProcessor);
        }//������Ϣ�ϱ�
        else if (upMsg.commandSign == addCommandSign) {
            do_03(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("δ֪������������Ϣ��msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {

    }

    /**
     * ʵʱ��Ϣ�ϱ�
     *
     * @param upMsg
     * @return
     */
    private void do_02(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        do_realDataOrAddData(upMsg, taskId, channelProcessor);
    }

    /**
     * ������Ϣ�ϱ�
     *
     * @param upMsg
     * @return
     */
    private void do_03(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        do_realDataOrAddData(upMsg, taskId, channelProcessor);
    }

    /**
     * ����ʵʱ���ݺ͸����ϴ�����
     *
     * @param upMsg
     */
    private void do_realDataOrAddData(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        //String parseContent = do_realDataOrAddData(upMsg);
        //modify by JIANKANG 2018-10-08 16:02
        //push msg body bytes to sink-kafka for parse data

        //��ȡ����ͳ�����Ϣ
        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);

        if (null == vehicleDTO) {
            logger.warn("���������ڣ��޷��������ݣ�{}", upMsg.vin);
            //���������ڷ���ʧ��
            sendCenterGeneralRes(upMsg,ProtoConstants.RC_FAIL);
        }else {
            if (upMsg.dataBuf.readableBytes() > 0) {
                logger.debug("����channel����ʵʱ����=====================" + ByteBufUtil.hexDump(upMsg.dataBuf));
                processEvent(Unpooled.copiedBuffer(upMsg.dataBuf).array(), taskId, upMsg.commandSign, Constants.NE_VECHILE_DATA_TOPIC, upMsg.vin, channelProcessor);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
            }
        }
//        //��ȡ����ͳ�����Ϣ
//        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);
//
//        if (null == vehicleDTO) {
//            logger.warn("���������ڣ��޷��������ݣ�{}", upMsg.vin);
//            //���������ڷ���ʧ��
//            sendCenterGeneralRes(upMsg,ProtoConstants.RC_FAIL);
//        }else {
//            logger.debug("upMsg:{},taskId:{},vehicleDTO:{}", upMsg.toString(), taskId,vehicleDTO.toString());
//            String parseContent = do_realDataOrAddData(upMsg);
//            logger.debug("parse parseContent:{}",parseContent.toString());
//            //processEvent(parseContent.getBytes(),taskId,upMsg.commandSign,Constants.NE_VECHILE_DATA_TOPIC,upMsg.vin,channelProcessor);
//            processEvent(parseContent.getBytes(),taskId,upMsg.commandSign,Constants.NE_VECHILE_DATA_TOPIC,upMsg.vin,channelProcessor);
//            //buildEvent(upMsg, taskId, channelProcessor, Constants.NE_VECHILE_DATA_TOPIC);
//            //���������ն�Ӧ��ɹ�
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
//        }
    }


    @Override
    public void setup() {

    }

    /**
     * ����ʵʱ���ݺ͸����ϴ�����
     *
     * @param upMsg
     */
    private String do_realDataOrAddData(final ProtoMsg upMsg) {
        Req_02 req_02 = new Req_02();
        ByteBuf msgBody = upMsg.dataBuf;
        req_02.setCommandSign(String.valueOf(upMsg.commandSign));
        req_02.setVin(upMsg.vin);
        req_02.setReceiveTime(System.currentTimeMillis());
        ParseException.clearSet();

        //��ȡ����ͳ�����Ϣ
        logger.debug("getVehicleInfo(upMsg):{}", getVehicleInfo(upMsg));
        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);
        if (vehicleDTO == null) {
//            vehicleDTO = new VehicleDTO();
//            vehicleDTO.setVin(upMsg.vin);
            logger.warn("���������ڣ��޷��������ݣ�{}", upMsg.vin);
            //���������ڷ���ʧ��
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            return null;
        }
        req_02.setVehicleCompany(vehicleDTO.getVehicleCompany());
        req_02.setVehicleType(vehicleDTO.getVehicleType());
        //�ɼ�ʱ��
        if (msgBody.readableBytes() > 0) {
            collecteTime = ProtoUtil.readTimeNE(msgBody.readBytes(6));
            Date date = new Date();
            date.setTime(collecteTime);
            req_02.setCollectTime(collecteTime);
        } else {
            logger.debug("REAL OR UNREAL DATA INVALID");
        }
        while (msgBody.readableBytes() > 1) {
            //��Ϣ���ͱ�ʶ
            try {
                informationType = Byte.parseByte(String.valueOf(msgBody.readByte()), 16);
            } catch (Exception ex) {
                logger.warn("InformationPh:information type read error:{}", ex.getMessage());
            }
            //��Ϣ����
            try {
                inforType = NEStateCode2Name.hex2String(informationType);
            } catch (Exception ex) {
                logger.warn("convert hex information Type error:{}", ex.getMessage());
            }

            switch (inforType) {
                case NEStateConstant.VEHICLEDATA://1.�������ݽ���
                    do_vehicleData(req_02, msgBody);
                    break;
                case NEStateConstant.DRIVERMOTOR://2.����������ݽ���
                    do_driverMotorData(req_02, msgBody);
                    break;
                case NEStateConstant.FUELCELL://3.ȼ�ϵ�����ݽ���
                    do_fuelCellData(req_02, msgBody);
                    break;
                case NEStateConstant.MOTORDATA://4.�������������ݽ���
                    do_motorData(req_02, msgBody);
                    break;
                case NEStateConstant.NEGPS://5.����λ�����ݽ���
                    do_gpsData(req_02, msgBody);
                    break;
                case NEStateConstant.EXTREMUM://6.��ֵ���ݽ���
                    do_extremumData(req_02, msgBody);
                    break;
                case NEStateConstant.ALARM://7.�������ݽ���
                    do_alarmData(req_02, msgBody);
                    break;
                case NEStateConstant.ENERGYSTORAGEVOLTAGE://8.��索��װ�õ�ѹ����
                    do_chargeStorageVoltage(req_02, msgBody);
                    break;
                case NEStateConstant.ENERGYSTORAGETEMPERATURE://9.��索��װ�õ�ѹ�¶�
                    do_chargeStorageTemperature(req_02, msgBody);
                    break;
                default:
                    logger.debug("no match handle method!");
            }
        }

        //��Ч�����������
        req_02.setHasValidNum(hasValidNum);
        logger.debug("��Ч����������:{}", hasValidNum);
        //�Ƿ�����������
        logger.debug("����Դ������:{}", Constants.NUM_NE_DATA_VALUE);
        req_02.setIntact(hasValidNum >= Constants.NUM_NE_DATA_VALUE ? 1 : ((hasValidNum < Constants.NUM_NE_DATA_VALUE) && (hasValidNum > 0)
                ? -1 : 0));

        // updata by yanghs  ��������״̬��Ϣ��ʱû���ô�

        //���³�������״̬��redis
//        try {
//            updateVehicleStatus2Redis(upMsg.vin);
//        }catch (Exception ex){
//            logger.warn("���³�������״̬��redis:{}",ex);
//        }

        return JSON.toJSONString(getNeMsgObject(req_02));
    }

    private void do_chargeStorageVoltage(Req_02 req_02, ByteBuf msgBody) {
        Short storageSubSysNum = 0;
        List<Integer> cellVoltageList;
        NEChargeVoltage neChargeVoltage = new NEChargeVoltage();
        NEChargeVoltageBean neChargeVoltageBean;
        List<NEChargeVoltageBean> neChargeVoltList = Lists.newArrayList();
        neChargeVoltage.setChargeVoltageInformationType((short) 0x08);
        if (msgBody.readableBytes() > 0) {
            storageSubSysNum = msgBody.readUnsignedByte();
            neChargeVoltage.setStorageVoltageSubSysNum(storageSubSysNum);
            hasValidNum = ParseException.computerCount("storageSubSysNum", storageSubSysNum);
        }
        while (storageSubSysNum-- > 0 && msgBody.readableBytes() > 10) {
            neChargeVoltageBean = new NEChargeVoltageBean();
            cellVoltageList = Lists.newArrayList();
            neChargeVoltageBean.setStorageSubSysNo(msgBody.readUnsignedByte());
            hasValidNum = ParseException.computerCount("storageSubSysNo", neChargeVoltageBean.getStorageSubSysNo());
            neChargeVoltageBean.setStorageVoltage(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("storageVoltage", neChargeVoltageBean.getStorageVoltage());
            neChargeVoltageBean.setStorageCurrent(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("storageCurrent", neChargeVoltageBean.getStorageCurrent());
            neChargeVoltageBean.setCellTotal(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("cellTotal", neChargeVoltageBean.getCellTotal());
            neChargeVoltageBean.setSerailOfFrame(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("serailOfFrame", neChargeVoltageBean.getSerailOfFrame());
            short cellNumOfFrame = msgBody.readUnsignedByte();
            neChargeVoltageBean.setCellNumOfFrame(cellNumOfFrame);
            hasValidNum = ParseException.computerCount("cellNumOfFrame", cellNumOfFrame);
            while (cellNumOfFrame-- > 0) {
                if (msgBody.readableBytes() > 0) {
                    Integer tmp = msgBody.readUnsignedShort();
                    cellVoltageList.add(tmp);
                } else {
                    logger.warn("����ط���Ӧ��Ϊ�յ�,����������,���ݸ����͵����ѹ��������");
                    cellVoltageList.add(0);
                }
                hasValidNum = ParseException.computerCount("cellVoltageList", cellVoltageList.get(0));
            }
            neChargeVoltageBean.setCellVoltage(cellVoltageList);
            neChargeVoltList.add(neChargeVoltageBean);
        }
        neChargeVoltage.setNeChargeVoltageBeanList(neChargeVoltList);
        req_02.setNeChargeVoltage(neChargeVoltage);
    }

    private void do_chargeStorageTemperature(Req_02 req_02,ByteBuf msgBody){
        short storageSubSysNum=0;
        NEChargeTemp neChargeTemp = new NEChargeTemp();
        NEChargeTempBean neChargeTempBean ;
        List<NEChargeTempBean> neChargeTempList = Lists.newArrayList();
        neChargeTemp.setChargeTempInformationType((short) 0x09);
        if(msgBody.readableBytes()>0) {
            storageSubSysNum = msgBody.readUnsignedByte();
            neChargeTemp.setStorageTempSubSysNum(storageSubSysNum);
            hasValidNum = ParseException.computerCount("storageTempSubSysNum",storageSubSysNum);
        }
        while(storageSubSysNum-->0&&msgBody.readableBytes()>=3){
            int num = 0;
            neChargeTempBean = new NEChargeTempBean();
            neChargeTempBean.setStorageSubSerial(msgBody.readUnsignedByte());
            hasValidNum = ParseException.computerCount("storageSubSerial",neChargeTempBean.getStorageSubSerial());
            int tempProbeNum = msgBody.readUnsignedShort();
            neChargeTempBean.setStorageTempProbeNum(tempProbeNum);
            hasValidNum = ParseException.computerCount("tempProbeNum",tempProbeNum);
            int[] probes = new int[tempProbeNum];
            while (num < tempProbeNum) {
                probes[num++] = msgBody.readUnsignedByte();
            }
            neChargeTempBean.setStorageTempAllProbeNums(probes);
            if (probes.length == 0) {
                logger.debug("probes length is 0!");
            } else {
                hasValidNum = ParseException.computerCount("probes", probes[0]);
            }
            neChargeTempList.add(neChargeTempBean);
        }
        neChargeTemp.setNeChargeTempBeanList(neChargeTempList);
        req_02.setNeChargeTemp(neChargeTemp);
    }

    private void do_vehicleData(Req_02 req_02, ByteBuf msgBody) {
        ByteBuf vehicleData;
        if (msgBody.readableBytes() >= 20) {
            vehicleData = msgBody.readBytes(20);
            NEVehicleBean neVehicleBean = do_vehicle(vehicleData);
            req_02.setNeVehicleBean(neVehicleBean);
        } else {
            logger.debug("����������������������");
        }
    }

    private void do_driverMotorData(Req_02 req_02, ByteBuf msgBody) {
        short driverMotorNum = 0;
        NEDriverMotor neDriverMotor = new NEDriverMotor();

        //��ǰ����������ݴ���
        if (msgBody.readableBytes() > 0) {
            //�����������
            driverMotorNum = msgBody.readUnsignedByte();
            neDriverMotor.setDriverMotorNumber(driverMotorNum);
            hasValidNum = ParseException.computerCount("driverMotorNum", driverMotorNum);
        } else {
            logger.warn("�������ݲ���");
        }
        NEDriverMotor neDriverMotors = do_driverMotor(msgBody, driverMotorNum, neDriverMotor);
        req_02.setNeDriverMotor(neDriverMotors);
    }

    private void do_alarmData(Req_02 req_02, ByteBuf msgBody) {
        NEAlarmBean neAlarmBean = null;
        if (msgBody.readableBytes() >= 0) {
            neAlarmBean = do_alarm(msgBody);
        }
        req_02.setNeAlarmBean(neAlarmBean);
    }

    private void do_extremumData(Req_02 req_02, ByteBuf msgBody) {
        NEExtremumBean neExtremumBean = null;
        if (msgBody.readableBytes() >= 14) {
            neExtremumBean = do_extremum(msgBody);
        }
        req_02.setNeExtremumBean(neExtremumBean);
    }

    private void do_gpsData(Req_02 req_02, ByteBuf msgBody) {
        NEGpsBean neGpsBean = new NEGpsBean();
        neGpsBean.setGpsInformationType((short) 0x05);
        if (msgBody.readableBytes() >= 9) {
            short locationCode = msgBody.readUnsignedByte();
            //��λ״̬Code
            neGpsBean.setLocationCode(locationCode);
            hasValidNum = ParseException.computerCount("locationCode", locationCode);
            //��λ״̬
            neGpsBean.setLocations(NEStateCode2Name.locationStates(locationCode));
            //����
            long longitude = msgBody.readUnsignedInt();
            neGpsBean.setLongitude(longitude);
            hasValidNum = ParseException.computerCount("longitude", longitude);
            //γ��
            long latitude = msgBody.readUnsignedInt();
            neGpsBean.setLatitude(latitude);
            hasValidNum = ParseException.computerCount("latitude", latitude);
            logger.debug(latitude * Math.pow(10, -6) + "," + longitude * Math.pow(10, -6));
        }
        req_02.setNeGpsBean(neGpsBean);
    }

    private void do_motorData(Req_02 req_02, ByteBuf msgBody) {
        NEEngineBean neEngineBean = new NEEngineBean();
        neEngineBean.setEngineInformationType((short) 0x04);
        if (msgBody.readableBytes() >= 5) {
            //������״̬code
            short engineStateCode = msgBody.readUnsignedByte();
            neEngineBean.setEngineStateCode(engineStateCode);
            hasValidNum = ParseException.computerCount("engineStateCode", engineStateCode);
            //������״̬ �������ǹر�
            neEngineBean.setEngineState(NEStateCode2Name.motorState(engineStateCode));
            //����ת��
            int speedOfCrankshaft = msgBody.readUnsignedShort();
            hasValidNum = ParseException.computerCount("speedOfCrankshaft", speedOfCrankshaft);
            if (speedOfCrankshaft >= 0 && speedOfCrankshaft <= 60000) {
                neEngineBean.setSpeedOfCrankshaft(speedOfCrankshaft);
            } else {
                neEngineBean.setSpeedOfCrankshaft(speedOfCrankshaft);
            }
            //ȼ��������
            int specificFuelConsumption = msgBody.readUnsignedShort();
            if (specificFuelConsumption >= 0 && specificFuelConsumption <= 60000) {
                neEngineBean.setSpecificFuelConsumption(specificFuelConsumption);
                hasValidNum = ParseException.computerCount("specificFuelConsumption", specificFuelConsumption);
            } else {
                neEngineBean.setSpecificFuelConsumption(specificFuelConsumption);
            }
        }
        req_02.setNeEngineBean(neEngineBean);
    }

    private void do_fuelCellData(Req_02 req_02, ByteBuf msgBody) {
        ByteBuf fuelCellData;
        //����ȼ�ϵ�صĵ�ѹ ���� ������
        NEFuelCellBean neFuelCellBean = new NEFuelCellBean();

        //ȼ�ϵ�����ݴ���
        if (msgBody.readableBytes() > 0) {
            //ȼ�ϵ�ص�ѹ
            neFuelCellBean.setFuelCellVoltage(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("fuelCellVoltage", neFuelCellBean.getFuelCellVoltage());
            //ȼ�ϵ�ص���
            neFuelCellBean.setFuelCellCurrent(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("fuelCellCurrent", neFuelCellBean.getFuelCellCurrent());
            //ȼ��������
            neFuelCellBean.setRateOfFuelConsumption(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("rateOfFuelConsumption", neFuelCellBean.getRateOfFuelConsumption());

            //��ȡȼ�ϵ���¶�̽������
            /*msgBody.readBytes(probeNum);
            int probeN = bytes2OctNum(probeNum);*/
            int probeN = msgBody.readUnsignedShort();
            //ȼ�ϵ���¶�̽������
            neFuelCellBean.setFuelCellProbeNumber(probeN);
            hasValidNum = ParseException.computerCount("probeN", probeN);

            //�ж�ȼ�ϵ���¶�̽����������Χ probeN >= 0 && probeN < 65531
            if (probeN >= 0 && probeN <= 65531) {
                //ȼ�ϵ�����ݽ���
                if (msgBody.readableBytes() >= 10 + probeN) {
                    fuelCellData = msgBody.readBytes(10 + probeN);
                    NEFuelCellBean neFuelCellBean1 = do_fuelCell(fuelCellData, neFuelCellBean, probeN);
                    req_02.setNeFuelCellBean(neFuelCellBean1);
                }
            } else {
                fuelCellData = msgBody.readBytes(10 + 1);
                NEFuelCellBean neFuelCellBean1 = do_fuelCell(fuelCellData, neFuelCellBean, probeN);
                req_02.setNeFuelCellBean(neFuelCellBean1);
                logger.error("probe num is incorrect");
            }
        }
    }

    /**
     * ������������
     *
     * @param msgBody
     * @return NEVehicleBean
     */
    private NEVehicleBean do_vehicle(ByteBuf msgBody) {
        NEVehicleBean vehicleBean = new NEVehicleBean();
        vehicleBean.setVehicleInformationType((short) 0x01);
        //����״̬
        short vehicleStateCode = msgBody.readUnsignedByte();
        vehicleBean.setVehicleStatusCode(vehicleStateCode);
        vehicleBean.setVehicleStatus(NEStateCode2Name.vehicleState(vehicleStateCode));
        hasValidNum = ParseException.computerCount("vehicleStateCode", vehicleStateCode);

        //���״̬
        short chargeStateCode = msgBody.readUnsignedByte();
        vehicleBean.setChargingStatusCode(chargeStateCode);
        vehicleBean.setChargingStatus(NEStateCode2Name.chargeState(chargeStateCode));
        hasValidNum = ParseException.computerCount("chargeStateCode", chargeStateCode);

        //����ģʽ
        short runModeCode = msgBody.readUnsignedByte();
        vehicleBean.setRunModeCode(runModeCode);
        vehicleBean.setRunMode(NEStateCode2Name.runMode(runModeCode));
        hasValidNum = ParseException.computerCount("runModeCode", runModeCode);

        //����
        int speed = msgBody.readUnsignedShort();
        vehicleBean.setSpeed(speed);
        hasValidNum = ParseException.computerCount("speed", speed);

        //�ۼ����
        long accMile = msgBody.readUnsignedInt();
        vehicleBean.setAccumulativeMile(accMile);
        hasValidNum = ParseException.computerCount("accMile", accMile);

        //�ܵ�ѹ
        int totalVoltage = msgBody.readUnsignedShort();
        vehicleBean.setTotalVoltage(totalVoltage);
        hasValidNum = ParseException.computerCount("totalVoltage", totalVoltage);

        //�ܵ���
        int totalElec = msgBody.readUnsignedShort();
        vehicleBean.setTotalElectricity(totalElec);
        hasValidNum = ParseException.computerCount("totalElec", totalElec);

        //SOC
        short soc = msgBody.readUnsignedByte();
        vehicleBean.setSoc(soc);
        hasValidNum = ParseException.computerCount("soc", soc);

        //DC״̬
        short dcStateCode = msgBody.readUnsignedByte();
        vehicleBean.setDcStatusCode(dcStateCode);
        vehicleBean.setDcStatus(NEStateCode2Name.dcState(dcStateCode));
        hasValidNum = ParseException.computerCount("dcStateCode", dcStateCode);

        //��λ
        short gearCode = msgBody.readUnsignedByte();
        vehicleBean.setGearCode(gearCode);
        vehicleBean.setGears(NEStateCode2Name.gearStates(gearCode));
        hasValidNum = ParseException.computerCount("gearCode", gearCode);

        //��Ե����
        int insulationResistance = msgBody.readUnsignedShort();
        vehicleBean.setInsulationResistance(insulationResistance);
        hasValidNum = ParseException.computerCount("insulationResistance", insulationResistance);
        //����̤���г�ֵ
        short acceleratorPedal = msgBody.readUnsignedByte();
        vehicleBean.setAcceleratorPedal(acceleratorPedal);
        hasValidNum = ParseException.computerCount("acceleratorPedal", acceleratorPedal);
        //�ƶ�̤��״̬
        short brakePedalStatus = msgBody.readUnsignedByte();
        vehicleBean.setBrakePedalStatus(brakePedalStatus);
        hasValidNum = ParseException.computerCount("brakePedalStatus", brakePedalStatus);
        //Ԥ��
        //vehicleBean.setReserved(msgBody.readUnsignedShort());
        return vehicleBean;
    }

    /**
     * ���������������
     *
     * @param msgBody
     * @param driverMotorNum
     */
    private NEDriverMotor do_driverMotor(ByteBuf msgBody, short driverMotorNum, NEDriverMotor driverMotor) {
        int currentDriverMotorNo = 0;
        driverMotor.setDriveMotorInformationType((short) 0x02);
        NEDriverMotorBean neDriverMotorBean;
        //�洢��������ܳ���Ϣ�б�
        List<NEDriverMotorBean> neDriverMotorBeanList = Lists.newArrayList();
        //��������ܳ���Ϣ
        while ((currentDriverMotorNo < driverMotorNum) && msgBody.readableBytes() >= 12) {
            neDriverMotorBean = new NEDriverMotorBean();
            //��ǰ���
            currentDriverMotorNo++;
            //����������
            short driverMotorSerial = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorSerial(driverMotorSerial);
            hasValidNum = ParseException.computerCount("driverMotorSerial", driverMotorSerial);

            //�������״̬��
            short driverMotorStateCode = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorStateCode(driverMotorStateCode);
            hasValidNum = ParseException.computerCount("driverMotorStateCode", driverMotorStateCode);
            //�������״̬
            neDriverMotorBean.setDriverMotorState(NEStateCode2Name.driverMotor(driverMotorStateCode));
            //��������������¶�
            short driverMotorCT = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorControllerTemperature(driverMotorCT);
            hasValidNum = ParseException.computerCount("driverMotorCT", driverMotorCT);

            //�������ת��
            int driverMotorRPM = msgBody.readUnsignedShort();
            neDriverMotorBean.setDriverMotorRPM(driverMotorRPM);
            hasValidNum = ParseException.computerCount("driverMotorRPM", driverMotorRPM);

            //�������ת��
            int driverMotorTorque = msgBody.readUnsignedShort();
            neDriverMotorBean.setDriverMotorTorque(driverMotorTorque);
            hasValidNum = ParseException.computerCount("driverMotorTorque", driverMotorTorque);

            //��������¶�
            short driverMotorT = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorTemperature(driverMotorT);
            hasValidNum = ParseException.computerCount("driverMotorT", driverMotorT);

            //��������������ѹ
            int motorControllerInputV = msgBody.readUnsignedShort();
            neDriverMotorBean.setMotorControllerInputVoltage(motorControllerInputV);
            hasValidNum = ParseException.computerCount("motorControllerInputV", motorControllerInputV);

            //���������ֱ��ĸ�ߵ���
            int motorControllerNegativeDCC = msgBody.readUnsignedShort();
            neDriverMotorBean.setMotorControllerNegativeDCCurrent(motorControllerNegativeDCC);
            hasValidNum = ParseException.computerCount("motorControllerNegativeDCC", motorControllerNegativeDCC);

            //����ǰ��������ܳ���Ϣ����List�ڴ�
            neDriverMotorBeanList.add(neDriverMotorBean);
        }
        //����������ܳ���Ϣ�б�洢�����������
        driverMotor.setNEDriverMotorBeans(neDriverMotorBeanList);
        return driverMotor;
    }

    /**
     * ����ȼ�ϵ������
     *
     * @param msgBody
     */
    private NEFuelCellBean do_fuelCell(ByteBuf msgBody, NEFuelCellBean neFuelCellBean, int probeNumber) {
        neFuelCellBean.setFuelCellInformationType((short) 0x03);
        //̽���¶�ֵ�б�
        ByteBuf probeTemperatureBuf = msgBody.readBytes(probeNumber);
        neFuelCellBean.setProbeTemperatures(bytes2ShortArr(probeTemperatureBuf, probeNumber));
        hasValidNum = ParseException.computerCount("probeTemperatureBuf", 0);
        //��ϵͳ������¶�
        int maxTempInHydrogen = msgBody.readUnsignedShort();
        neFuelCellBean.setMaxTemperatureInHydrogenSystem(maxTempInHydrogen);
        hasValidNum = ParseException.computerCount("maxTempInHydrogen", maxTempInHydrogen);

        //��ϵͳ������¶�̽�����
        short maxTempProbeSerial = msgBody.readUnsignedByte();
        neFuelCellBean.setMaxTemperatureProbeSerial(maxTempProbeSerial);
        hasValidNum = ParseException.computerCount("maxTempProbeSerial", maxTempProbeSerial);

        //�������Ũ��
        int maxHydrogenConcentration = msgBody.readUnsignedShort();
        neFuelCellBean.setMaxHydrogenConcentration(maxHydrogenConcentration);
        hasValidNum = ParseException.computerCount("maxHydrogenConcentration", maxHydrogenConcentration);

        //�������Ũ�ȴ���������
        short maxHydrogenProbeSerial = msgBody.readUnsignedByte();
        neFuelCellBean.setMaxHydrogenConcentrationProbeSerial(maxHydrogenProbeSerial);
        hasValidNum = ParseException.computerCount("maxHydrogenProbeSerial", maxHydrogenProbeSerial);

        //�������ѹ��
        int maxPressureHydrogen = msgBody.readUnsignedShort();
        neFuelCellBean.setMaxPressureHydrogen(maxPressureHydrogen);
        hasValidNum = ParseException.computerCount("maxPressureHydrogen", maxPressureHydrogen);

        //�������ѹ������������
        short maxPressureHydrogenProbeSerial = msgBody.readUnsignedByte();
        neFuelCellBean.setMaxPressureHydrogenProbeSerial(maxPressureHydrogenProbeSerial);
        hasValidNum = ParseException.computerCount("maxPressureHydrogenProbeSerial", maxPressureHydrogenProbeSerial);

        //��ѹDC/DC״̬Code
        short dcState = msgBody.readUnsignedByte();
        neFuelCellBean.setHighPressDCStateCode(dcState);
        hasValidNum = ParseException.computerCount("dcState", dcState);

        //��ѹDC/DC״̬
        neFuelCellBean.setHighPressDCState(NEStateCode2Name.fuelCellDCState(dcState));
        return neFuelCellBean;
    }

    /**
     * ����ֵ����
     *
     * @param msgBody
     */
    private NEExtremumBean do_extremum(ByteBuf msgBody) {
        NEExtremumBean neExtremumBean = new NEExtremumBean();
        neExtremumBean.setExtremumInformationType((short) 0x06);
        //��ߵ�ѹ�����ϵͳ��
        short highVBatterySubNum = msgBody.readUnsignedByte();
        neExtremumBean.setHighVBatterySubNum(highVBatterySubNum);
        hasValidNum = ParseException.computerCount("highVBatterySubNum", highVBatterySubNum);

        //��ߵ�ѹ��ص������
        short highVBatteryCellCode = msgBody.readUnsignedByte();
        neExtremumBean.setHighVBatteryCellCode(highVBatteryCellCode);
        hasValidNum = ParseException.computerCount("highVBatteryCellCode", highVBatteryCellCode);

        //��ص����ѹ���ֵ
        int maximumBatteryVoltage = msgBody.readUnsignedShort();
        neExtremumBean.setMaximumBatteryVoltage(maximumBatteryVoltage);
        hasValidNum = ParseException.computerCount("maximumBatteryVoltage", maximumBatteryVoltage);

        //��͵�ѹ�����ϵͳ��
        short lowVBatterySubNum = msgBody.readUnsignedByte();
        neExtremumBean.setLowVBatterySubNum(lowVBatterySubNum);
        hasValidNum = ParseException.computerCount("lowVBatterySubNum", lowVBatterySubNum);

        //��͵�ѹ��ص������
        short lowVBatteryCellCode = msgBody.readUnsignedByte();
        neExtremumBean.setLowVBatteryCellCode(lowVBatteryCellCode);
        hasValidNum = ParseException.computerCount("lowVBatteryCellCode", lowVBatteryCellCode);

        //��ص����ѹ���ֵ
        int minimumBatteryVoltage = msgBody.readUnsignedShort();
        neExtremumBean.setMinimumBatteryVoltage(minimumBatteryVoltage);
        hasValidNum = ParseException.computerCount("minimumBatteryVoltage", minimumBatteryVoltage);

        //����¶���ϵͳ��
        short highTemperatureSubNum = msgBody.readUnsignedByte();
        neExtremumBean.setHighTemperatureSubNum(highTemperatureSubNum);
        hasValidNum = ParseException.computerCount("highTemperatureSubNum", highTemperatureSubNum);

        //����¶�̽�����
        short highTemperatureProbeSerial = msgBody.readUnsignedByte();
        neExtremumBean.setHighTemperatureProbeSerial(highTemperatureProbeSerial);
        hasValidNum = ParseException.computerCount("highTemperatureProbeSerial", highTemperatureProbeSerial);

        //����¶�ֵ
        short maxTemperatureValue = msgBody.readUnsignedByte();
        neExtremumBean.setMaxTemperatureValue(maxTemperatureValue);
        hasValidNum = ParseException.computerCount("maxTemperatureValue", maxTemperatureValue);

        //����¶���ϵͳ��
        short lowTemperatureSubnum = msgBody.readUnsignedByte();
        neExtremumBean.setLowTemperatureSubNum(lowTemperatureSubnum);
        hasValidNum = ParseException.computerCount("lowTemperatureSubnum", lowTemperatureSubnum);

        //����¶�̽�����
        short lowTemperatureValue = msgBody.readUnsignedByte();
        neExtremumBean.setLowTemperatureProbeSerial(lowTemperatureValue);
        hasValidNum = ParseException.computerCount("lowTemperatureValue", lowTemperatureValue);

        //����¶�ֵ
        short minTemperatureValue = msgBody.readUnsignedByte();
        neExtremumBean.setMinTemperatureValue(minTemperatureValue);
        hasValidNum = ParseException.computerCount("minTemperatureValue", minTemperatureValue);

        return neExtremumBean;
    }

    //�澯��Ϣ���ݴ���
    private NEAlarmBean do_alarm(ByteBuf msgBody) {
        NEAlarmBean neAlarmBean = new NEAlarmBean();
        neAlarmBean.setAlarmInformationType((short) 0x07);
        //�ɳ�索��װ�ù��ϴ����б�
        List<Long> rechargeableStorageCodeList = Lists.newArrayList();
        //����������ϴ����б�
        List<Long> driverMotorFailureCodeList = Lists.newArrayList();
        //���������ϴ����б�
        List<Long> engineFailureCodeList = Lists.newArrayList();
        //�������ϴ����б�
        List<Long> otherFailureCodeList = Lists.newArrayList();
        //��߱����ȼ�
        short maxAlarmRate = msgBody.readUnsignedByte();
        hasValidNum = ParseException.computerCount("maxAlarmRate", maxAlarmRate);
        if (maxAlarmRate >= 0 && maxAlarmRate <= 3) {
            neAlarmBean.setMaxAlarmRating(maxAlarmRate);
            neAlarmBean.setMaxAlarmRatingName(maxAlarmRate == 0 ? "�޹���" : (maxAlarmRate == 1 ? "1������" : (maxAlarmRate == 2 ? "2������" : "3������")));
        } else {
            neAlarmBean.setMaxAlarmRating(maxAlarmRate);
            neAlarmBean.setMaxAlarmRatingName(maxAlarmRate == 0 ? "�޹���" : (maxAlarmRate == 1 ? "1������" : (maxAlarmRate == 2 ? "2������" : "3������")));
            new ParseException(maxAlarmRate, "��߱����ȼ�");

        }
        //ͨ�ñ�����־
        long alarmIdentification = msgBody.readUnsignedInt();
        neAlarmBean.setAlarmIdentification(alarmIdentification);
        neAlarmBean.setAlarmIdentificationList(NEStateCode2Name.parseGeneralAlarms((alarmIdentification)));
        hasValidNum = ParseException.computerCount("alarmIdentification", alarmIdentification);

        //�ɳ�索��װ�ù�������N1
        short rechargeAbleN1 = msgBody.readUnsignedByte();
        neAlarmBean.setRechargeableStorageDeviceN1(rechargeAbleN1);
        hasValidNum = ParseException.computerCount("rechargeAbleN1", rechargeAbleN1);
        if (rechargeAbleN1 > 0 && rechargeAbleN1 <= 252) {
            while (rechargeAbleN1-- > 0) {
                rechargeableStorageCodeList.add(msgBody.readUnsignedInt());
            }
            //�ɳ�索��װ�ù��ϴ����б�
            neAlarmBean.setRechargeableStorageCodeList(rechargeableStorageCodeList);
            hasValidNum = ParseException.computerCount("rechargeableStorageCodeList", rechargeableStorageCodeList.get(0));
        } else {
            while (rechargeAbleN1-- > 0) {
                rechargeableStorageCodeList.add(msgBody.readUnsignedInt());
            }
            //�ɳ�索��װ�ù��ϴ����б�
            neAlarmBean.setRechargeableStorageCodeList(rechargeableStorageCodeList);
            new ParseException(rechargeAbleN1, "�ɳ�索��װ�ù�������N1");
        }

        //���������������N2
        short driverMotorfailN2 = msgBody.readUnsignedByte();
        neAlarmBean.setDriverMotorFailureN2(driverMotorfailN2);
        hasValidNum = ParseException.computerCount("driverMotorfailN2", driverMotorfailN2);

        if (driverMotorfailN2 > 0 && driverMotorfailN2 <= 252) {
            while (driverMotorfailN2-- > 0) {
                driverMotorFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //����������ϴ����б�
            neAlarmBean.setDriverMotorFailureCodeList(driverMotorFailureCodeList);
            hasValidNum = ParseException.computerCount("driverMotorFailureCodeList", driverMotorFailureCodeList.get(0));
        } else {
            while (driverMotorfailN2-- > 0) {
                driverMotorFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //����������ϴ����б�
            neAlarmBean.setDriverMotorFailureCodeList(driverMotorFailureCodeList);
            new ParseException(driverMotorfailN2, "���������������N2");
        }
        //��������������N3
        short engineFailN3 = msgBody.readUnsignedByte();
        neAlarmBean.setEngineFailureN3(engineFailN3);
        hasValidNum = ParseException.computerCount("engineFailN3", engineFailN3);
        if (engineFailN3 > 0 && engineFailN3 <= 252) {
            while (engineFailN3-- > 0) {
                engineFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //���������ϴ����б�
            neAlarmBean.setEngineFailureCodeList(engineFailureCodeList);
            hasValidNum = ParseException.computerCount("engineFailureCodeList", engineFailureCodeList.get(0));
        } else {
            while (engineFailN3-- > 0) {
                engineFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //���������ϴ����б�
            neAlarmBean.setEngineFailureCodeList(engineFailureCodeList);
            new ParseException(engineFailN3, "��������������N3");
        }
        //������������N4
        short otherFailN4 = msgBody.readUnsignedByte();
        neAlarmBean.setOtherFailureN4(otherFailN4);
        hasValidNum = ParseException.computerCount("otherFailN4", otherFailN4);

        if (otherFailN4 > 0 && otherFailN4 <= 252) {
            while (otherFailN4-- > 0) {
                otherFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //�������ϴ����б�
            neAlarmBean.setOtherFailureCodeList(otherFailureCodeList);
            hasValidNum = ParseException.computerCount("otherFailureCodeList", otherFailureCodeList.get(0));
        } else {
            while (otherFailN4-- > 0) {
                otherFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //�������ϴ����б�
            neAlarmBean.setOtherFailureCodeList(otherFailureCodeList);
            new ParseException(otherFailN4, "������������N4");
        }
        return neAlarmBean;
    }

    public NEMessage2KafkaBean getNeMsgObject(Req_02 req) {
        //��ȡ��������Ҫ��kafka�Ķ���
        NEMessage2KafkaBean neMsg = new NEMessage2KafkaBean();
        //collectTime
        neMsg.setCollectTime(collecteTime);
        //vin
        neMsg.setVin(req.getVin());
        neMsg.setVehicleType(req.getVehicleType());
        neMsg.setVehicleCompany(req.getVehicleCompany());
        //receiveTime
        neMsg.setReceiveTime(req.getReceiveTime());
        //��Ч������
        neMsg.setHasValidNum(req.getHasValidNum());
        //����������
        neMsg.setIntact(req.getIntact());
        //commandSign
        neMsg.setMsgId("32960_0" + req.getCommandSign());
        if (null != req.getNeVehicleBean() || null != req.getNeAlarmBean() || null != req.getNeDriverMotor()
                || null != req.getNeEngineBean() || null != req.getNeExtremumBean() || null != req.getNeFuelCellBean() ||
                null != req.getNeGpsBean()) {
            NEVehicleBean neVehicle = req.getNeVehicleBean();
            if (null != neVehicle) {
                neMsg.setNeVehicleBean(neVehicle);
            }
            //��ȡ�������ʵʱ����ʵ��
            NEDriverMotor neDriverMotor = req.getNeDriverMotor();
            if (null != neDriverMotor) {
                neMsg.setNeDriverMotor(neDriverMotor);
            }
            //��ȡȼ�ϵ��ʵʱ����ʵ��
            NEFuelCellBean neFuelCellBean = req.getNeFuelCellBean();
            if (null != neFuelCellBean) {
                neMsg.setNeFuelCellBean(neFuelCellBean);
            }
            //��ȡ������ʵʱ����ʵ��
            NEEngineBean neEngineBean = req.getNeEngineBean();
            if (null != neEngineBean) {
                neMsg.setNeEngineBean(neEngineBean);
            }
            //��ȡ����λ������ʵ��
            NEGpsBean neGpsBean = req.getNeGpsBean();
            if (null != neGpsBean) {
                neMsg.setNeGpsBean(neGpsBean);
            }
            //��ȡʵʱ��ֵ����ʵ��
            NEExtremumBean neExtremumBean = req.getNeExtremumBean();
            if (null != neExtremumBean) {
                neMsg.setNeExtremumBean(neExtremumBean);
            }
            //��ȡʵʱ��������ʵ��
            NEAlarmBean neAlarmBean = req.getNeAlarmBean();
            if (null != neAlarmBean) {
                neMsg.setNeAlarmBean(neAlarmBean);
            }
            //��ȡʵʱ�ɳ�索�ܵ�ѹ
            NEChargeVoltage neChargeVoltage = req.getNeChargeVoltage();
            if (null != neChargeVoltage) {
                neMsg.setNeChargeVoltage(neChargeVoltage);
            }
            //��ȡʵʱ�ɳ�索���¶�
            NEChargeTemp neChargeTemp = req.getNeChargeTemp();
            if (null != neChargeTemp) {
                neMsg.setNeChargeTemp(neChargeTemp);
            }
        }
        return neMsg;
    }
}
