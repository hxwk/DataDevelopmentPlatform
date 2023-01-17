package com.dfssi.dataplatform.ide.video.mvc.service;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.video.mvc.entity.*;


/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description web前端指令下发
 */
public interface IVideoTaskAccessService {
    /**
     * 视频监控列表指令转发
     * @param jts9205TaskEntity
     * @return
     */
    Jts_9205_TaskEntity getJts9205TaskEntity(Jts_9205_TaskEntity jts9205TaskEntity);

    /**
     * 实时视频控制设置指令转发（控制指令）
     * @param jts9102NdTaskEntity
     * @return
     */
    Jts_9102_Nd_TaskEntity getJts9102NdTaskEntity(Jts_9102_Nd_TaskEntity jts9102NdTaskEntity);

    /**
     * 实时视频参数设置指令转发（传输指令）
     * @param jts9101NdTaskEntity
     * @return
     */
    Jts_9101_Nd_TaskEntity getJts9101NdTaskEntity(Jts_9101_Nd_TaskEntity jts9101NdTaskEntity);

    /**
     * 回放视频参数设置指令转发（控制指令）
     * @param jts9202TaskEntity
     * @return
     */
    Jts_9202_TaskEntity getJts9202TaskEntity(Jts_9202_TaskEntity jts9202TaskEntity);

    /**
     * 回放视频参数设置指令转发（传输指令）
     * @param jts9201TaskEntity
     * @return
     */
    Jts_9201_TaskEntity getJts9201TaskEntity(Jts_9201_TaskEntity jts9201TaskEntity);

    /**
     * 视频监控列表的播放按钮，同时回放多个视频的指令转发（回放传输指令）
     * @param jobj
     * @param jts9201TaskEntity
     * @return
     */
    Jts_9201_TaskEntity getJts9201TaskEntityList(JSONObject jobj, Jts_9201_TaskEntity jts9201TaskEntity);

    /**
     * 视频监控列表从kafka取数据存入mysql
     * @param videoMonitorListEntity
     * @return
     */
    int insert(VideoMonitorListEntity videoMonitorListEntity);

    /**
     * 分页查询视频监控列表
     * @param videoMonitorListEntity
     * @return
     */
    PageResult<VideoMonitorListEntity> getVideoMonitorList(VideoMonitorListEntity videoMonitorListEntity, PageParam pageParam);

    /**
     * 视频监控列表删除（数据删除，不是逻辑删除）
     * @param videoMonitorListEntity
     * @return
     */
    int deleteList(VideoMonitorListEntity videoMonitorListEntity);

}
