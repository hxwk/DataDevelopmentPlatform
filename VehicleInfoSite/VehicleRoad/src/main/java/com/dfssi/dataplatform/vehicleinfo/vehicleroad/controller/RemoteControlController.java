package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.common.RedisService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.RemoteControlEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IRemoteControlService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.Req_8105;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * 远程控制管理
 * Created by yanghs on 2018/9/12.
 * yinl develop
 */
@Api(description="远程控制管理")
@RestController
@RequestMapping("/remote")
public class RemoteControlController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RestTemplate resttemplate;
    @Value("${access.service.url}")
    private String accessServiceUrl;

    @Value("${getRedisValue.retryTimes}")
    private int retryTimes;

    @Autowired
    private IRemoteControlService remoteControlService;


    @Autowired
    private RedisService redisService;
    /**
     * 远程控制
     * @param remoteControlEntity
     * @return
     */
    @PostMapping("/control")
    @LogAudit
    public ResponseObj remotecontrol(@RequestBody RemoteControlEntity remoteControlEntity){
        logger.info("开始进行终端远程控制："+remoteControlEntity.toString());
        String serverUrl=accessServiceUrl+"/service/command";
        //返回结果给前端应用
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        Req_8105 sendToMasterReq8105 = null;
        try {
            sendToMasterReq8105 = remoteControlService.getReq_8105(remoteControlEntity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析入参失败，请检查入参是否符合规范",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"入参校验失败","");
            return responseObj;
        }

        logger.info("开始向master发送远程控制命令:"+sendToMasterReq8105.toString());
        String redis8105Key = "8105_"+remoteControlEntity.getSim()+"_"+remoteControlEntity.getCommandType();

        try {
            JSONObject json= resttemplate.postForObject(serverUrl,sendToMasterReq8105,JSONObject.class);
            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("向master发送终端远程控制请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"向master发送终端远程控制请求失败","");
            return responseObj;
        }


        String  value = null;
        try {
            value = redisService.getRedisValue(redis8105Key,retryTimes);
            logger.info("查询geode返回结果redis8105Key："+redis8105Key+",value:"+value);
        } catch (Exception e) {
            logger.error("获取终端远程控制指令下发结果异常，请检查geode库",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"获取终端远程控制指令下发结果异常，请检查geode库是否正常","");
            redisService.removeStringValue(redis8105Key);
            logger.info("删除geode中指令返回结果成功，redis8105Key："+redis8105Key);
            return responseObj;
        }
        if("fail".equals(value) || "exception".equals(value)){
            logger.info("client下发终端远程控制指令失败，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"下发终端远程控制指令失败","client下发终端远程控制指令失败，result:"+value);
        }else if("notLogin".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("远程控制指令下发失败,请检查vid和sim是否正确，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","远程控制指令下发失败,请检查vid和sim是否正确，result:"+value);
        }else if("nothing".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("远程控制指令下发成功但是"+retryTimes+"S内未查到返回结果，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询远程控制指令执行结果失败","远程控制指令下发成功但是"+retryTimes+"未查到返回结果，result:"+value);
        }else{
            HashMap<String,String> dataValue = new HashMap<>();
            dataValue.put(redis8105Key,value);
            logger.info("查询远程控制指令执行结果成功，result:"+dataValue);
            responseObj.setData(dataValue);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        }
        if(!"nothing".equals(value)){
            boolean bl = redisService.removeStringValue(redis8105Key);
            if(bl == true){
                logger.info("终端参数设置指令下发结果在redis中移除成功，redis8105Key："+redis8105Key);
            }else{
                logger.info("终端参数设置指令下发结果在redis中移除失败，redis8105Key："+redis8105Key);
            }
        }else{
            logger.info("在redis中未查询到终端远程控制指令下发结果的key:"+redis8105Key);
        }
        return responseObj;
    }
}
