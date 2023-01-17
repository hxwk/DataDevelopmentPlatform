package com.dfssi.dataplatform.ide.video.mvc.entity;

import lombok.Data;

/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description
 */
@Data
public class Jts_9102_Nd_TaskEntity {
    private String id;
    private String sim;//sim卡号
    private String vid;
    private String vin;//车辆唯一标识
    private String lpn;//车牌号
    private String channelNo;//通道号
    private String controlCommand;//控制指令
    private String shutDownAVType;//关闭音视频类型
    private String streamType;//切换码流类型

}
