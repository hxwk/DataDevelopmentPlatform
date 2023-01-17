package com.dfssi.dataplatform.datasync.plugin.interceptor.canbean;

import java.io.Serializable;
import java.util.List;

/**
 * Message bean -> DBC
 * @author jianKang
 * @date 2017/12/22
 */
public class MessageBean implements Serializable {
    public String id(){
        return "BO_";
    }
    private String message_id;
    private String message_name;
    private String message_size;
    private String transmitter;
    /**
     * each BO_ , above many SG_
     */
    private List<SignalBean> signalBeanList;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_name() {
        return message_name;
    }

    public void setMessage_name(String message_name) {
        this.message_name = message_name;
    }

    public String getMessage_size() {
        return message_size;
    }

    public void setMessage_size(String message_size) {
        this.message_size = message_size;
    }

    public String getTransmitter() {
        return transmitter;
    }

    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    public List<SignalBean> getSignalBeanList() {
        return signalBeanList;
    }

    public void setSignalBeanList(List<SignalBean> signalBeanList) {
        this.signalBeanList = signalBeanList;
    }

    //fixme todo 打印整个消息体包括消息ID和值
    @Override
    public String toString() {
        return "MessageBean{" +
                "CANID='" + message_id + '\'' +
                ", DBCMsgName='" + message_name + '\'' +
                ", DBCMsgLength='" + message_size + '\'' +
                ", nodeName='" + transmitter + '\'' +
                '}';
    }
}
