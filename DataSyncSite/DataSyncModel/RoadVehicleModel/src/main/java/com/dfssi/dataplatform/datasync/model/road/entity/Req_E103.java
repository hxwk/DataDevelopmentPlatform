package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @ClassName Req_E103
 * @Description TODO
 * @Author ThinkPad
 * @Date 2018/9/20
 * @Versiion 1.0
 **/
public class Req_E103 extends JtsReqMsg {

    public static final String _id = "jts.E103";

    @Override
    public String id() { return "jts.E103"; }

    private String sim;

    private String timestamp;
    /**
     * 参数ID列表 文件类型
     */

    private String paramId;
    /**
     *文件路径长度
     */

//    private short paramLength;
    //文件路径
    private String paramValue;


//    public short getParamLength() {
//        return paramLength;
//    }

//    public void setParamLength(short paramLength) {
//        this.paramLength = paramLength;
//    }


    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }



    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "Req_E103{" +
                "paramId=" + paramId +
                ",timestamp="+timestamp+
                ", paramValue='" + paramValue + '\'' +
                ", vid='" + vid + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
