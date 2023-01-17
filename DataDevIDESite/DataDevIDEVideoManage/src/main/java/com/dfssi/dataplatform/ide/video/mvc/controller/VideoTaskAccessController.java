package com.dfssi.dataplatform.ide.video.mvc.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.video.mvc.entity.*;
import com.dfssi.dataplatform.ide.video.mvc.service.IVideoTaskAccessService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


/**
 * @Author wangk
 * @Date 2018/8/22
 * @Description web指令下发
 */
@RestController
@RequestMapping("/videotaskaccess")
@Api(description = "web指令下发")
public class VideoTaskAccessController {

    @Value("${access.service.url}")
    private  String serviceUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private IVideoTaskAccessService videoTaskAccessService;

    /**
     *视频监控列表指令下发
     * @param jts9205TaskEntity
     * @return
     */
    @RequestMapping(value = "getJts9205Task" , method = RequestMethod.POST)
    @ApiOperation(value = "视频监控列表指令下发")
    @LogAudit
    public ResponseObj getJts9205Task(Jts_9205_TaskEntity jts9205TaskEntity){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String serverUrl =serviceUrl+"/service/command";
        JSONObject jsonObject = restTemplate.postForObject(serverUrl, videoTaskAccessService.getJts9205TaskEntity(jts9205TaskEntity), JSONObject.class);
        if(Objects.nonNull(jsonObject)){
           responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功","");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败","失败");
        }
        responseObj.setData(jsonObject);
        return responseObj;
    }

    /**
     *实时视频参数设置（控制指令）
     * @param jts9102NdTaskEntity
     * @return
     */
    @RequestMapping(value ="getJts9102NdTask" , method = RequestMethod.POST)
    @ApiOperation(value = "实时视频控制指令参数设置")
    @LogAudit
    public ResponseObj getJts9102NdTask(Jts_9102_Nd_TaskEntity jts9102NdTaskEntity){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String serverUrl =serviceUrl+"/service/command";
        JSONObject jsonObject = restTemplate.postForObject(serverUrl, videoTaskAccessService.getJts9102NdTaskEntity(jts9102NdTaskEntity), JSONObject.class);
        if(Objects.nonNull(jsonObject)){
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功","");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败","失败");
        }
        responseObj.setData(jsonObject);
        return responseObj;
    }

    /**
     *实时视频参数设置（传输指令）
     * @param jts9101NdTaskEntity
     * @return
     */
    @RequestMapping(value ="getJts9101NdTask" , method = RequestMethod.POST)
    @ApiOperation(value = "实时视频传输指令参数设置")
    @LogAudit
    public ResponseObj getJts9101NdTask(Jts_9101_Nd_TaskEntity jts9101NdTaskEntity){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String serverUrl =serviceUrl+"/service/command";
        JSONObject jsonObject = restTemplate.postForObject(serverUrl, videoTaskAccessService.getJts9101NdTaskEntity(jts9101NdTaskEntity), JSONObject.class);
        //jsonObject={"rc":1,"errMsg":"master未能向客户端发送命令，请检查客户端是否存在"}
        //jsonObject={"data":"","status":{"msg":"","code":"200","details":""}}}
        if(Objects.nonNull(jsonObject)){
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功","");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败","失败");
        }
        responseObj.setData(jsonObject);
        return responseObj;
    }

    /**
     *回放视频参数设置（控制指令）
     * @param jts9202TaskEntity
     * @return
     */
    @RequestMapping(value ="getJts9202Task" , method = RequestMethod.POST)
    @ApiOperation(value = "回放视频控制指令参数设置")
    @LogAudit
    public ResponseObj getJts9202Task(Jts_9202_TaskEntity jts9202TaskEntity){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String serverUrl =serviceUrl+"/service/command";
        JSONObject jsonObject = restTemplate.postForObject(serverUrl, videoTaskAccessService.getJts9202TaskEntity(jts9202TaskEntity), JSONObject.class);
        if(Objects.nonNull(jsonObject)){
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功","");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败","失败");
        }
        responseObj.setData(jsonObject);
        return responseObj;
    }

    /**
     *回放视频参数设置（传输指令）
     * @param
     * @return
     */
    @RequestMapping(value ="getJts9201Task" , method = RequestMethod.POST)
    @ApiOperation(value = "回放视频传输指令参数设置")
    @LogAudit
    public ResponseObj getJts9201Task(Jts_9201_TaskEntity jts9201TaskEntity){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String serverUrl =serviceUrl+"/service/command";
        JSONObject jsonObject = restTemplate.postForObject(serverUrl, videoTaskAccessService.getJts9201TaskEntity(jts9201TaskEntity), JSONObject.class);
        if(Objects.nonNull(jsonObject)){
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功","");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败","失败");
        }
        responseObj.setData(jsonObject);
        return responseObj;
    }

    /**
     *视频监控列表的播放按钮 ，同时回放多个视频的指令转发（回放视频传输指令）
     * @param
     * @return
     */
    @RequestMapping(value ="getJts9201TaskList" , method = RequestMethod.POST)
    @ApiOperation(value = "回放视频传输指令参数设置")
    @LogAudit
    public ResponseObj getJts9201TaskList(@RequestBody String str){
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String serverUrl =serviceUrl+"/service/command";
        JSONObject jObj = JSON.parseObject(str);
        JSONArray jsonArray = jObj.getJSONArray("arrData");
        Jts_9201_TaskEntity jts9201taskEntity = new Jts_9201_TaskEntity();
        JSONObject jsonObject = null;
        for(int i = 0; i<jsonArray.size(); i++){
            JSONObject  joj = jsonArray.getJSONObject(i);
            jsonObject = restTemplate.postForObject(serverUrl, videoTaskAccessService.getJts9201TaskEntityList(joj, jts9201taskEntity), JSONObject.class);
        }
        if(Objects.nonNull(jsonObject)){
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功","");
        }else{
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败","失败");
        }
        responseObj.setData(jsonObject);
        return responseObj;
    }


}
