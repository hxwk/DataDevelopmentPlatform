package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:22
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * 提问下发
 */
public class Req_8302 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8302"; }

    private Byte flag; //标志位

    private String question; //问题

    private Integer questionId; //问题Id

    private List<ParamItem> paramItems;

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ParamItem> getParamItems() {
        return paramItems;
    }

    public void setParamItems(List<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "Req_8302{" + super.toString() +
                "flag=" + flag +
                ", question='" + question + '\'' +
                ", questionId=" + questionId +
                ", paramItems=" + paramItems +
                '}';
    }
}
