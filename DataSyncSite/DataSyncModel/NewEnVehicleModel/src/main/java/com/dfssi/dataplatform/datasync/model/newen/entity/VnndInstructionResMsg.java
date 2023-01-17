package com.dfssi.dataplatform.datasync.model.newen.entity;


import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;

public class VnndInstructionResMsg extends VnndResMsg {

    private Integer instruction;

    private String instructionDesc;

    public Integer getInstruction() {
        return instruction;
    }

    public void setInstruction(Integer instruction) {
        this.instruction = instruction;
    }

    public String getInstructionDesc() {
        return instructionDesc;
    }

    public void setInstructionDesc(String instructionDesc) {
        this.instructionDesc = instructionDesc;
    }

    @Override
    public String toString() {
        return "VnndInstructionResMsg{" +
                ", instruction=" + instruction +
                ", instructionDesc='" + instructionDesc + '\'' +
                super.toString() +
                '}';
    }
}
