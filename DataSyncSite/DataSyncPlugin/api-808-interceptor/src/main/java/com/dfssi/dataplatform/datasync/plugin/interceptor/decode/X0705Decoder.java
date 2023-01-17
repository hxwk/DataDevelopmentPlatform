package com.dfssi.dataplatform.datasync.plugin.interceptor.decode;

import com.dfssi.dataplatform.datasync.common.utils.ByteBufUtils;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.CanBusParamItem;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.interceptor.canbean.AnalyzeBean;
import com.dfssi.dataplatform.datasync.plugin.interceptor.canbean.AnalyzeSignal;
import com.dfssi.dataplatform.datasync.plugin.interceptor.common.DBCFormat;
import com.dfssi.dataplatform.datasync.plugin.interceptor.dbcparse.T38CanParse;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.interceptor.common.ProtoUtil.readTime1;

/**
 * x0704 protocol message body parse
 * @author jianKang
 * @date 2017/12/16
 */
public class X0705Decoder {
    static final Logger logger = LoggerFactory.getLogger(X0705Decoder.class);
    static final String dbcFormat = DBCFormat.OLD.name();
    ByteBufUtils byteBufUtils = new ByteBufUtils();
    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * CAN 总线数据上传
     * @param upMsg
     * @return
     */
    public byte[] do_0705(final ProtoMsg upMsg) {
        CanBusParamItem cpi;
        AnalyzeBean analyzeBean =null;
        //AnalyzeMsgBean analyzeMsgBean;
        //List<AnalyzeMsgBean> analyzeMsgBeanList=Lists.newArrayList();
        List<AnalyzeSignal> analyzeSignals = Lists.newArrayList();
        T38CanParse t38CanParse = new T38CanParse();
        //解析上行请求协议
        try{
            analyzeBean = new AnalyzeBean();
            String msgBody = new String(upMsg.getDataBuf());
            ByteBuf msgData = byteBufUtils.hexStringToByteBuf(msgBody);
            int dataCount = msgData.readUnsignedShort();
            logger.info("数据项个数 "+dataCount);
            analyzeBean.setItemNum(dataCount);
            Date receiveTime = readTime1(msgData.readBytes(5));
            analyzeBean.setReceiveTime(receiveTime.getTime());
            logger.info("数据接收时间 "+ DateFormatUtils.format(receiveTime,DATEFORMAT));
            int itemNum = 0;
            while(msgData.readableBytes()>=12 && itemNum<=dataCount){
                cpi = new CanBusParamItem();
                String canID = Integer.toHexString(msgData.readInt());
                String canData = Long.toHexString(msgData.readLong());
                logger.info("CanId: "+canID);
                logger.info("CanData: "+canData);
                cpi.setCanId(canID);
                cpi.setCanData(canData);
                analyzeSignals.addAll(t38CanParse.getAnalyzeSignalList(cpi, dbcFormat));
                //analyzeSignals = t38CanParse.getAnalyzeSignalList(cpi, dbcFormat);
                /*if(analyzeMsgBean != null){
                    analyzeMsgBeanList.add(analyzeMsgBean);
                }*/
                itemNum++;
                logger.info("itemNum:{}",itemNum);
            }
        }catch (Exception ex){
            logger.error("消息体解析错误:{}", ex.getMessage());
        }finally{
            analyzeBean.setMsgId(String.valueOf(upMsg.getMsgId()));
            analyzeBean.setMessageBeanList(analyzeSignals);
        }
        return analyzeBean.toString().getBytes();
    }
}
