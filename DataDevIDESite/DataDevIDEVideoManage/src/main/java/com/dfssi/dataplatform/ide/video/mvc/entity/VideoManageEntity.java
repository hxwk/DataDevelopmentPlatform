package com.dfssi.dataplatform.ide.video.mvc.entity;


import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

/**
 * 视频基础参数设置entity
 */
@Data
public class VideoManageEntity extends BaseVO {
    private Integer realEncodeMode;//实时流编码模式
    private Integer realResolution;//实时流分辨率
    private String realKeyFrameInterval;//实时流关键帧间隔
    private String realTargetFrameRate;//实时流目标帧率
    private String realTargetBitRate;//实时流目标码率
    private Integer memStreamEncodingMode;//存储流编码模式
    private Integer memStreamResolution;//存储流分辨率
    private String memStreamKeyFrameInterval;//存储流关键帧间隔
    private String memStreamTargetFrameRate;//存储流目标帧率
    private String memStreamTargetBitRate;//存储流目标码率
    private String osdSupertitleSettings;//OSD字幕叠加设置
    private Integer enableAudioOutput;//是否启用音频输出,0:不启用，1:启用
    private String vin;//车辆唯一标识码
    private String sim;//终端sim卡
    private String descr;//描述
}
