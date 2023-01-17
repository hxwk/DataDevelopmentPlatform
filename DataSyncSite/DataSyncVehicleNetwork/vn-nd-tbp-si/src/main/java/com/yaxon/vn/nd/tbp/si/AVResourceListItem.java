package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/6/27
 * @description
 */
public class AVResourceListItem implements Serializable {
    private short channelNo;
    private long startTime;
    private long endTime;
    private byte[] alarmMark;
    private List<String> alarmMarks;
    private byte avResourceType;
    private byte streamType;
    private byte storageType;
    private long fileSize;

    public short getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(short channelNo) {
        this.channelNo = channelNo;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public byte[] getAlarmMark() {
        return alarmMark;
    }

    public void setAlarmMark(byte[] alarmMark) {
        this.alarmMark = alarmMark;
    }

    public List<String> getAlarmMarks() {
        return alarmMarks;
    }

    public void setAlarmMarks(List<String> alarmMarks) {
        this.alarmMarks = alarmMarks;
    }

    public byte getAvResourceType() {
        return avResourceType;
    }

    public void setAvResourceType(byte avResourceType) {
        this.avResourceType = avResourceType;
    }

    public byte getStreamType() {
        return streamType;
    }

    public void setStreamType(byte streamType) {
        this.streamType = streamType;
    }

    public byte getStorageType() {
        return storageType;
    }

    public void setStorageType(byte storageType) {
        this.storageType = storageType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "AVResourceListItem{" +
                "channelNo=" + channelNo +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", alarmMark=" + Arrays.toString(alarmMark) +
                ", alarmMarks=" + alarmMarks +
                ", avResourceType=" + avResourceType +
                ", streamType=" + streamType +
                ", storageType=" + storageType +
                ", fileSize=" + fileSize +
                '}';
    }
}
