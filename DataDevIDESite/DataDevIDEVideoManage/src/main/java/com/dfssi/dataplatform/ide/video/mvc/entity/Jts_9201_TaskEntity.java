package com.dfssi.dataplatform.ide.video.mvc.entity;

import lombok.Data;

/**
 * @Author wangk
 * @Date 2018/8/24
 * @Description
 */
@Data
public class Jts_9201_TaskEntity {
    private String id;
    private String vin;//车辆唯一标识
    private String vid;
    private String sim;//sim卡号
    private String serverIPAddressLength;//IP地址长度
    private String serverIPAddress;//ip
    private String serverAudioVideoMonitorPortTCP;//tcp端口
    private String serverAudioVideoMonitorPortUDP;//udp端口
    private String logicalChannelNum;//通道号
    private String audioVideoResourceType;//音视频资源类型
    private String bitStreamType;//码流类型
    private String storageType;//存储类型
    private String refluxMode;//回放类型
    private String fastForwardOrBackMultiple;//快进快退
    private String startTime;
    private String endTime;

}
