package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

/**
 * @ClassName SdFileQuery
 * @Description TODO
 * @Author chenf
 * @Date 2018/9/27
 * @Versiion 1.0
 **/
public class SdFileQuery extends JtsReqMsg{
    public static final String _id = "jts.E102";

    @Override
    public String id() { return "jts.E102"; }

    /**
     * 参数ID列表 文件类型
     */

    private String id = _id;

    private String sim;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", sim='" + sim + '\'' +
                ", paramId=" + paramId +
                ", paramLength=" + paramLength +
                ", paramValue='" + paramValue + '\'' +
                '}';
    }
}
