package com.dfssi.dataplatform.datasync.model.common;


/**
 * Created by Hannibal on 2018-04-13.
 */
public abstract class BaseMessage extends JtsReqMsg {

    private String commandSign;//命令标识

    private String vehicleCompany;//车企


    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getCommandSign() {
        return commandSign;
    }

    public void setCommandSign(String commandSign) {
        this.commandSign = commandSign;
    }
}
