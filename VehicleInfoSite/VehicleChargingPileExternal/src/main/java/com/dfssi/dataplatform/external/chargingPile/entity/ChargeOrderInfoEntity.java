package com.dfssi.dataplatform.external.chargingPile.entity;

/**
 * Description 订单信息
 *
 * @author bin.Y
 * @version 2018/5/31 10:25
 */
public class ChargeOrderInfoEntity {
    private String OperatorID ;
    private String ConnectorID;
    private String StartChargeSeq ;
    private int UserChargeType;
    private String MobileNumber;
    private double Money;
    private double ElectMoney;
    private double ServiceMoney;
    private double Elect;
    private double CuspElect;

    private double CuspElectPrice;
    private double CuspServicePrice;
    private double CuspMoney;
    private double CuspElectMoney;
    private double CuspServiceMoney;

    private double PeakElect;
    private double PeakElectPrice;
    private double PeakServicePrice;
    private double PeakMoney;
    private double PeakElectMoney;

    private double PeakServiceMoney;
    private double FlatElect;
    private double FlatElectPrice;
    private double FlatServicePrice;
    private double FlatMoney;
    private double FlatElectMoney;
    private double FlatServiceMoney;
    private double ValleyElect;
    private double ValleyElectPrice;
    private double ValleyServicePrice;


    private double ValleyMoney;
    private double ValleyElectMoney;
    private double ValleyServiceMoney;
    private String StartTime;
    private String EndTime;
    private double PaymentAmount;
    private String PayTime;
    private int PayChannel;
    private String DiscountInfo;
    private double TotalPower;

    private double TotalElecMoney;
    private double TotalServiceMoney;
    private double TotalMoney;
    private int StopReason;
    private int SumPeriod;

    public String getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
    }

    public String getConnectorID() {
        return ConnectorID;
    }

    public void setConnectorID(String connectorID) {
        ConnectorID = connectorID;
    }

    public String getStartChargeSeq() {
        return StartChargeSeq;
    }

    public void setStartChargeSeq(String startChargeSeq) {
        StartChargeSeq = startChargeSeq;
    }

    public int getUserChargeType() {
        return UserChargeType;
    }

    public void setUserChargeType(int userChargeType) {
        UserChargeType = userChargeType;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public double getMoney() {
        return Money;
    }

    public void setMoney(double Money) {
        this.Money = Money;
    }

    public double getElectMoney() {
        return ElectMoney;
    }

    public void setElectMoney(double electMoney) {
        ElectMoney = electMoney;
    }

    public double getServiceMoney() {
        return ServiceMoney;
    }

    public void setServiceMoney(double serviceMoney) {
        ServiceMoney = serviceMoney;
    }

    public double getElect() {
        return Elect;
    }

    public void setElect(double elect) {
        Elect = elect;
    }

    public double getCuspElect() {
        return CuspElect;
    }

    public void setCuspElect(double cuspElect) {
        CuspElect = cuspElect;
    }

    public double getCuspElectPrice() {
        return CuspElectPrice;
    }

    public void setCuspElectPrice(double cuspElectPrice) {
        CuspElectPrice = cuspElectPrice;
    }

    public double getCuspServicePrice() {
        return CuspServicePrice;
    }

    public void setCuspServicePrice(double cuspServicePrice) {
        CuspServicePrice = cuspServicePrice;
    }

    public double getCuspMoney() {
        return CuspMoney;
    }

    public void setCuspMoney(double cuspMoney) {
        CuspMoney = cuspMoney;
    }

    public double getCuspElectMoney() {
        return CuspElectMoney;
    }

    public void setCuspElectMoney(double cuspElectMoney) {
        CuspElectMoney = cuspElectMoney;
    }

    public double getCuspServiceMoney() {
        return CuspServiceMoney;
    }

    public void setCuspServiceMoney(double cuspServiceMoney) {
        CuspServiceMoney = cuspServiceMoney;
    }

    public double getPeakElect() {
        return PeakElect;
    }

    public void setPeakElect(double peakElect) {
        PeakElect = peakElect;
    }

    public double getPeakElectPrice() {
        return PeakElectPrice;
    }

    public void setPeakElectPrice(double peakElectPrice) {
        PeakElectPrice = peakElectPrice;
    }

    public double getPeakServicePrice() {
        return PeakServicePrice;
    }

    public void setPeakServicePrice(double peakServicePrice) {
        PeakServicePrice = peakServicePrice;
    }

    public double getPeakMoney() {
        return PeakMoney;
    }

    public void setPeakMoney(double peakMoney) {
        PeakMoney = peakMoney;
    }

    public double getPeakElectMoney() {
        return PeakElectMoney;
    }

    public void setPeakElectMoney(double peakElectMoney) {
        PeakElectMoney = peakElectMoney;
    }

    public double getPeakServiceMoney() {
        return PeakServiceMoney;
    }

    public void setPeakServiceMoney(double peakServiceMoney) {
        PeakServiceMoney = peakServiceMoney;
    }

    public double getFlatElect() {
        return FlatElect;
    }

    public void setFlatElect(double flatElect) {
        FlatElect = flatElect;
    }

    public double getFlatElectPrice() {
        return FlatElectPrice;
    }

    public void setFlatElectPrice(double flatElectPrice) {
        FlatElectPrice = flatElectPrice;
    }

    public double getFlatServicePrice() {
        return FlatServicePrice;
    }

    public void setFlatServicePrice(double flatServicePrice) {
        FlatServicePrice = flatServicePrice;
    }

    public double getFlatMoney() {
        return FlatMoney;
    }

    public void setFlatMoney(double flatMoney) {
        FlatMoney = flatMoney;
    }

    public double getFlatElectMoney() {
        return FlatElectMoney;
    }

    public void setFlatElectMoney(double flatElectMoney) {
        FlatElectMoney = flatElectMoney;
    }

    public double getFlatServiceMoney() {
        return FlatServiceMoney;
    }

    public void setFlatServiceMoney(double flatServiceMoney) {
        FlatServiceMoney = flatServiceMoney;
    }

    public double getValleyElect() {
        return ValleyElect;
    }

    public void setValleyElect(double valleyElect) {
        ValleyElect = valleyElect;
    }

    public double getValleyElectPrice() {
        return ValleyElectPrice;
    }

    public void setValleyElectPrice(double valleyElectPrice) {
        ValleyElectPrice = valleyElectPrice;
    }

    public double getValleyServicePrice() {
        return ValleyServicePrice;
    }

    public void setValleyServicePrice(double valleyServicePrice) {
        ValleyServicePrice = valleyServicePrice;
    }

    public double getValleyMoney() {
        return ValleyMoney;
    }

    public void setValleyMoney(double valleyMoney) {
        ValleyMoney = valleyMoney;
    }

    public double getValleyElectMoney() {
        return ValleyElectMoney;
    }

    public void setValleyElectMoney(double valleyElectMoney) {
        ValleyElectMoney = valleyElectMoney;
    }

    public double getValleyServiceMoney() {
        return ValleyServiceMoney;
    }

    public void setValleyServiceMoney(double valleyServiceMoney) {
        ValleyServiceMoney = valleyServiceMoney;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public double getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public String getPayTime() {
        return PayTime;
    }

    public void setPayTime(String payTime) {
        PayTime = payTime;
    }

    public int getPayChannel() {
        return PayChannel;
    }

    public void setPayChannel(int payChannel) {
        PayChannel = payChannel;
    }

    public String getDiscountInfo() {
        return DiscountInfo;
    }

    public void setDiscountInfo(String discountInfo) {
        DiscountInfo = discountInfo;
    }

    public double getTotalPower() {
        return TotalPower;
    }

    public void setTotalPower(double totalPower) {
        TotalPower = totalPower;
    }

    public double getTotalElecMoney() {
        return TotalElecMoney;
    }

    public void setTotalElecMoney(double totalElecMoney) {
        TotalElecMoney = totalElecMoney;
    }

    public double getTotalServiceMoney() {
        return TotalServiceMoney;
    }

    public void setTotalServiceMoney(double totalServiceMoney) {
        TotalServiceMoney = totalServiceMoney;
    }

    public double getTotalMoney() {
        return TotalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        TotalMoney = totalMoney;
    }

    public int getStopReason() {
        return StopReason;
    }

    public void setStopReason(int stopReason) {
        StopReason = stopReason;
    }

    public int getSumPeriod() {
        return SumPeriod;
    }

    public void setSumPeriod(int sumPeriod) {
        SumPeriod = sumPeriod;
    }
}
