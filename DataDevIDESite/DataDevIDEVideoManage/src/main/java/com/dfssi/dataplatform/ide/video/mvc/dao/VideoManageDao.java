package com.dfssi.dataplatform.ide.video.mvc.dao;

import com.dfssi.dataplatform.ide.video.mvc.entity.VideoManageEntity;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface VideoManageDao{

    int insertOrUpdate(VideoManageEntity videoManageEntity);

    int delete(String strVin);

    List<VideoManageEntity> getList(String strVin);




}
