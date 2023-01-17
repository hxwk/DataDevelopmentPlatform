package com.dfssi.dataplatform.external.chargingPile.entity;

/**
 * Description 运营商实体类
 *
 * @author bin.Y
 * @version 2018/5/29 20:39
 */
public class ChargeOperatorInfoEntity {
    private String OperatorID;
    private String OperatorName;
    private String OperatorTel1;
    private String OperatorTel2;
    private String OperatorRegAddress;
    private String OperatorNote;
    private String PassWord;
    private String Url;

    public String getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
    }

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public String getOperatorTel1() {
        return OperatorTel1;
    }

    public void setOperatorTel1(String operatorTel1) {
        OperatorTel1 = operatorTel1;
    }

    public String getOperatorTel2() {
        return OperatorTel2;
    }

    public void setOperatorTel2(String operatorTel2) {
        OperatorTel2 = operatorTel2;
    }

    public String getOperatorRegAddress() {
        return OperatorRegAddress;
    }

    public void setOperatorRegAddress(String operatorRegAddress) {
        OperatorRegAddress = operatorRegAddress;
    }

    public String getOperatorNote() {
        return OperatorNote;
    }

    public void setOperatorNote(String operatorNote) {
        OperatorNote = operatorNote;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
