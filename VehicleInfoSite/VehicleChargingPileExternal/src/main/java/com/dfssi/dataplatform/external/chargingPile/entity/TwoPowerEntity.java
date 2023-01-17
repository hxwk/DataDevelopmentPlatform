package com.dfssi.dataplatform.external.chargingPile.entity;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/06/08 15:06
 */
public class TwoPowerEntity {
    private double power;
    private double currentPower;
    private int createHour;

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(double currentPower) {
        this.currentPower = currentPower;
    }

    public int getCreateHour() {
        return createHour;
    }

    public void setCreateHour(int createHour) {
        this.createHour = createHour;
    }
}
