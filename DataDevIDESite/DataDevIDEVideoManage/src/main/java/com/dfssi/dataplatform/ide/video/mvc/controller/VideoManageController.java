package com.dfssi.dataplatform.ide.video.mvc.controller;


import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.video.mvc.entity.VideoManageEntity;
import com.dfssi.dataplatform.ide.video.mvc.entity.VideoMonitorListEntity;
import com.dfssi.dataplatform.ide.video.mvc.service.IVideoManageService;
import com.dfssi.dataplatform.ide.video.mvc.service.IVideoTaskAccessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author  wangk
 * @Date  2018/8/15
 * @Description 视频管理
 */
@RestController
@RequestMapping("/videomanage")
@Api(description = "视频管理")
public class VideoManageController {
    @Autowired
    private IVideoManageService videoManageService;

    @Autowired
    private IVideoTaskAccessService videoTaskAccessService;

    protected Logger logger =  LoggerFactory.getLogger(VideoManageController.class);

    /**
     * 视频基础参数设置新增，修改
     * @return
     */
    @RequestMapping(value = "saveOrUpdate", method = RequestMethod.POST )
    @ApiOperation(value = "视频基础参数设置新增，修改")
    @LogAudit
    public ResponseObj saveVideoOrUpdate(VideoManageEntity videoManageEntity){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = videoManageService.saveVideoOrUpdateModel(videoManageEntity);
        if(StringUtils.isNotEmpty(result)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败",result);
        }else{
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        }
        return responseObj;
    }

    /**
     视频管理删除操作（逻辑删除）
     */
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ApiOperation(value = "根据vin逻辑删除视频管理")
    @LogAudit
    public ResponseObj deleteVideo(String strVin){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        if(StringUtils.isBlank(strVin)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", "删除失败，缺少必传项");
            return responseObj;
        }
        String result = videoManageService.deleteVideoModel(strVin);
        if(StringUtils.isNotEmpty(result)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "删除失败", result);
        }else{
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "删除成功", "");
        }
        return responseObj;
    }

    /**
    根据vin查询车辆的视频基础参数信息
    */
    @RequestMapping(value="getList", method = RequestMethod.GET)
    @ApiOperation(value = "根据vin查询视频基础参数")
    @LogAudit
    public ResponseObj getList(String strVin){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        if(StringUtils.isEmpty(strVin)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", "缺少查询参数");
            return responseObj;
        }
        List<VideoManageEntity> list = videoManageService.getList(strVin);
        responseObj.setData(list);
        responseObj.setStatus(ResponseObj.CODE_SUCCESS, "查询成功", "");
        return responseObj;
    }

    /**
     *分页获取视频监控列表
     */
    @RequestMapping(value="getVideoMonitorList", method = RequestMethod.GET)
    @ApiOperation(value = "根据sim卡查询视频监控列表")
    @LogAudit
    public ResponseObj getVideoMonitorList(String strSim, PageParam pageParam){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        if(StringUtils.isEmpty(strSim)){
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", "缺少查询参数");
            return responseObj;
        }
        VideoMonitorListEntity videoMonitorListEntity = new VideoMonitorListEntity();
        videoMonitorListEntity.setSim(strSim);
        PageResult<VideoMonitorListEntity> pageResult = videoTaskAccessService.getVideoMonitorList(videoMonitorListEntity, pageParam);
        responseObj.setData(pageResult);
        responseObj.setStatus(ResponseObj.CODE_SUCCESS, "查询成功", "");
        return responseObj;
    }


}
