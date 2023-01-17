package com.dfssi.dataplatform.chargingPile.entity;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/4 8:58
 */
public class ReturnBodyEntity {
    private int Ret;
    private String Msg;
    private String Data;
    private String Sig;

    public int getRet() {
        return Ret;
    }

    public void setRet(int ret) {
        Ret = ret;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getSig() {
        return Sig;
    }

    public void setSig(String sig) {
        Sig = sig;
    }
}
