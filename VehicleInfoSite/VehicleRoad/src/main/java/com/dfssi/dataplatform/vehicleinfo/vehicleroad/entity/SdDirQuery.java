package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import java.util.Arrays;

/**
 * @ClassName SdDirQuery
 * @Description 目录查询
 * @Author chenf
 * @Date 2018/9/27
 * @Versiion 1.0
 **/
public class SdDirQuery extends JtsReqMsg {
    public static final String _id = "jts.E101";

    @Override
    public String id() { return "jts.E101"; }

    private String id=_id;

    private String sim;

    private int[] paramIds; //参数ID列表

    public int[] getParamIds() {
        return paramIds;
    }

    public void setParamIds(int[] paramIds) {
        this.paramIds = paramIds;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SdDirQuery{" +
                "id='" + id + '\'' +
                ", sim='" + sim + '\'' +
                ", paramIds=" + Arrays.toString(paramIds) +
                ", vid='" + vid + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
