package com.yaxon.vn.nd.ne.tas.common;

/**
 * @author JianKang
 * @date 2018/2/12
 * @description 信息类型标志
 */
public enum InformationTypeCode {
    //整车数据
    VEHICLEDATA("0x01"),
    //驱动电机
    DRIVERMOTOR("0x02"),
    //燃料电池
    FUELCELL("0x03"),
    //发动机数据
    MOTORDATA("0x04"),
    //车辆位置信息
    NEGPS("0x05"),
    //极值数据
    EXTREMUM("0x06"),
    //报警数据
    ALARM("0x07");
    private String typeCode;

    InformationTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }
}
