package com.dfssi.dataplatform.plugin.tcpnesource.handler;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.newen.entity.Req_01;
import com.dfssi.dataplatform.datasync.model.newen.entity.Req_04;
import com.dfssi.dataplatform.datasync.model.newen.entity.Req_05;
import com.dfssi.dataplatform.datasync.model.newen.entity.Req_06;
import com.dfssi.dataplatform.plugin.tcpnesource.common.Constants;
import com.dfssi.dataplatform.plugin.tcpnesource.common.GeodeTool;
import com.dfssi.dataplatform.plugin.tcpnesource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.plugin.tcpnesource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.plugin.tcpnesource.util.ProtoUtil;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.PlatformDTO;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TerminalHandler extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TerminalHandler.class);

    private Map<String, String> vin2CompanyMap = new HashMap<>();
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        //车辆登入
        if (upMsg.commandSign == 0x01) {
            do_01(upMsg, taskId, channelProcessor);
        }
        else if (upMsg.commandSign == 0x04) {//车辆登出
            do_04(upMsg, taskId, channelProcessor);
        }
        else if (upMsg.commandSign == 0x05) {//平台登入
            do_05(upMsg, taskId, channelProcessor);
        }
        else if (upMsg.commandSign == 0x06) {//平台登出
            do_06(upMsg, taskId, channelProcessor);
        }
        else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {

    }


    /**
     * 终端登入
     * @param upMsg
     * @return
     */
    private void do_01(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
//        ByteBuf msgBody = Unpooled.buffer(upMsg.bytes.length);
//        msgBody.writeBytes(upMsg.bytes);
        ByteBuf msgBody = upMsg.dataBuf;
        Req_01 req_01 = translateVehicleLoginMessage(msgBody);
        if (null == req_01 || StringUtils.isBlank(req_01.getIccid())) {
            logger.warn("{}：平台登入报文解析结果为空，验证不通过", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        req_01.setVin(upMsg.vin);
        req_01.setCommandSign(String.valueOf(upMsg.commandSign));
        req_01.setStatus(0);

        // 验证车辆信息
        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);
        if (null == vehicleDTO || StringUtils.isBlank(vehicleDTO.getVehicleCompany())) {
            logger.warn("{}：车辆登入，未匹配到车辆或者车企信息，验证不通过", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        //验证iccid是否一致,暂时不处理
        req_01.setStatus(1);
        req_01.setVehicleCompany(vehicleDTO.getVehicleCompany());
        req_01.setVehicleType(vehicleDTO.getVehicleType());
        buildEvent(req_01, taskId, channelProcessor, upMsg.vin, upMsg.commandSign, Constants.NE_VECHILE_LOGIN_TOPIC);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);


    }

    /**
     * 终端登出
     * @param upMsg
     * @return
     */
    private void do_04(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
//        ByteBuf msgBody = Unpooled.buffer(upMsg.bytes.length);
//        msgBody.writeBytes(upMsg.bytes);

        ByteBuf msgBody = upMsg.dataBuf;
        Req_04 req_04 = translateVehicleLogoutMessage(msgBody);

        if (null == req_04 || null == req_04.getSn()) {
            logger.warn("{}:车辆登出报文内容不完整", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        req_04.setVin(upMsg.vin);
        req_04.setCommandSign(String.valueOf(upMsg.commandSign));

        //添加车辆信息
        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);
        if (null == vehicleDTO || StringUtils.isBlank(vehicleDTO.getVehicleCompany())) {
            logger.warn("{}：车辆登出未匹配到车辆或者车企信息，验证不通过", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        req_04.setVehicleCompany(vehicleDTO.getVehicleCompany());
        req_04.setVehicleType(vehicleDTO.getVehicleType());
        buildEvent(req_04, taskId, channelProcessor, upMsg.vin, upMsg.commandSign, Constants.NE_VECHILE_LOGIN_TOPIC);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);


    }

    /**
     * 平台登入
     * @param upMsg
     * @return
     */
    private void do_05(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        logger.info("TerminalHandler do_05 doUpMsg = " + upMsg);

//        ByteBuf msgBody = Unpooled.buffer(upMsg.bytes.length);
//        msgBody.writeBytes(upMsg.bytes);
        ByteBuf msgBody = upMsg.dataBuf;
        Req_05 req_05 = translatePlatformLoginMessage(msgBody);
        if (null == req_05 || StringUtils.isBlank(req_05.getPassword())) {
            logger.warn("{}：平台登入报文解析结果为空，验证不通过", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        req_05.setVin(upMsg.vin);
        req_05.setCommandSign(String.valueOf(upMsg.commandSign));

//        //添加车辆信息
//        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);
//        if (null == vehicleDTO || StringUtils.isBlank(vehicleDTO.getVehicleCompany())) {
//            logger.warn("{}：未匹配到车企信息，验证不通过", upMsg.vin);
//
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
//
//            return;
//        }

        // 根据车企匹配用户名密码是否正确
        String companyName = validatePlatformInfo(upMsg, req_05);
        if (StringUtils.isBlank(companyName)) {
            logger.warn("{}：平台登入，未匹配到平台或者车企信息，验证不通过", upMsg.vin);
            req_05.setStatus(0);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            return;
        }else{
            req_05.setStatus(1);
        }

        vin2CompanyMap.put(upMsg.vin, companyName);

        req_05.setVehicleCompany(companyName);

        buildEvent(req_05, taskId, channelProcessor, upMsg.vin, upMsg.commandSign, Constants.NE_PLATFORM_LOGIN_TOPIC);
        logger.info("平台登入成功！开始发送返回报文！");
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        logger.info("平台登入成功！发送返回报文完成！");
    }

    /**
     * 验证车企的平台登入用户名密码是否正确
     * @param upMsg
     * @param req_05
     */
    private String validatePlatformInfo(ProtoMsg upMsg, Req_05 req_05) {
        req_05.setStatus(0);

        Region region = null;

        Object objList = null;
        String companyName = null;
        try {

            region = GeodeTool.getRegeion(Constants.REGION_PLATFORMINFO);

            StringBuilder sqlBuf = new StringBuilder();
            sqlBuf.append("select * from /");
            sqlBuf.append(Constants.REGION_PLATFORMINFO);
            sqlBuf.append(" where userName = '");
            sqlBuf.append(req_05.getUsername());
            sqlBuf.append("' and password = '");
            sqlBuf.append(req_05.getPassword());
            sqlBuf.append("' limit 1");

            logger.debug(" REGION_PLATFORMINFO region sql: " + sqlBuf.toString());
            objList = region.query(sqlBuf.toString());

            if (objList instanceof ResultsBag) {
                Iterator iter = ((ResultsBag) objList).iterator();

                while (iter.hasNext()) {
                    PlatformDTO platformDTO = (PlatformDTO) iter.next();
                    companyName = platformDTO.getVehicleCompany();
                }
            }
        } catch (Exception e) {
            logger.error("查询geode出错", e);
        }
        if (companyName==null){
            companyName ="东风性能测试";
        }
        return companyName;
    }


    /**
     * 解析平台登入报文
     * @param msgBody
     * @return
     */
    private Req_05 translatePlatformLoginMessage(ByteBuf msgBody) {
        Req_05 req_05 = new Req_05();

        if (msgBody.readableBytes() <= 0) {
            logger.warn("平台登入报文内容为空");

            return null;
        }

        try {
            req_05.setLoginTime(ProtoUtil.readTimeNE(msgBody.readBytes(6)));
            req_05.setSn(msgBody.readShort());
            req_05.setUsername(ProtoUtil.readString(msgBody, 12));
            req_05.setPassword(ProtoUtil.readString(msgBody, 20));
            req_05.setEncryptRule(msgBody.readByte());
        } catch (Exception e) {
            logger.error(null, e);

            return null;
        }

        return req_05;
    }

    /**
     * 解析平台登出报文
     * @param msgBody
     * @return
     */
    private Req_06 translatePlatformLogoutMessage(ByteBuf msgBody) {
        Req_06 req_06 = new Req_06();

        if (msgBody.readableBytes() <= 0) {
            logger.warn("平台登出报文内容为空");

            return null;
        }

        try {
            req_06.setLogoutTime(ProtoUtil.readTimeNE(msgBody.readBytes(6)));
            req_06.setSn(msgBody.readShort());
        } catch (Exception e) {
            logger.error(null, e);

            return null;
        }

        return req_06;
    }

    /**
     * 解析车辆登入报文
     * @param msgBody
     * @return
     */
    private Req_01 translateVehicleLoginMessage(ByteBuf msgBody) {
        Req_01 req_01 = new Req_01();

        if (msgBody.readableBytes() <= 0) {
            logger.warn("车辆登入报文内容为空");

            return null;
        }

        try {
            req_01.setLoginTime(ProtoUtil.readTimeNE(msgBody.readBytes(6)));
            req_01.setSn(msgBody.readShort());
            req_01.setIccid(ProtoUtil.readString(msgBody, 20));
            req_01.setChargeableChildSystemNum(msgBody.readByte());
            req_01.setChargeableSystemCodeLength(msgBody.readByte());
            int len = msgBody.readableBytes();
            req_01.setChargeableSystemCode(ProtoUtil.readString(msgBody, len));
        } catch (Exception e) {
            logger.error(null, e);

            return null;
        }

        return req_01;
    }

    /**
     * 解析平台登出报文
     * @param msgBody
     * @return
     */
    private Req_04 translateVehicleLogoutMessage(ByteBuf msgBody) {
        Req_04 req_04 = new Req_04();

        if (msgBody.readableBytes() <= 0) {
            logger.warn("车辆登出报文内容为空");

            return null;
        }

        try {
            req_04.setLogoutTime(ProtoUtil.readTimeNE(msgBody.readBytes(6)));
            req_04.setSn(msgBody.readShort());
        } catch (Exception e) {
            logger.error(null, e);

            return null;
        }

        return req_04;
    }

    /**
     * 平台登出
     * @param upMsg
     * @return
     */
    private void do_06(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
//        ByteBuf msgBody = Unpooled.buffer(upMsg.bytes.length);
//        msgBody.writeBytes(upMsg.bytes);

        ByteBuf msgBody = upMsg.dataBuf;

        Req_06 req_06 = translatePlatformLogoutMessage(msgBody);

//        logger.info(" do_06 = " + req_06);

        if (null == req_06 || null == req_06.getSn()) {
            logger.warn("{}:平台登出报文内容不完整", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        req_06.setVin(upMsg.vin);
        req_06.setCommandSign(String.valueOf(upMsg.commandSign));

        //添加车辆信息
//        VehicleDTO vehicleDTO = getVehicleInfo(upMsg);
//        String company = vehicleDTO.getVehicleCompany();
//
//        if (null == vehicleDTO || StringUtils.isBlank(vehicleDTO.getVehicleCompany())) {
//            logger.warn("{}：平台登出未匹配到车企信息，验证不通过", upMsg.vin);
//
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
//
//            return;
//        }

        String company = vin2CompanyMap.get(upMsg.vin);

        if (StringUtils.isBlank(company)) {
            logger.warn("{}：平台登出未匹配到车企信息，验证不通过", upMsg.vin);

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            return;
        }

        req_06.setVehicleCompany(company);
//        req_06.setStatus
        buildEvent(req_06, taskId, channelProcessor, upMsg.vin, upMsg.commandSign, Constants.NE_PLATFORM_LOGIN_TOPIC);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);


    }


    @Override
    public void setup() {

    }
}
