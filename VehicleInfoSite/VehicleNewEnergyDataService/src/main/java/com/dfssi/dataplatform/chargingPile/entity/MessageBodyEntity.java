package com.dfssi.dataplatform.chargingPile.entity;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/2 13:15
 */
public class MessageBodyEntity {
    private String OperatorID;
    private String Data;
    private String TimeStamp;
    private String Seq;
    private String Sig;

    public String getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSeq() {
        return Seq;
    }

    public void setSeq(String seq) {
        Seq = seq;
    }

    public String getSig() {
        return Sig;
    }

    public void setSig(String sig) {
        Sig = sig;
    }
}
