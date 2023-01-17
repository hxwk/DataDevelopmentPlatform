package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * @author jianKang
 * @date 2018/01/19
 */
public class CanAnalyzeBeanItem implements Serializable {
    public String id;
    private String sim;
    private String vid;
    private String dbcType;
    private String msgId;
    private int itemNum;
    private long receiveTime;
    //private List<AnalyzeMsgBean> messageBeanList;
    private List<CanAnalyzeSignal> messageBeanList;

    public String getId() {
        return id==null? UUID.randomUUID().toString():id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

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

    public List<CanAnalyzeSignal> getMessageBeanList() {
        return messageBeanList;
    }

    public void setMessageBeanList(List<CanAnalyzeSignal> messageBeanList) {
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
        return "CanAnalyzeBeanItem{" +
                "id='" + id + '\'' +
                ", sim='" + sim + '\'' +
                ", vid='" + vid + '\'' +
                ", dbcType='" + dbcType + '\'' +
                ", msgId='" + msgId + '\'' +
                ", itemNum=" + itemNum +
                ", receiveTime=" + receiveTime +
                ", messageBeanList=" + messageBeanList +
                '}';
    }
}
