package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:27
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 提问应答
 */
public class Req_0302 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0302"; }

    private Byte id; //答案ID

    private Short sn; //流水号

    private Byte flag; //标志位：0，正常；1，流水号不匹配；2，答案id不存在

    private Integer questionId;

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Short getSn() {
        return sn;
    }

    public void setSn(Short sn) {
        this.sn = sn;
    }

    public Byte getId() {
        return id;
    }

    public void setId(Byte id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Req_0302{" +
                "id=" + id +
                ", sn=" + sn +
                ", flag=" + flag +
                ", questionId=" + questionId +
                '}';
    }
}
