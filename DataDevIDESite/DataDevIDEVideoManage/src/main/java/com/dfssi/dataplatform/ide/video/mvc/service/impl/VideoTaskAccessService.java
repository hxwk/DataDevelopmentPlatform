package com.dfssi.dataplatform.ide.video.mvc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.video.mvc.constants.Constants;
import com.dfssi.dataplatform.ide.video.mvc.dao.VideoMonitorListDao;
import com.dfssi.dataplatform.ide.video.mvc.entity.*;
import com.dfssi.dataplatform.ide.video.mvc.service.IVideoTaskAccessService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description
 */
@Service(value = "videoTaskAccessService")
public class VideoTaskAccessService implements IVideoTaskAccessService {
    @Autowired
    private VideoMonitorListDao videoMonitorListDao;

    /**
    视频监控列表
     */
    @Override
    public Jts_9205_TaskEntity getJts9205TaskEntity(Jts_9205_TaskEntity jts9205TaskEntity) {
        if(Objects.isNull(jts9205TaskEntity)){
            return null;
        }else{
            jts9205TaskEntity.setId(jts9205TaskEntity.getId() == null?"jts.9205":jts9205TaskEntity.getId());
            String sTime = jts9205TaskEntity.getStartTime();
            String startTime = null;
            if(StringUtils.isNotBlank(sTime) && sTime.length() >= 3){
                //将2018-08-08 12：12：15类型的字符串转为20180808121215
                String s = sTime.trim().replaceAll("[[\\s-:punct:]]","");
                startTime = s.substring(2);
            }
            jts9205TaskEntity.setStartTime(startTime);
            String eTime = jts9205TaskEntity.getEndTime();
            String endTime = null;
            if(StringUtils.isNotBlank(eTime) && eTime.length() >= 3){
                String es = eTime.trim().replaceAll("[[\\s-:punct:]]","");
                endTime = es.substring(2);
            }
            jts9205TaskEntity.setEndTime(endTime);
            jts9205TaskEntity.setAlarmSign(jts9205TaskEntity.getAlarmSign() == null?"0":jts9205TaskEntity.getAlarmSign());
            jts9205TaskEntity.setBitStreamType(jts9205TaskEntity.getBitStreamType() == null?"1":jts9205TaskEntity.getBitStreamType());
            return jts9205TaskEntity;
        }
    }

    /**
    实时视频参数设置(控制)
     */
    @Override
    public Jts_9102_Nd_TaskEntity getJts9102NdTaskEntity(Jts_9102_Nd_TaskEntity jts9102NdTaskEntity){
        if(Objects.isNull(jts9102NdTaskEntity)){
            return null;
        }else{
            jts9102NdTaskEntity.setId(jts9102NdTaskEntity.getId() == null?"jts.9102.nd":jts9102NdTaskEntity.getId());
            return jts9102NdTaskEntity;
        }
    }

    /**
    实时视频参数设置（传输）
     */
    @Override
    public Jts_9101_Nd_TaskEntity getJts9101NdTaskEntity(Jts_9101_Nd_TaskEntity jts9101NdTaskEntity){
        if(Objects.isNull(jts9101NdTaskEntity)){
            return null;
        }else{
            jts9101NdTaskEntity.setId(jts9101NdTaskEntity.getId() == null?"jts.9101.nd":jts9101NdTaskEntity.getId());
            String sip = jts9101NdTaskEntity.getIp() == null?"0":jts9101NdTaskEntity.getIp();
            jts9101NdTaskEntity.setIpLength(String.valueOf(sip.length()));
            return  jts9101NdTaskEntity;
        }
    }

    /**
    回放视频参数设置（控制）
     */
    @Override
    public Jts_9202_TaskEntity getJts9202TaskEntity(Jts_9202_TaskEntity jts9202TaskEntity) {
        if(Objects.isNull(jts9202TaskEntity)){
            return null;
        }else{
            jts9202TaskEntity.setId(jts9202TaskEntity.getId() == null?"jts.9202":jts9202TaskEntity.getId());
            String str = jts9202TaskEntity.getDragPlaybackPosition();
            String dragPlaybackPosition = null;
            if(StringUtils.isNotEmpty(str)){
                dragPlaybackPosition = str.trim().replaceAll(" ", "-").replaceAll(":","-");
            }
            jts9202TaskEntity.setDragPlaybackPosition(dragPlaybackPosition);
            return jts9202TaskEntity;
        }
    }

    /**
    回放视频参数设置（传输）
     */
    @Override
    public Jts_9201_TaskEntity getJts9201TaskEntity(Jts_9201_TaskEntity jts9201TaskEntity) {
        if(Objects.isNull(jts9201TaskEntity)){
            return null;
        }else{
            jts9201TaskEntity.setId(jts9201TaskEntity.getId() == null?"jts.9201":jts9201TaskEntity.getId());
            String sip = jts9201TaskEntity.getServerIPAddress() == null?"0":jts9201TaskEntity.getServerIPAddress();
            jts9201TaskEntity.setServerIPAddressLength(String.valueOf(sip.length()));
            String sTime = jts9201TaskEntity.getStartTime();
            String startTime = null;
            if(StringUtils.isNotBlank(sTime) && sTime.length() >= 3){
                String s = sTime.trim().replaceAll("[[\\s-:punct:]]","");
                startTime = s.substring(2);
            }
            jts9201TaskEntity.setStartTime(startTime);
            String eTime = jts9201TaskEntity.getEndTime();
            String endTime = null;
            if(StringUtils.isNotBlank(eTime) && eTime.length() >=3){
                String es = eTime.trim().replaceAll("[[\\s-:punct:]]","");
                endTime = es.substring(2);
            }
            jts9201TaskEntity.setEndTime(endTime);
            return jts9201TaskEntity;
        }
    }

    /**
     *多视频回放
     * @param jobj
     * @param jts9201taskEntity
     * @return
     */
    @Override
    public Jts_9201_TaskEntity getJts9201TaskEntityList(JSONObject jobj, Jts_9201_TaskEntity jts9201taskEntity){
        jts9201taskEntity.setId(Constants.JTS_9201_ID);
        //页面无法提供参数，所以采用默认的写法
        jts9201taskEntity.setServerIPAddress(Constants.JTS_9201_SERVER_IPADDRESS);
        jts9201taskEntity.setServerIPAddressLength(String.valueOf(Constants.JTS_9201_SERVER_IPADDRESS.length()));
        jts9201taskEntity.setRefluxMode(Constants.S_ZERO);
        jts9201taskEntity.setFastForwardOrBackMultiple(Constants.S_ZERO);
        jts9201taskEntity.setServerAudioVideoMonitorPortTCP(Constants.JTS_9201_TCP_PORT);
        jts9201taskEntity.setServerAudioVideoMonitorPortUDP(Constants.S_ZERO);
        jts9201taskEntity.setLogicalChannelNum(jobj.getString("logicalChannelNum"));
        jts9201taskEntity.setSim(jobj.getString("sim"));
        jts9201taskEntity.setVin(jobj.getString("vin"));
        jts9201taskEntity.setVid(jobj.getString("vid"));
        jts9201taskEntity.setAudioVideoResourceType(jobj.getString("avResourceType"));
        jts9201taskEntity.setBitStreamType(jobj.getString("codeStreamType"));
        jts9201taskEntity.setStorageType(jobj.getString("memoryType"));
        String sTime = jobj.getString("startTime");
        String startTime = null;
        if(StringUtils.isNotBlank(sTime) && sTime.length() >= 3){
            String s = sTime.trim().replaceAll("[[\\s-:punct:]]","");
            startTime = s.substring(2);
        }
        jts9201taskEntity.setStartTime(startTime);
        String eTime = jobj.getString("endTime");
        String endTime = null;
        if(StringUtils.isNotBlank(eTime) && eTime.length() >=3){
            String es = eTime.trim().replaceAll("[[\\s-:punct:]]","");
            endTime = es.substring(2);
        }
        jts9201taskEntity.setEndTime(endTime);
        return jts9201taskEntity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insert(VideoMonitorListEntity videoMonitorListEntity) {
        int rows = videoMonitorListDao.insert(videoMonitorListEntity);
        return rows;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<VideoMonitorListEntity> getVideoMonitorList(VideoMonitorListEntity videoMonitorListEntity, PageParam pageParam) {
        Page<VideoMonitorListEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<VideoMonitorListEntity> list = videoMonitorListDao.getVideoMonitorList(videoMonitorListEntity);
        PageResult<VideoMonitorListEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int deleteList(VideoMonitorListEntity videoMonitorListEntity) {
        return videoMonitorListDao.deleteList(videoMonitorListEntity);
    }

}
