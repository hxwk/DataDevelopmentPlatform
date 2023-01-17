package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import java.io.Serializable;
import java.util.List;

//终端返回的参数应该是TerminalSettingEntity加上一个查询参数流水号的参数
public class TerminalParamQuery{
    /*public byte terminalSettingParamTotal;//查询终端参数的总个数
    public List<String> terminalSettingParamList;//终端参数的List   长度为4n*/
    private long sim;

    /* 车辆ID */
    private String vid;

    //终端参数查询 多个参数时用逗号隔开适用于普通查询
    //F002：DBC版本查询
    //F003：通讯模块固件版本号
    //F004：通讯模块APP版本号
    //F005：MCU boot固件版本号
    //F006：MCU APP版本号
    //F007：视频模块固件版本号
    //F008：视频模块APP版本号
    //F00A：ECU软件版本查询
    //F00B：ECU硬件版本查询
    private String queryType; //查询终端参数的类型

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }


    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    @Override
    public String toString() {
        return "{sim:"+sim+",vid:"+vid+",queryType:"+queryType+"}";
    }
}
