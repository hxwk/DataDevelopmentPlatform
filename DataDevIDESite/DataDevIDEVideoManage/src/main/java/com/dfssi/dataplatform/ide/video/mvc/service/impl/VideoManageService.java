package com.dfssi.dataplatform.ide.video.mvc.service.impl;

import com.dfssi.dataplatform.ide.video.mvc.dao.VideoManageDao;
import com.dfssi.dataplatform.ide.video.mvc.entity.VideoManageEntity;
import com.dfssi.dataplatform.ide.video.mvc.service.IVideoManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value="videoManageService")
public class VideoManageService implements IVideoManageService {
    @Autowired
    private VideoManageDao videoManageDao;

    private Logger logger = LoggerFactory.getLogger(VideoManageService.class);

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String saveVideoOrUpdateModel(VideoManageEntity videoManageEntity) {
        String result = "操作失败";
        try {
            int rows = videoManageDao.insertOrUpdate(videoManageEntity);
            if(rows > 0){
                result = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String deleteVideoModel(String strVin) {
        String result = "删除失败";
        try {
            int rows = videoManageDao.delete(strVin);
            if(rows > 0 ){
                logger.debug("删除数据成功");
                result = "";
            }
        } catch (Exception e) {
            logger.error("操作失败");
            throw e;
        }
        return result;
    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<VideoManageEntity> getList(String strVin) {
        List<VideoManageEntity> list = videoManageDao.getList(strVin);
        return list;
    }


}
