package com.yaxon.vn.nd.tbp.si.ne;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/2/11
 * @description
 */
public class NEDriverMotor implements Serializable {
    private short driveMotorInformationType;
    private short driverMotorNumber;
    private List<NEDriverMotorBean> NEDriverMotorBeans;

    public short getDriveMotorInformationType() {
        return driveMotorInformationType;
    }

    public void setDriveMotorInformationType(short driveMotorInformationType) {
        this.driveMotorInformationType = driveMotorInformationType;
    }

    public short getDriverMotorNumber() {
        return driverMotorNumber;
    }

    public void setDriverMotorNumber(short driverMotorNumber) {
        this.driverMotorNumber = driverMotorNumber;
    }

    public List<NEDriverMotorBean> getNEDriverMotorBeans() {
        return NEDriverMotorBeans;
    }

    public void setNEDriverMotorBeans(List<NEDriverMotorBean> NEDriverMotorBeans) {
        this.NEDriverMotorBeans = NEDriverMotorBeans;
    }

    @Override
    public String toString() {
        ObjectUtils.defaultIfNull(NEDriverMotorBeans, Lists.newArrayList());
        String body = "NEDriverMotor{" +
                "driveMotorInformationType=" + driveMotorInformationType +
                ", driverMotorNumber=" + driverMotorNumber +
                ", NEDriverMotorBeans=" + NEDriverMotorBeans +
                '}';
        return body;
    }
}
