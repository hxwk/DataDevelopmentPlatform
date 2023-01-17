package com.dfssi.dataplatform.datasync.plugin.interceptor.canbean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * analyze to hdfs bean -> output
 * @author jianKang
 * @date 2017/12/23
 */
public class AnalyzeBean implements Serializable{
    public String id(){
        return "AnalyzeBean";
    }
    private String sim;
    private String dbcType;
    private String msgId;
    private int itemNum;
    private long receiveTime;
    //private List<AnalyzeMsgBean> messageBeanList;
    private List<AnalyzeSignal> messageBeanList;

    public String getSim() {
        return sim==null?"unknown":sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getDbcType() {
        return dbcType==null?"t38":dbcType;
    }

    public void setDbcType(String dbcType) {
        this.dbcType = dbcType;
    }

    public long getReceiveTime() {
        return receiveTime==0?System.currentTimeMillis():receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

 /*   public List<AnalyzeMsgBean> getMessageBeanList() {
        return messageBeanList==null?Lists.newArrayList():messageBeanList;
    }

    public void setMessageBeanList(List<AnalyzeMsgBean> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }
*/

    public List<AnalyzeSignal> getMessageBeanList() {
        return messageBeanList;
    }

    public void setMessageBeanList(List<AnalyzeSignal> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    @Override
    public String toString() {
        /*return "\nstart\nAnalyzeBean[" +
                "CAN ID='" + msgId + '\'' +
                ", CANReceiveTime='" + receiveTime + '\'' +
                ", CANMessageBeans=" + messageBeanList.toString() +
                ']';*/
        return JSON.toJSONString(this);
    }
}
