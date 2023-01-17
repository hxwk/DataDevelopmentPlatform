package com.dfssi.dataplatform.ide.video.mvc.dao;

import com.dfssi.dataplatform.ide.video.mvc.entity.VideoMonitorListEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author wangk
 * @Date 2018/8/25
 * @Description
 */
@Mapper
public interface VideoMonitorListDao {

    int insert(VideoMonitorListEntity videoMonitorListEntity);

    List<VideoMonitorListEntity> getVideoMonitorList(VideoMonitorListEntity videoMonitorListEntity);

    int deleteList(VideoMonitorListEntity videoMonitorListEntity);
}
