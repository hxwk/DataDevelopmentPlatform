package com.yaxon.vn.nd.tbp.si;


import java.util.List;

/**
 * 下发透传LED显示屏　（LED显示屏滚动信息）
 */
public class Req_8B10_42_08 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8B104208";
    }

    private byte typeId; //设备类型
    private Short  dataType; //数据类型

    private byte infoType; //信息类型   0x02：滚动类型
    private List<String> contentList; //信息条数  每条17个字节，1个字节当密钥序号, 信息内容（N*信息内容）


    public byte getTypeId() {
        return typeId;
    }

    public void setTypeId(byte typeId) {
        this.typeId = typeId;
    }

    public Short getDataType() {
        return dataType;
    }

    public void setDataType(Short dataType) {
        this.dataType = dataType;
    }

    public byte getInfoType() {
        return infoType;
    }

    public void setInfoType(byte infoType) {
        this.infoType = infoType;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    @Override
    public String toString() {
        return "Req_8B10_42_08{" +
                "typeId=" + typeId +
                ", dataType=" + dataType +
                ", infoType=" + infoType +
                ", contentList=" + contentList +
                '}';
    }
}
