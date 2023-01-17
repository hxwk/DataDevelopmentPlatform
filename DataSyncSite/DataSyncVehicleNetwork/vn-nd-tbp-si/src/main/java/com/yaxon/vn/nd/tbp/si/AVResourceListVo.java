package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/6/27
 * @description
 */
public class AVResourceListVo implements Serializable {

    private String id;
    private String vid;
    private String sim;
    private int flowNo;
    private long avResourceNum;
    private List<AVResourceListItem> avResourceListItemList;

    public String getId() {
        return id;
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
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public int getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(int flowNo) {
        this.flowNo = flowNo;
    }

    public long getAvResourceNum() {
        return avResourceNum;
    }

    public void setAvResourceNum(long avResourceNum) {
        this.avResourceNum = avResourceNum;
    }

    public List<AVResourceListItem> getAvResourceListItemList() {
        return avResourceListItemList;
    }

    public void setAvResourceListItemList(List<AVResourceListItem> avResourceListItemList) {
        this.avResourceListItemList = avResourceListItemList;
    }

    @Override
    public String toString() {
        return "AVResourceListVo{" +
                "id='" + id + '\'' +
                ", vid='" + vid + '\'' +
                ", sim='" + sim + '\'' +
                ", flowNo='" + flowNo + '\'' +
                ", avResourceNum=" + avResourceNum +
                ", avResourceListItemList=" + avResourceListItemList +
                '}';
    }
}
