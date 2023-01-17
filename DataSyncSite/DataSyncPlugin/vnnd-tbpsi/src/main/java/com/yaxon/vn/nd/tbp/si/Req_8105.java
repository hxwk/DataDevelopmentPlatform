package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-01 11:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 终端控制
 */
public class Req_8105 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8105"; }

    private Byte commandWord; //命令字

    private CommandParamItem commandParamItem;

    private String commandParam; //命令参数

    public Byte getCommandWord() {
        return commandWord;
    }

    public void setCommandWord(Byte commandWord) {
        this.commandWord = commandWord;
    }

    public String getCommandParam() {
        return commandParam;
    }

    public void setCommandParam(String commandParam) {
        this.commandParam = commandParam;
    }

    public CommandParamItem getCommandParamItem() {
        return commandParamItem;
    }

    public void setCommandParamItem(CommandParamItem commandParamItem) {
        this.commandParamItem = commandParamItem;
    }

    @Override
    public String toString() {
        return "Req_8105{" + super.toString() +
                ", commandWord=" + commandWord +
                ", commandParamItem=" + commandParamItem +
                ", commandParam='" + commandParam + '\'' +
                '}';
    }
}
