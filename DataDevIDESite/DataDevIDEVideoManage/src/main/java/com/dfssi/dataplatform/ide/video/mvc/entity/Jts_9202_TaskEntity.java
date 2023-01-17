package com.dfssi.dataplatform.ide.video.mvc.entity;

import lombok.Data;

/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description
 */
@Data
public class Jts_9202_TaskEntity {
    private String id;
    private String sim;//sim卡号
    private String AudioVideoChannelNum;//音视频通道号
    private String playbackControl;//回放控制
    private String fastForwardOrBackMultiple;//快进快退
    private String dragPlaybackPosition;//拖动回放位置
    private String vid;
    private String vin;//车辆唯一标识

}
