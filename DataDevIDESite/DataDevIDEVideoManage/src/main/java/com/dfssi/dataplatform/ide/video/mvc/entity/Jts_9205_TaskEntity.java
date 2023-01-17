package com.dfssi.dataplatform.ide.video.mvc.entity;

import lombok.Data;

/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description
 */
@Data
public class Jts_9205_TaskEntity {
    private String id;
    private String sim; //sim卡号
    private String logicalChannelNum;//通道号
    private String startTime; //开始时间
    private String endTime;//结束时间
    private String alarmSign;//报警指示
    private String audioVideoResourceType;//音视频资源类型
    private String bitStreamType;//码流类型
    private String storageType;//存储类型
    private String vid;
    private String vin;//车辆唯一标识
    private String lpn;//车牌号
}
