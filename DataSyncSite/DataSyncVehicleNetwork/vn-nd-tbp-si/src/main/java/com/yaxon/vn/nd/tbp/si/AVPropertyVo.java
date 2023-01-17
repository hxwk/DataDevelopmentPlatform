package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.List;

/*
音视频属性基类
*/
public class AVPropertyVo implements Serializable {

    private String id;
    private String vid;
    private String sim;
    private String audioEncodeMode;     //音频编码方式
    private String audioApology;  //音频声道歉
    private String samplRate; //输入音频采样率
    private String samplNumber;  //输入音频采样位数
    private int audioFrameLength;   //音频帧长度
    private String isOutputSupported;  //是否支持音频输出
    private String videoEncodeMode; // 视频编码方式
    private String supportMaxAudioChannelNum;   //终端支持的最大音频物理通道数量
    private String supportMaxVideoChannelNum;  //终端支持的最大视频物理通道数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getAudioEncodeMode() {
        return audioEncodeMode;
    }

    public void setAudioEncodeMode(String audioEncodeMode) {
        this.audioEncodeMode = audioEncodeMode;
    }

    public String getAudioApology() {
        return audioApology;
    }

    public void setAudioApology(String audioApology) {
        this.audioApology = audioApology;
    }

    public String getSamplRate() {
        return samplRate;
    }

    public void setSamplRate(String samplRate) {
        this.samplRate = samplRate;
    }

    public String getSamplNumber() {
        return samplNumber;
    }

    public void setSamplNumber(String samplNumber) {
        this.samplNumber = samplNumber;
    }

    public int getAudioFrameLength() {
        return audioFrameLength;
    }

    public void setAudioFrameLength(int audioFrameLength) {
        this.audioFrameLength = audioFrameLength;
    }

    public String getIsOutputSupported() {
        return isOutputSupported;
    }

    public void setIsOutputSupported(String isOutputSupported) {
        this.isOutputSupported = isOutputSupported;
    }

    public String getVideoEncodeMode() {
        return videoEncodeMode;
    }

    public void setVideoEncodeMode(String videoEncodeMode) {
        this.videoEncodeMode = videoEncodeMode;
    }

    public String getSupportMaxAudioChannelNum() {
        return supportMaxAudioChannelNum;
    }

    public void setSupportMaxAudioChannelNum(String supportMaxAudioChannelNum) {
        this.supportMaxAudioChannelNum = supportMaxAudioChannelNum;
    }

    public String getSupportMaxVideoChannelNum() {
        return supportMaxVideoChannelNum;
    }

    public void setSupportMaxVideoChannelNum(String supportMaxVideoChannelNum) {
        this.supportMaxVideoChannelNum = supportMaxVideoChannelNum;
    }
    @Override
    public String toString() {
        return "AVResourceListVo{" +
                "id='" + id + '\'' +
                ", vid='" + vid + '\'' +
                ", sim='" + sim + '\'' +
                ", audioEncodeMode='" + audioEncodeMode + '\'' +
                ", audioApology='" + audioApology + '\'' +
                ", samplRate='" + samplRate + '\'' +
                ", samplNumber='" + samplNumber + '\'' +
                ", audioFrameLength='" + audioFrameLength + '\'' +
                ", isOutputSupported='" + isOutputSupported + '\'' +
                ", videoEncodeMode='" + videoEncodeMode + '\'' +
                ", supportMaxAudioChannelNum='" + supportMaxAudioChannelNum + '\'' +
                ", supportMaxVideoChannelNum='" + supportMaxVideoChannelNum + '\'' +
                '}';
    }
}
