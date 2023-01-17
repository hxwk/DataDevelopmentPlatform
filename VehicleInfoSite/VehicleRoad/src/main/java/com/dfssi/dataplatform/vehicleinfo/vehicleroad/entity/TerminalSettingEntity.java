package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 远程终端控制与设置--设置的终端参数的entity
 * Created by yanghs on 2018/9/12.
 */
//设置终端参数的entity相当复杂，可根据应用适当简化参数设置
@Data
public class TerminalSettingEntity {
    private long sim;

    /* 车辆ID */
    private String vid;

    private List<Item> paramItems;

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


    public List<Item> getParamItems() {
        return paramItems;
    }

    public void setParamItems(List<Item> paramItems) {
        this.paramItems = paramItems;
    }

    @Override
    public String toString() {
        return "{sim:"+sim+",vid:"+vid+",paramItems:"+paramItems+"}";
    }
}
