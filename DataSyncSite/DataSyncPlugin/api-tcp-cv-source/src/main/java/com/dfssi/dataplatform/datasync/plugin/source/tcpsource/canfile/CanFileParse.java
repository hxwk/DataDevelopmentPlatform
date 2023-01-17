package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile;

import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.*;

/**
 * x0705 CAN T38 Can protocol parse Message class
 * @author jianKang
 * @date 2017/12/22
 */
public class CanFileParse {
    static final Logger logger = LoggerFactory.getLogger(CanFileParse.class);
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
    public CanFileParse() {
        getValidT38Data = new CanDBCFileRead().readValidData();
    }

    /**
     * get message define and values by EMPTY
     * @param lines
     * @return items list
     */
    private List<String> getMsgDefAndValues(Queue<String> lines) {
        List<String> msgDefAndValues = Lists.newArrayList();
        /**
         * get list by delimite EMPTY
         */
        for (String msg : lines) {
            if (null != msg && ! StringUtils.EMPTY.equals(msg)) {
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
    private Map<CanMessageBean, List<CanSignalBean>> getT38MsgComputerNew(List<String> msgItemList) {
        Map<CanMessageBean, List<CanSignalBean>> t38MsgMap = Maps.newConcurrentMap();
        CanMessageBean msgBean = null;
        List<CanSignalBean> signalBeanList = Lists.newArrayList();
        CanSignalBean signalBean;
        int appear = 0;
        try {
            for (int idx = 0; idx < msgItemList.size(); idx++) {
                if (msgItemList.get(idx).equals(BOSTANT)) {
                    if (0 == appear) {
                        appear++;
                    } else if (1 == appear) {
                        //dbc文件解析映射列表
                        t38MsgMap.put(msgBean, signalBeanList);
                        signalBeanList = Lists.newLinkedList();
                    }
                    /**
                     * The extended
                     CAN ID can be determined by masking out the most significant bit with the
                     mask 0xCFFFFFFF.
                     */
                    msgBean = new CanMessageBean();
                    String message_id = String.valueOf(Long.parseLong(msgItemList.get(++idx)));
                    msgBean.setMessage_id(message_id);
                    msgBean.setMessage_name(msgItemList.get(++idx));
                    msgBean.setMessage_size(msgItemList.get(++idx));
                    msgBean.setTransmitter(msgItemList.get(++idx));

                } else if (SGCONSTANT.equals(msgItemList.get(idx))) {
                    signalBean = new CanSignalBean();
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
     * @param msgItems
     * @return map
     */
    private Map<CanMessageBean, List<CanSignalBean>> getT38MsgComputer(List<String> msgItems) {
        Map<CanMessageBean, List<CanSignalBean>> t38MsgComputer = Maps.newConcurrentMap();
        List<CanSignalBean> signalBeanList = Lists.newArrayList();
        CanSignalBean signalBean;
        CanMessageBean msgBean = null;
        int appear = 0;
        for (int idx = 0; idx < msgItems.size(); idx++) {
            if (msgItems.get(idx).equals(BOSTANT)) {
                if (appear == 1) {
                    t38MsgComputer.put(msgBean, signalBeanList);
                    signalBeanList = Lists.newArrayList();
                } else if (appear == 0) {
                    appear++;
                }
                /**
                 * The extended
                 CAN ID can be determined by masking out the most significant bit with the
                 mask 0xCFFFFFFF.
                 */
                msgBean = new CanMessageBean();
                String message_id = String.valueOf(Long.parseLong(msgItems.get(++idx)));
                msgBean.setMessage_id(message_id);
                msgBean.setMessage_name(msgItems.get(++idx));
                msgBean.setMessage_size(msgItems.get(++idx));
                msgBean.setTransmitter(msgItems.get(++idx));

            } else if (msgItems.get(idx).equals(SGCONSTANT)) {
                signalBean = new CanSignalBean();
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
        List<String> itemList = Lists.newLinkedList();
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
    public List<CanAnalyzeSignal> getAnalyzeSignalList(CanBusParamItem canBusParamItem, String modelType) {
        double resultValue;
        CanAnalyzeSignal analyzeSignal;
        Map<CanMessageBean, List<CanSignalBean>> t38Msg = null;
        List<CanAnalyzeSignal> analyzeSignalList = Lists.newArrayList();
        /**
         * 解析出来DBC文件中有用的数据，按行显示 parse DBC file by line
         */
        List<String> msgDefAndValues = getMsgDefAndValues(getValidT38Data);
        try {
            logger.debug( " modelType = " + modelType + ", msgDefAndValues = " + msgDefAndValues);
            if (CanDBCFormat.OLD.name().equalsIgnoreCase(modelType)) {
                t38Msg = getT38MsgComputer(msgDefAndValues);
            } else if (CanDBCFormat.CURRENT.name().equalsIgnoreCase(modelType)) {
                t38Msg = getT38MsgComputerNew(msgDefAndValues);
            }
            for (Map.Entry<CanMessageBean, List<CanSignalBean>> entries : t38Msg.entrySet()) {
                Long msgBeanCanId = Long.parseLong(entries.getKey().getMessage_id(), 10) & CANDISTANST;
                //can id & can data
                Long canBusItemCanId = Long.parseLong(canBusParamItem.getCanId(), 16) & CANDISTANST;
                if (msgBeanCanId.equals(canBusItemCanId)) {
                    for (CanSignalBean signalBean : entries.getValue()) {
                        analyzeSignal = new CanAnalyzeSignal();
                        resultValue = getResultValue(signalBean, canBusParamItem.getCanData());
                        analyzeSignal.setId(UUID.randomUUID().toString());
                        analyzeSignal.setValue(resultValue);
                        analyzeSignal.setSignal_name(signalBean.getSignal_name());
                        analyzeSignal.setUnit(signalBean.getUnit());
                        analyzeSignalList.add(analyzeSignal);
                    }
                }
            }
        }catch (Exception ex){
            logger.error("CanFileParse GOT CAN Item error:{}",ex);
        }
        return analyzeSignalList;
    }

    /**
     * get analyze message bean by 0705 by canMsg id
     * @param modelType:old and current. canBusParamItem:can bus param item
     * @return AnalyzeMsgBean
     */
    public CanAnalyzeMsgBean getAnalyzeMsgBean(CanBusParamItem canBusParamItem, String modelType) {
        double resultValue;
        CanAnalyzeMsgBean analyzeMsgBean = new CanAnalyzeMsgBean();
        CanAnalyzeSignal analyzeSignal;
        List<CanAnalyzeSignal> analyzeSignalList = Lists.newArrayList();
        /**
         * 解析出来DBC文件中有用的数据，按行显示 parse DBC file by line
         */
        List<String> msgDefAndValues = getMsgDefAndValues(getValidT38Data);
        Map<CanMessageBean, List<CanSignalBean>> t38Msg = null;
        try {
            if (CanDBCFormat.OLD.name().equals(modelType)) {
                t38Msg = getT38MsgComputer(msgDefAndValues);
            } else if (CanDBCFormat.CURRENT.name().equals(modelType)) {
                t38Msg = getT38MsgComputerNew(msgDefAndValues);
            }
            for (Map.Entry<CanMessageBean, List<CanSignalBean>> entries : t38Msg.entrySet()) {
                Long msgBeanCanId = Long.parseLong(entries.getKey().getMessage_id(), 10) & CANDISTANST;
                Long canBusItemCanId = Long.parseLong(canBusParamItem.getCanId(), 16) & CANDISTANST;

                if (msgBeanCanId.equals(canBusItemCanId)) {
                    for (CanSignalBean signalBean : entries.getValue()) {
                        analyzeSignal = new CanAnalyzeSignal();
                        analyzeSignal.setId(UUID.randomUUID().toString());
                        resultValue = getResultValue(signalBean, canBusParamItem.getCanData());
                        analyzeSignal.setValue(resultValue);
                        analyzeSignal.setSignal_name(signalBean.getSignal_name());
                        analyzeSignal.setUnit(signalBean.getUnit());
                        analyzeSignalList.add(analyzeSignal);
                    }
                }
            }
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
    private double getResultValue(CanSignalBean signalBean, String canData) {
        ByteBufCustomTool byteBufCustomTool = new ByteBufCustomTool();
        //TODO optimize
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBufCustomTool.hexStringToByteBuf(canData));
        ByteBuf byteBuffer = null;
        double resultValue = 0.000d;
        CanSignalBean sigBean = signalBean;
        List<Byte> bytes = Lists.newArrayList();
        long raw_value = 0;
        String raw_value_tmp;
        Byte[] bt ;
        try {
            if (ZERO.equals(sigBean.getByte_order())) {
                byteBuffer = byteBuf.order(ByteOrder.BIG_ENDIAN);
            } else if (ONE.equals(sigBean.getByte_order())) {
                byteBuffer = byteBuf.order(ByteOrder.LITTLE_ENDIAN);
            }
            while (byteBuffer.readableBytes() > 0) {
                logger.debug("start--> ");
                Byte b = byteBuffer.readByte();
                bytes.add(b);
            }
            bt = bytes.toArray(new Byte[bytes.size()]);
            String bitArray = byteBufCustomTool.bytesToBit(bt);
            int index = Integer.parseInt(sigBean.getStart_bit());
            int len = Integer.parseInt(sigBean.getSignal_size());
            int endIndex = ((index + len - 1) / 8 + 1) * 8 - index % 8;
            if (0 == len % 8) {
                endIndex = index + len;
            }
            int beginIndex = endIndex - len;
//            int beginIndex = Integer.parseInt(signalBean.getStart_bit());
//            int endIndex = Integer.parseInt(signalBean.getSignal_size()) + beginIndex;
            double factor = Double.parseDouble(signalBean.getFactor());
            double offset = Double.parseDouble(signalBean.getOffset());
            if(bitArray.length()>=endIndex-1) {
                raw_value_tmp = bitArray.substring(beginIndex, endIndex);
                if (ONE.equalsIgnoreCase(sigBean.getByte_order()) && len % 8 == 0) {
                    raw_value_tmp = getLittleBit(raw_value_tmp);
                }
            }else{
                if(beginIndex>bitArray.length()-1||endIndex>bitArray.length()){
                    logger.error("取值大于位数组长度,报文和DBC不匹配");
                }
                logger.error("bitArray length:{},beginIndex:{},endIndex:{},factor:{},offset:{}",bitArray.length(),beginIndex,endIndex,factor,offset);
                return 0.0;
            }
            if (ZERO.equals(raw_value_tmp)) {
                raw_value = 0;
            } else if (StringUtils.EMPTY.equals(raw_value_tmp)) {
                logger.warn("报文对应位值为空");
            } else {
                raw_value = Long.parseLong(raw_value_tmp, 2);
                //logger.info("raw_value " + raw_value);
            }
            resultValue = raw_value * factor + offset;
            //logger.info("result value: " + resultValue);
        }catch (Exception e){
            logger.error("get result value error:{},getStackTrace:{}", e,e.getStackTrace());
        }
        return resultValue;
    }

    public String getLittleBit(String orgBit) {
        if (orgBit.length() <=8 ) {
            return orgBit;
        }

        int size = orgBit.length() / 8;
        StringBuilder leftBuf = new StringBuilder();
        for (int i = 0; i < size; i++) {
            leftBuf.append(orgBit.substring(orgBit.length() - (i + 1) * 8, orgBit.length() - i * 8));
        }

        return leftBuf.toString();
    }

//    public static void main(String[] args) {
//
////        System.out.println(" getLittleBit = " + new CanFileParse().getLittleBit("0100100011111100"));
//
//        CanFileParse t38 = new CanFileParse();
//        CanBusParamItem canBusParamItem = new CanBusParamItem();
//        canBusParamItem.setCanId("0CF00400");
//        canBusParamItem.setCanData("4183827F1E000483");
////        canBusParamItem.setCanId("18FEF131");
////        canBusParamItem.setCanData("0400005115642000");
//        CanAnalyzeMsgBean analyzeMsgBean = t38.getAnalyzeMsgBean(canBusParamItem,CanDBCFormat.OLD.name());
//        if(analyzeMsgBean!=null){
//            logger.debug("can items size: "+analyzeMsgBean.getAnalyzeSignalList().size());
//            //analyzeMsgBean.getCan_id();
//            for(CanAnalyzeSignal analyzeSignal : analyzeMsgBean.getAnalyzeSignalList()){
//                logger.info(analyzeSignal.toString());
//            }
//        }
//    }
}
