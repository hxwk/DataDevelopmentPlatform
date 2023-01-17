package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

public class Req_9202 extends JtsReqMsg {
    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private byte AudioVideoChannelNum; //音视频通道号
    private byte playbackControl; //回放控制
    private byte fastForwardOrBackMultiple; //快进或快退倍数
    private String dragPlaybackPosition; //拖动回放位置 格式为时间  byte[6]
    @Override
    public String id() { return "jts.9202"; }

    @Override
    public String toString() {
        return "Res_9202{sim:"+sim
                +",AudioVideoChannelNum:"+AudioVideoChannelNum
                +",playbackControl:"+playbackControl
                +",fastForwardOrBackMultiple:"+fastForwardOrBackMultiple
                +",dragPlaybackPosition:"+dragPlaybackPosition+"}";
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getAudioVideoChannelNum() {
        return AudioVideoChannelNum;
    }

    public void setAudioVideoChannelNum(byte audioVideoChannelNum) {
        AudioVideoChannelNum = audioVideoChannelNum;
    }

    public byte getPlaybackControl() {
        return playbackControl;
    }

    public void setPlaybackControl(byte playbackControl) {
        this.playbackControl = playbackControl;
    }

    public byte getFastForwardOrBackMultiple() {
        return fastForwardOrBackMultiple;
    }

    public void setFastForwardOrBackMultiple(byte fastForwardOrBackMultiple) {
        this.fastForwardOrBackMultiple = fastForwardOrBackMultiple;
    }

    public String getDragPlaybackPosition() {
        return dragPlaybackPosition;
    }

    public void setDragPlaybackPosition(String dragPlaybackPosition) {
        this.dragPlaybackPosition = dragPlaybackPosition;
    }
}
