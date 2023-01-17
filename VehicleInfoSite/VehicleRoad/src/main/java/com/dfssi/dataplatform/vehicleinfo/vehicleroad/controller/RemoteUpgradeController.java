package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.common.RedisService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IRemoteControlService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IRemoteUpgradeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
import java.util.Map;
import java.util.Set;

/**
 * 远程升级管理
 *Created by hongs  2018/9/18.
 * hongs develop
 */
@Api(description="远程升级管理")
@RestController
@RequestMapping("/fileDown")
public class RemoteUpgradeController {

    protected Logger logger = LoggerFactory.getLogger(RemoteUpgradeController.class);

    @Autowired
    private IRemoteUpgradeService remoteUpgradeService;

    @Value("${access.service.url}")
    private String accessServiceUrl;

    @Value("${getRedisValue.retryTimes}")
    private int retryTimes;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IRemoteUpgradeService IRemoteUpgradeService;



    /**
     * 远程升级文件下发
     * @param entity
     * @return
     */
    @PostMapping("/doDown1")
    @LogAudit
    public ResponseObj handoutInstruction(@RequestBody Req_8108_Entity entity){
        logger.info("远程升级参数："+entity.toString());
        String serverUrl=accessServiceUrl + "/service/command";
//        String serverUrl="http://127.0.0.1:10009/service/command";

        //指令下发内部转发
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        Req_8108 sendToMasterReq8108 = null;
        try {
            sendToMasterReq8108 = IRemoteUpgradeService.getReq_8108(entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析入参失败，请检查入参是否符合规范",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"入参校验失败","");
            return responseObj;
        }

        try {
            JSONObject  json=  restTemplate.postForObject(serverUrl,sendToMasterReq8108,JSONObject.class);
//            JSONObject json1= restTemplate.postForObject(serverUrl,sendToMasterReq8108,JSONObject.class);
            System.out.println(json);
            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"向master发送请求失败","");
            return responseObj;
        }

        //查询redis 响应
        String queryKey="E103_"+entity.getSim();
        Object res=IRemoteUpgradeService.query0108Response(queryKey);
        responseObj.setData(res);
        responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");

       return  responseObj;
    }



    /**
     * 远程升级文件下发
     * @param entity
     * @return
     */
    @PostMapping("/doDown2")
    @LogAudit
    public ResponseObj doDown(@RequestBody Req_E103_Entity entity){
        logger.info("远程升级参数："+entity.toString());
//        accessServiceUrl += "/service/command";
        String serverUrl=accessServiceUrl+"/service/command";

        ResponseObj responseObj=  ResponseObj.createResponseObj();


        Req_E103 sendToMasterReqE103 = null;
        try {
            sendToMasterReqE103 = IRemoteUpgradeService.getReq_E103(entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析入参失败，请检查入参是否符合规范",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"入参校验失败","");
            return responseObj;
        }
        String timeStamp = sendToMasterReqE103.getTimestamp();
        try {
            JSONObject  json=  restTemplate.postForObject(serverUrl,sendToMasterReqE103,JSONObject.class);
            System.out.println(json);
            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"向master发送请求失败","");
            return responseObj;
        }

        //查询redis 响应 异步执行，当车机回复报文出现延迟时 这里查询geode会查不到东西 做延时等待操作
        String redisE103Key="E103_"+entity.getSim()+"_"+timeStamp+"_res";
        String  value = null;
        try {
            value = redisService.getRedisValue(redisE103Key,retryTimes);
            logger.info("查询geode返回结果redisE103Key："+redisE103Key+",value:"+value);
        } catch (Exception e) {
            logger.error("获取远程升级指令下发结果异常，请检查geode库",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"获取远程升级指令下发结果异常，请检查geode库是否正常","");
            redisService.removeStringValue(redisE103Key);
            logger.info("删除geode中指令返回结果成功，redisE103Key："+redisE103Key);
            return responseObj;
        }
        //将value转换为Items
        if("fail".equals(value) || "exception".equals(value)){
            logger.info("client下发远程升级指令失败，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"远程升级指令失败","client下发远程升级指令失败，result:"+value);
        }else if("notLogin".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("远程升级指令下发失败,请检查vid和sim是否正确，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+value);
        }else if("nothing".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("指令下发成功但是5S内未查到返回结果，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询远程升级指令下发结果失败","指令下发成功但是"+retryTimes+"S内未查到返回结果，result:"+value);
        }else{
            logger.info("获取到远程升级指令下发结果value:"+value);
            responseObj.setData(value);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        }
        if(!"nothing".equals(value)){
            boolean bl = redisService.removeStringValue(redisE103Key);
            if(bl == true){
                logger.info("远程升级指令下发结果在redis中移除成功，redisE103Key："+redisE103Key);
            }else{
                logger.info("远程升级指令下发结果在redis中移除失败，redisE103Key："+redisE103Key);
            }
        }else{
            logger.info("在redis中未查询到终端参数设置指令下发结果的key:"+redisE103Key);
        }
        return  responseObj;
    }



    /**
     * 远程升级文件下发
     * @param entity
     * @return
     */
    @PostMapping("/doDown")
    @LogAudit
    public ResponseObj doDown2(@RequestBody Req_E103_Entity entity){
        logger.info("远程升级文件指令下发："+entity.toString());
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        if(StringUtils.isBlank(entity.getVid())||StringUtils.isBlank(entity.getSim())||StringUtils.isBlank(entity.getParamValue())){
            logger.error("参数校验错误 sim vid filePath不能为空");
            HashMap data = new HashMap();
            data.put("result","param exception");
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "参数校验错误", "sim vid  filePath 不能为空");
            return responseObj;
        }

        Req_E103 sendToMasterReqE103 = null;
        String redisE103key ="E103_"+entity.getSim()+"_"+entity.getParamId()+"_res";
        String  resStr=null;

        try {
            String endPoint = accessServiceUrl +  "/service/command";

            sendToMasterReqE103 = IRemoteUpgradeService.getReq_E103(entity);
            logger.info("开始向master发送查询终端参数命令:"+sendToMasterReqE103.toString());

            JSONObject json = restTemplate.postForObject(endPoint,sendToMasterReqE103,JSONObject.class);

            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                HashMap data = new HashMap();
                data.put("result","fail");
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
//            redisService.setStringValue("FTPID:SIM:18271913350","18271913350@spf.dbc@/var/ftp/pub/ndypt/application/roadVehicle/upload/");
//String aaaa=redisService.getRedisValue("FTPID:SIM:18271913350",1);
//            redisService.setStringValue(redisE103key,"download success");
            //等待返回
            if(entity.getIsWaiting()==0) {
                Thread.sleep(1500);
                resStr = redisService.getRedisValue(redisE103key,1);
            }else {
                Thread.sleep(2000);
                //一直去取值
                resStr = redisService.getRedisValue(redisE103key, 60);
            }
            //将value转换为Items
            if("fail".equals(resStr) || "exception".equals(resStr)){
                logger.info("client下发远程升级指令失败，result:"+resStr);
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"远程升级指令失败","client下发远程升级指令失败，result:"+resStr);
                 }else if("notLogin".equals(resStr)){
                    //下发成功但是5S内未查到结果
                    logger.info("查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
                    HashMap data = new HashMap();
                    data.put("result",resStr);
                    responseObj.setData(data);
                    responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
//                    redisService.removeStringValue(redisE103key);
                }else if("nothing".equals(resStr)){
                //下发成功但是5S内未查到结果
                logger.info("指令下发成功但是5S内未查到返回结果，result:"+resStr);
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询远程升级指令下发结果失败","指令下发成功但是5S内未查到返回结果，result:"+resStr);
            }else{
                logger.info("获取到远程升级指令下发结果value:"+resStr);
                responseObj.setData(resStr);
                if(resStr.equals("download success")){
                    responseObj.setStatus(ResponseObj.CODE_SUCCESS,"下载成功","");
                }else if(resStr.equals("download exception")){
                    responseObj.setStatus(ResponseObj.CODE_SUCCESS,"下载异常","");
                }else {

                }
            }
            if(!"nothing".equals(resStr)){
                boolean bl = redisService.removeStringValue(redisE103key);
                if(bl == true){
                    logger.info("远程升级指令下发结果在redis中移除成功，redisE103Key："+redisE103key);
                }else{
                    logger.info("远程升级指令下发结果在redis中移除失败，redisE103Key："+redisE103key);
                }
            }else{
                logger.info("在redis中未查询到终端参数设置指令下发结果的key:"+redisE103key);
            }
        }catch (Exception e){
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "指令下发失败", e.getMessage());
        }
            return  responseObj;
    }
}
