package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @ClassName Req_E102
 * @Description TODO
 * @Author ThinkPad
 * @Date 2018/9/20
 * @Versiion 1.0
 **/
public class Req_E102 extends JtsReqMsg {

    public static final String _id = "jts.E102";

    @Override
    public String id() { return "jts.E102"; }

    private String sim;

    /**
     * 参数ID列表 文件类型
     */

    private int paramId;
    /**
     *文件路径长度
     */

    private int paramLength;
    //文件路径
    private String paramValue;


    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public int getParamLength() {
        return paramLength;
    }

    public void setParamLength(int paramLength) {
        this.paramLength = paramLength;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        return "Req_E102{" +
                "paramId=" + paramId +
                ", paramLength=" + paramLength +
                ", paramValue='" + paramValue + '\'' +
                ", vid='" + vid + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
