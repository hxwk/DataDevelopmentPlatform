package com.yaxon.vn.nd.tbp.si;


/**
 * 基类
 */
public class VnndLoginResMsg extends VnndResMsg {

    private byte rc;

    private String authCode;

    public byte getRc() {
        return rc;
    }

    public void setRc(byte rc) {
        this.rc = rc;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Override
    public String toString() {
        return "VnndLoginResMsg{" +
                "rc=" + rc +
                ", authCode='" + authCode + '\'' +
                '}';
    }
}
