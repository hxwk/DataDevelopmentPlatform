package com.dfssi.dataplatform.ide.video.mvc.entity;

import lombok.Data;

/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description
 */
@Data
public class Jts_9101_Nd_TaskEntity {
    private String id;
    private String sim;//sim卡号
    private String ipLength;//ip长度
    private String ip;//ip
    private String tcpPort;//tcp端口
    private String udpPort;//udp端口
    private String channelNo;//通道号
    private String dataType;//数据类型
    private String streamType;//码流类型
    private String instruction;//指令
    private String business;
    private String vid;
    private String vin;//车辆唯一标识
    private String lpn;//车牌号
}
