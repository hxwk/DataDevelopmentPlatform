package com.dfssi.dataplatform.datasync.plugin.interceptor.dbcparse;

import com.dfssi.dataplatform.datasync.common.utils.ByteBufUtils;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.CanBusParamItem;
import com.dfssi.dataplatform.datasync.plugin.interceptor.canbean.AnalyzeMsgBean;
import com.dfssi.dataplatform.datasync.plugin.interceptor.canbean.AnalyzeSignal;
import com.dfssi.dataplatform.datasync.plugin.interceptor.canbean.MessageBean;
import com.dfssi.dataplatform.datasync.plugin.interceptor.canbean.SignalBean;
import com.dfssi.dataplatform.datasync.plugin.interceptor.common.DBCFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * x0705 CAN T38 Can protocol parse Message class
 * @author jianKang
 * @date 2017/12/22
 */
public class T38CanParse {
    static final Logger logger = LoggerFactory.getLogger(T38CanParse.class);
    static Queue<String> getValidT38Data = null;
    private static final String ONE = "1";
    private static final String ZERO = "0";
    private static final String BOSTANT="BO_";
    private static final String SGCONSTANT="SG_";
    private static final String LEFTBRACKET="(";
    private static final String RIGHTBRACKET=")";
    private static final String COMMA=",";
    private static final String AT="@";
    private static final String VERTICALBAR="\\|";
    private static final long CANDISTANST = 0x1FFFFFFF;

    /**
     * 解析DBC配置文件到list
     */
    public T38CanParse() {
        getValidT38Data = new T38DBCFileParse().readValidData();
    }

    /**
     * get message define and values by EMPTY
     * @return items list
     */
    private List<String> getMsgDefAndValues(Queue<String> lines) {
        List<String> msgDefAndValues = Lists.newArrayList();
        /**
         * get list by delimite EMPTY
         */
        for (String msg : lines) {
            if (msg != null && !msg.equals(StringUtils.EMPTY)) {
                List<String> itemList = Arrays.asList(msg.split(StringUtils.SPACE));
                for (String msgItem : itemList) {
                    msgDefAndValues.add(msgItem);
                }
            }
        }
        return msgDefAndValues;
    }

    /**
     * get map by message item list （new DBC File format）
     * @param msgItemList
     * @return map
     */
    private Map<MessageBean, List<SignalBean>> getT38MsgComputerNew(List<String> msgItemList) {
        Map<MessageBean, List<SignalBean>> t38MsgMap = Maps.newConcurrentMap();
        MessageBean msgBean = null;
        List<SignalBean> signalBeanList = Lists.newArrayList();
        SignalBean signalBean;
        int appear = 0;
        try {
            for (int idx = 0; idx < msgItemList.size(); idx++) {
                if (msgItemList.get(idx).equals(BOSTANT)) {
                    if (appear == 0) {
                        appear++;
                    } else if (appear == 1) {
                        t38MsgMap.put(msgBean, signalBeanList);
                        signalBeanList = Lists.newArrayList();
                    }
                    /**
                     * The extended
                     CAN ID can be determined by masking out the most significant bit with the
                     mask 0xCFFFFFFF.
                     */
                    msgBean = new MessageBean();
                    String message_id = String.valueOf(Long.parseLong(msgItemList.get(++idx)));
                    msgBean.setMessage_id(message_id);
                    msgBean.setMessage_name(msgItemList.get(++idx));
                    msgBean.setMessage_size(msgItemList.get(++idx));
                    msgBean.setTransmitter(msgItemList.get(++idx));

                } else if (SGCONSTANT.equals(msgItemList.get(idx))) {
                    signalBean = new SignalBean();
                    signalBean.setSignal_name(msgItemList.get(++idx));

                    ++idx; //:
                    String fourItem = msgItemList.get(++idx);
                    signalBean.setStart_bit(fourItem.split(VERTICALBAR)[0]);
                    signalBean.setSignal_size(fourItem.split(VERTICALBAR)[1].split(AT)[0]);
                    signalBean.setByte_order(String.valueOf(fourItem.split(AT)[1].charAt(0)));
                    signalBean.setValue_type(String.valueOf(fourItem.split(AT)[1].charAt(1)));
                    String fiveItem = msgItemList.get(++idx);
                    signalBean.setFactor(String.valueOf(fiveItem.split(COMMA)[0].replace(LEFTBRACKET, StringUtils.EMPTY)));
                    signalBean.setOffset(String.valueOf(fiveItem.split(COMMA)[1].replace(RIGHTBRACKET, StringUtils.EMPTY)));
                    signalBean.setNumScope(msgItemList.get(++idx));
                    signalBean.setUnit(msgItemList.get(++idx));
                    signalBean.setReceiver(msgItemList.get(++idx));
                    signalBeanList.add(signalBean);
                } else if (msgItemList.get(idx).equals(StringUtils.SPACE)) {
                    continue;
                }
            }
            /**
             * 将最后一个元素保存到map中
             */
            if (appear == 1 && signalBeanList.size() > 0) {
                t38MsgMap.put(msgBean, signalBeanList);
                appear--;
            }
        }catch (Exception ex){
            logger.error("DBC Format File parse error, file format not match,please check!");
        }
        return t38MsgMap;
    }

    /**
     * get map by message item list
     * @return map
     */
    private Map<MessageBean, List<SignalBean>> getT38MsgComputer(List<String> msgItems) {
        Map<MessageBean, List<SignalBean>> t38MsgComputer = Maps.newConcurrentMap();
        List<SignalBean> signalBeanList = Lists.newArrayList();
        SignalBean signalBean;
        MessageBean msgBean = null;
        int appear = 0;
        for (int idx = 0; idx < msgItems.size(); idx++) {
            if (msgItems.get(idx).equals(BOSTANT)) {
                if(appear==1){
                    t38MsgComputer.put(msgBean, signalBeanList);
                    signalBeanList = Lists.newArrayList();
                }else if(appear==0){
                    appear++;
                }
                /**
                 * The extended
                 CAN ID can be determined by masking out the most significant bit with the
                 mask 0xCFFFFFFF.
                 */
                msgBean = new MessageBean();
                String message_id = String.valueOf(Long.parseLong(msgItems.get(++idx)));
                msgBean.setMessage_id(message_id);
                msgBean.setMessage_name(msgItems.get(++idx));
                msgBean.setMessage_size(msgItems.get(++idx));
                msgBean.setTransmitter(msgItems.get(++idx));

            } else if (msgItems.get(idx).equals(SGCONSTANT)) {
                signalBean = new SignalBean();
                signalBean.setSignal_name(msgItems.get(++idx));

                ++idx;//:
                List<String> items = handleSignalItem(msgItems.get(++idx));
                signalBean.setStart_bit(items.get(0));
                signalBean.setSignal_size(items.get(1));
                signalBean.setByte_order(items.get(2));
                signalBean.setValue_type(items.get(3));
                signalBean.setFactor(items.get(4));
                signalBean.setOffset(items.get(5));
                signalBean.setNumScope(items.get(6));
                signalBean.setUnit(msgItems.get(++idx));
                signalBean.setReceiver(msgItems.get(++idx));
                signalBeanList.add(signalBean);
            } else if (msgItems.get(idx).equals(StringUtils.SPACE)) {
                continue;
            }
        }
        /**
         * 将最后一个元素保存到map中
         */
        if(appear==1&&signalBeanList.size()>0){
            t38MsgComputer.put(msgBean, signalBeanList);
            appear--;
        }
        return t38MsgComputer;
    }

    /**
     * complex string to signal item
     * @param signal
     * @return list signal item
     */
    private List<String> handleSignalItem(String signal) {
        List<String> itemList = Lists.newArrayList();
        itemList.add(signal.split(AT)[0].split(VERTICALBAR)[0]);
        itemList.add(signal.split(AT)[0].split(VERTICALBAR)[1]);
        itemList.add(String.valueOf(signal.split(AT)[1].split("\\(")[0].toCharArray()[0]));
        itemList.add(String.valueOf(signal.split(AT)[1].split("\\(")[0].toCharArray()[1]));
        itemList.add(String.valueOf(signal.split(AT)[1].split("\\(")[1].split("\\)")[0].split(COMMA)[0]));
        itemList.add(String.valueOf(signal.split(AT)[1].split("\\(")[1].split("\\)")[0].split(COMMA)[1]));
        itemList.add(String.valueOf(signal.split(AT)[1].split("\\(")[1].split("\\)")[1]));
        return itemList;
    }

    /**
     * get analyze message bean by 0705 by canMsg id
     * @param modelType:old and current. canBusParamItem:can bus param item
     * @return AnalyzeMsgBean
     */
    public List<AnalyzeSignal> getAnalyzeSignalList(CanBusParamItem canBusParamItem,String modelType) {
        double resultValue;
        AnalyzeSignal analyzeSignal;
        List<AnalyzeSignal> analyzeSignalList = Lists.newArrayList();
        /**
         * 解析出来DBC文件中有用的数据，按行显示 parse DBC file by line
         */
        List<String> msgDefAndValues = getMsgDefAndValues(getValidT38Data);
        Map<MessageBean, List<SignalBean>> t38Msg = null;
        try {
            if (DBCFormat.OLD.name().equals(modelType)) {
                t38Msg = getT38MsgComputer(msgDefAndValues);
            } else if (DBCFormat.CURRENT.name().equals(modelType)) {
                t38Msg = getT38MsgComputerNew(msgDefAndValues);
            }
            for (Map.Entry<MessageBean, List<SignalBean>> entries : t38Msg.entrySet()) {
                Long msgBeanCanId = Long.parseLong(entries.getKey().getMessage_id(), 10) & CANDISTANST;
                Long canBusItemCanId = Long.parseLong(canBusParamItem.getCanId(), 16) & CANDISTANST;

                if (msgBeanCanId.equals(canBusItemCanId)) {
                    for (SignalBean signalBean : entries.getValue()) {
                        analyzeSignal = new AnalyzeSignal();
                        resultValue = getResultValue(signalBean, canBusParamItem.getCanData());
                        analyzeSignal.setValue(resultValue);
                        analyzeSignal.setSignal_name(signalBean.getSignal_name());
                        analyzeSignal.setUnit(signalBean.getUnit());
                        analyzeSignalList.add(analyzeSignal);
                    }
                }
            }
        }catch (Exception ex){
            logger.error("get CAN Item Error message, error =",ex.getMessage());
        }
        return analyzeSignalList;
    }


    /**
     * get analyze message bean by 0705 by canMsg id
     * @param modelType:old and current. canBusParamItem:can bus param item
     * @return AnalyzeMsgBean
     */
    public AnalyzeMsgBean getAnalyzeMsgBean(CanBusParamItem canBusParamItem,String modelType) {
        double resultValue;
        AnalyzeMsgBean analyzeMsgBean = new AnalyzeMsgBean();
        AnalyzeSignal analyzeSignal;
        List<AnalyzeSignal> analyzeSignalList = Lists.newArrayList();
        /**
         * 解析出来DBC文件中有用的数据，按行显示 parse DBC file by line
         */
        List<String> msgDefAndValues = getMsgDefAndValues(getValidT38Data);
        Map<MessageBean, List<SignalBean>> t38Msg = null;
        try {
            if (DBCFormat.OLD.name().equals(modelType)) {
                t38Msg = getT38MsgComputer(msgDefAndValues);
            } else if (DBCFormat.CURRENT.name().equals(modelType)) {
                t38Msg = getT38MsgComputerNew(msgDefAndValues);
            }
            for (Map.Entry<MessageBean, List<SignalBean>> entries : t38Msg.entrySet()) {
                Long msgBeanCanId = Long.parseLong(entries.getKey().getMessage_id(), 10) & CANDISTANST;
                Long canBusItemCanId = Long.parseLong(canBusParamItem.getCanId(), 16) & CANDISTANST;

                if (msgBeanCanId.equals(canBusItemCanId)) {
                    for (SignalBean signalBean : entries.getValue()) {
                        analyzeSignal = new AnalyzeSignal();
                        resultValue = getResultValue(signalBean, canBusParamItem.getCanData());
                        analyzeSignal.setValue(resultValue);
                        analyzeSignal.setSignal_name(signalBean.getSignal_name());
                        analyzeSignal.setUnit(signalBean.getUnit());
                        analyzeSignalList.add(analyzeSignal);
                    }
                }
            }
            //todo fixme
            analyzeMsgBean.setCan_id(canBusParamItem.getCanId());
            analyzeMsgBean.setAnalyzeSignalList(analyzeSignalList);
        }catch (Exception ex){
            logger.error("get CAN Item Error message, error =",ex.getMessage());
        }
        return analyzeMsgBean;
    }

    /**
     * get resualt value kx+b
     * @param signalBean
     * @param canData
     * @return Long
     */
    private double getResultValue(SignalBean signalBean, String canData) {
        ByteBufUtils byteBufUtils = new ByteBufUtils();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBufUtils.hexStringToByteBuf(canData));
        ByteBuf byteBuffer = null;
        double resultValue = 0.000;
        SignalBean sigBean = signalBean;
        List<Byte> bytes = Lists.newArrayList();
        long raw_value = 0;
        Byte[] bt ;
        try {
            if (ONE.equals(sigBean.getByte_order())) {
                byteBuffer = byteBuf.order(ByteOrder.BIG_ENDIAN);
            } else if (ZERO.equals(sigBean.getByte_order())) {
                byteBuffer = byteBuf.order(ByteOrder.LITTLE_ENDIAN);
            }
            while (byteBuf.readableBytes() > 0) {
                Byte b = byteBuffer.readByte();
                bytes.add(b);
            }
            bt = bytes.toArray(new Byte[bytes.size()]);
            String bitArray = byteBufUtils.bytesToBit(bt);
            logger.info("current bit arrays " + bitArray);
            int beginIndex = Integer.parseInt(signalBean.getStart_bit());
            int endIndex = Integer.parseInt(signalBean.getSignal_size()) + beginIndex;
            double factor = Double.parseDouble(signalBean.getFactor());
            double offset = Double.parseDouble(signalBean.getOffset());
            String raw_value_tmp = bitArray.substring(beginIndex, endIndex);
            logger.info("raw_value_tmp " + raw_value_tmp);
            if (ZERO.equals(raw_value_tmp)) {
                raw_value = 0;
            } else if (StringUtils.EMPTY.equals(raw_value_tmp)) {
                logger.warn("please check can item data or DBC file!");
            } else {
                raw_value = Integer.parseUnsignedInt(raw_value_tmp, 2);
                logger.info("raw_value " + raw_value);
            }
            resultValue = raw_value * factor + offset;
            logger.info("result value: " + resultValue);
        }catch (Exception e){
            logger.error("get result value error,",e.getMessage());
        }
        return resultValue;
    }

    public static void main(String[] args) {
        T38CanParse t38 = new T38CanParse();

        CanBusParamItem canBusParamItem = new CanBusParamItem();
        canBusParamItem.setCanId("4CFE6CEE");
        canBusParamItem.setCanData("77FFFFFFFFFFFFFF");
        AnalyzeMsgBean analyzeMsgBean = t38.getAnalyzeMsgBean(canBusParamItem,DBCFormat.CURRENT.name());
        if(analyzeMsgBean!=null){
            logger.info("can items size: "+analyzeMsgBean.getAnalyzeSignalList().size());
            //analyzeMsgBean.getCan_id();
            for(AnalyzeSignal analyzeSignal : analyzeMsgBean.getAnalyzeSignalList()){
                logger.info(analyzeSignal.toString());
                /*logger.info("signalname: "+analyzeSignal.getSignal_name());
                logger.info("getValue: "+analyzeSignal.getValue());
                logger.info("getUnit: "+analyzeSignal.getUnit());*/
            }
        }
    }
}
