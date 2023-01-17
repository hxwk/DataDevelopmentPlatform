package com.dfssi.dataplatform.ide.video.mvc.service;

import com.dfssi.dataplatform.ide.video.mvc.entity.VideoManageEntity;

import java.util.List;

/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description 视频基础参数设置增删改查
 */
public interface IVideoManageService {

    /**
     * 视频基础参数设置保存,修改
     * @param videoManageEntity
     * @return
     */
    String saveVideoOrUpdateModel(VideoManageEntity videoManageEntity);

    /**
     * 视频基础参数设置删除（逻辑删除）
     * @param strVin
     * @return
     */
    String deleteVideoModel(String strVin);

    /**
     * vin查询视频基础参数设置
     * @param strVin
     * @return
     */
    List<VideoManageEntity> getList(String strVin);


}
