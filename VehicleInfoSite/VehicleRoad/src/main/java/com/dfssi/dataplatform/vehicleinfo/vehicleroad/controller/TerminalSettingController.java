package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.common.RedisService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.ITerminalSettingService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 远程终端控制与设置
 * Created by yanghs on 2018/9/12.
 * zhx develop
 */
@Api(description="终端参数查询与设置")
@RestController
@RequestMapping("/terminalSetting")
public class TerminalSettingController {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RestTemplate resttemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ITerminalSettingService terminalSettingService;

    @Value("${access.service.url}")
    private String accessServiceUrl;

    @Value("${getRedisValue.retryTimes}")
    private int retryTimes;

    /**
     * 远程终端控制与查询--终端参数设置功能
     * @param terminalSettingEntity
     * @return
     */
    @PostMapping("/paramsetting")
    public ResponseObj paramsetting(@RequestBody TerminalSettingEntity terminalSettingEntity){
        logger.info("开始进行终端参数设置："+terminalSettingEntity.toString());
        String endPoint = accessServiceUrl +"/service/command";
        //返回结果给前端应用
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        Req_8103 sendToMasterReq8103 = null;
        try {
            sendToMasterReq8103 = terminalSettingService.getReq_8103(terminalSettingEntity);
        } catch (Exception e) {
            logger.error("解析入参失败，请检查入参是否符合规范",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"入参校验失败","");
            return responseObj;
        }
        logger.info("开始向master发送终端设置参数："+sendToMasterReq8103.toString());

        try {
            JSONObject json= resttemplate.postForObject(endPoint,sendToMasterReq8103,JSONObject.class);
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
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"向master发送请求异常","");
            return responseObj;
        }
        String redis8103Key = "8103_"+terminalSettingEntity.getSim()+"_res";
        String  value = null;
        try {
            value = redisService.getRedisValue(redis8103Key,retryTimes);
        } catch (Exception e) {
            logger.error("获取到终端参数设置指令下发结果异常",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"获取到终端参数指令下发结果异常，请检查geode库","");
            return responseObj;
        }
        logger.info("获取到终端参数设置指令下发结果value:"+value);

        //将value转换为Items
        if("fail".equals(value) || "exception".equals(value)){
            logger.info("client下发查询终端指令失败，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询终端参数失败","client下发查询终端指令失败，result:"+value);
        }else if("notLogin".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("查询指令下发失败,请检查vid和sim是否正确，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+value);
        }else if("nothing".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("指令下发成功但是"+retryTimes+"S内未查到返回结果，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询终端参数失败","指令下发成功但是"+retryTimes+"S内未查到返回结果，result:"+value);
        }else{
            HashMap res = new HashMap();
            res.put("result",value);
            responseObj.setData(res);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        }
        if(!"nothing".equals(value)){
            boolean bl = redisService.removeStringValue(redis8103Key);
            if(bl == true){
                logger.info("终端参数设置指令下发结果在redis中移除成功，redis8103Key："+redis8103Key);
            }else{
                logger.info("终端参数设置指令下发结果在redis中移除失败，redis8103Key："+redis8103Key);
            }
        }else{
            logger.info("在redis中未查询到终端参数设置指令下发结果的key:"+redis8103Key);
        }
        return responseObj;
    }



    /**
     * 查询指定终端参数功能
     * 
     * @param terminalParamQuery
     * @return
     */
    @PostMapping("/querySpecifiedParam")
    public ResponseObj querySpecifiedParam(@RequestBody TerminalParamQuery terminalParamQuery){
        logger.info("开始查询指定终端参数："+terminalParamQuery.toString());
        //String serverUrl="http://127.0.0.1:10001/service/command";
        String serverUrl= accessServiceUrl+"/service/command";
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        Req_8106 sendToMasterReq8106 = null;
        try {
            sendToMasterReq8106 = terminalSettingService.getReq_8106(terminalParamQuery);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析入参失败，请检查入参是否符合规范",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"入参校验失败","");
            return responseObj;
        }
        String timeStamp = sendToMasterReq8106.getTimestamp();
        logger.info("开始向master发送查询终端参数命令:"+sendToMasterReq8106.toString());


        try {
            JSONObject json= resttemplate.postForObject(serverUrl,sendToMasterReq8106,JSONObject.class);
            System.out.println(json);
            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("向master发送请求失败，serverUrl:"+serverUrl,e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"向master发送请求失败","");
            return responseObj;
        }
        List<Item> items = new ArrayList<Item>();
        String terminalParamQuerytype = terminalParamQuery.getQueryType();
        String[] paramIdStr = terminalParamQuerytype.split(",");
        for(String paramIdTem : paramIdStr){
            if("F001".equals(paramIdTem)){
                logger.info("开始查询默认DBC文件名0xF001结果");
            }else if("F002".equals(paramIdTem)){
                logger.info("开始查询通讯模块固件版本号0xF002结果");
            }else if("F003".equals(paramIdTem)){
                logger.info("开始查询通讯模块固件版本号查询0xF003结果");
            }else if("F004".equals(paramIdTem)){
                logger.info("开始查询通讯模块APP版本号查询0xF004结果");
            }else if("F005".equals(paramIdTem)){
                logger.info("开始查询MCU boot固件版本号查询0xF005结果");
            }else if("F006".equals(paramIdTem)){
                logger.info("开始查询MCU APP版本号查询0xF006结果");
            }else if("F007".equals(paramIdTem)){
                logger.info("开始查询视频模块固件版本号查询0xF007结果");
            }else if("F008".equals(paramIdTem)){
                logger.info("开始查询视频模块APP版本号查询0xF008结果");
            }else if("F009".equals(paramIdTem)){
                logger.info("开始查询硬件版本号查询0xF009结果");
            }else if("F00A".equals(paramIdTem)){
                logger.info("开始查询ECU软件版本查询0xF00A结果");
            }else if("F00B".equals(paramIdTem)){
                logger.info("开始查询ECU硬件版本查询0xF00B结果");
            }else if("F00C".equals(paramIdTem)){
                logger.info("开始查询SD 卡目录文件名查询0xF00C结果");
            }else if("F00D".equals(paramIdTem)){
                logger.info("开始查询DBC文件0xF00D结果");
            }else if("F00E".equals(paramIdTem)){
                logger.info("开始查询MDF文件0xF00E结果");
            }else if("F00F".equals(paramIdTem)){
                logger.info("开始查询视频文件0xF00F结果");
            }else if("F010".equals(paramIdTem)){
                logger.info("开始查询音频文件0xF010结果");
            }else if("F011".equals(paramIdTem)){
                logger.info("8106不支持自定义文件(绝对路径)0xF011的查询");
            }else if("F012".equals(paramIdTem)){
                logger.info("8106不支持删除自定义文件(绝对路径)0xF012的查询");
            }else if("F013".equals(paramIdTem)){
                logger.info("开始查询数据上报周期0xF013结果");
            }else if("F014".equals(paramIdTem)){
                logger.info("开始查询终端休眠模式0xF014结果");
            }else if("F015".equals(paramIdTem)){
                logger.info("开始查询终端SD卡存储频率0xF015结果");
            }else if("F016".equals(paramIdTem)){
                logger.info("开始查询SD卡容量(M)0xF016结果");
            }else if("F017".equals(paramIdTem)){
                logger.info("开始查询终端FTP的ip和端口0xF017结果");
            }else{
                int paramIdKey = Integer.parseInt(paramIdTem,16);
                logger.info("开始进行8106终端常规参数查询，参数ID:"+paramIdKey);
            }
        }

        String redis8106Key = "8106_"+terminalParamQuery.getSim()+"_"+timeStamp+"_res";
        String  value = null;
        try {
            value = redisService.getRedisValue(redis8106Key,retryTimes);
            logger.info("查询geode返回结果redis8106Key："+redis8106Key+",value:"+value);
        } catch (Exception e) {
            logger.error("获取查询终端参数指令下发结果异常，请检查geode库",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"获取查询终端参数指令下发结果异常，请检查geode库是否正常","");
            redisService.removeStringValue(redis8106Key);
            logger.info("删除geode中指令返回结果成功，redis8106Key："+redis8106Key);
            return responseObj;
        }
        //将value转换为Items
        if("fail".equals(value) || "exception".equals(value)){
            logger.info("client下发查询终端指令失败，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询终端参数失败","client下发查询终端指令失败，result:"+value);
        }else if("notLogin".equals(value)){
            //下发成功但是5S内未查到结果
            logger.info("查询指令下发失败,请检查vid和sim是否正确，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+value);
        }else if("nothing".equals(value)){
            //下发成功但是超时时间内未查到结果
            logger.info("指令下发成功但是"+retryTimes+"S内未查到返回结果，result:"+value);
            HashMap data = new HashMap();
            data.put("result",value);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询终端参数失败","指令下发成功但是"+retryTimes+"S内未查到返回结果，result:"+value);
        }else{
            items = JSONArray.parseArray(value, Item.class);
            TerminalParamResult data = new TerminalParamResult();
            data.setParamItems(items);
            logger.info("应用查询结果成功，result:"+data);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
        }
        if(!"nothing".equals(value)){
            boolean bl = redisService.removeStringValue(redis8106Key);
            if(bl == true){
                logger.info("终端参数设置指令下发结果在redis中移除成功，redis8106Key："+redis8106Key);
            }else{
                logger.info("终端参数设置指令下发结果在redis中移除失败，redis8106Key："+redis8106Key);
            }
        }else{
            logger.info("在redis中未查询到终端参数查询指令下发结果的key:"+redis8106Key);
        }
        return responseObj;
    }


    /**
     * 查询指定终端全部参数
     * @param terminalParamQuery
     * @return
     */
    @PostMapping("/queryAllParam")
    public ResponseObj queryAllParam(@RequestBody TerminalParamQuery terminalParamQuery){
        logger.info("开始查询指定终端全部参数："+terminalParamQuery.toString());
        //String serverUrl="http://127.0.0.1:10001/service/command";
        String serverUrl= accessServiceUrl+"/service/command";

        Req_8104 sendToMasterReq8104 =terminalSettingService.getReq_8104(terminalParamQuery);

        logger.info("开始向master发送查询终端参数命令:"+sendToMasterReq8104.toString());

        //返回结果给前端应用
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        try {
            JSONObject json= resttemplate.postForObject(serverUrl,terminalParamQuery,JSONObject.class);
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
        if (1 == 1){
            String  value = redisService.getRedisValue("key",retryTimes);
            JSONObject jsonv = JSONObject.parseObject(value);
            System.out.println(jsonv.get("vid"));responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
            return responseObj;
        }else{
            responseObj.setStatus(ResponseObj.CODE_SUCCESS,"成功","");
            return responseObj;
        }
    }


    /*public static void main(String[] args) {
        int retryTimes = 0;
        String redis8106Key = "8103_18271913350_res";
        //String  value = getRedisValue(redis8106Key,retryTimes);
        HashMap res = new HashMap();
        //res.put("result",value);
    }*/
}
