package com.dfssi.dataplatform.ide.video.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

/**
 * @Author wangk
 * @Date 2018/8/25
 * @Description  视频监控列表的实体类
 */
@Data
public class VideoMonitorListEntity extends BaseVO {
    private String id;
    private Integer flowId;//流水号
    private Integer avResourcesSum;//音视频资源总数
    private Integer channelNo;//通道号
    private String startDate;//开始时间
    private String endDate;//结束时间
    private String alarmFlag;//报警标志
    private Integer avResourceType;//音视频资源类型
    private Integer codeStreamType;//码流类型
    private Integer memoryType;//存储器类型
    private Integer fileSize;//文件大小
    private String vin;//车辆唯一标识
    private String sim;//车辆sim卡
    private String vid;//vid
    private String descr;//描述
}
