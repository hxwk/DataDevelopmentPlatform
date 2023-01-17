package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jian on 2018/1/19.
 */
public class CanMessageBean implements Serializable {
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
    private List<CanSignalBean> signalBeanList;

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

    public List<CanSignalBean> getSignalBeanList() {
        return signalBeanList;
    }

    public void setSignalBeanList(List<CanSignalBean> signalBeanList) {
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
