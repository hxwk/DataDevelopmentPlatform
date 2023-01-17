package com.yaxon.vn.nd.tbp.si;


/**
 * 基类
 */
public class VnndResMsg {

    private String vid;

    private Integer status;//失败传0，成功传1

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VnndResMsg{" +
                ", vid=" + vid +
                ", status=" + status +
                '}';
    }
}
