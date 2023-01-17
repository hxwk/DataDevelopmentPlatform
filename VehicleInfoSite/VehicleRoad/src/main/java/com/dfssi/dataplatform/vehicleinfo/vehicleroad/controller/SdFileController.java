package com.dfssi.dataplatform.vehicleinfo.vehicleroad.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.common.RedisService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SDFileEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SdDirQuery;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SdFileQuery;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.ISdFileService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.DirectionUtils;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * @ClassName SDFileController
 * @Description TODO
 * @Author chenf
 * @Date 2018/9/27
 * @Versiion 1.0
 **/
@Api(description="SD卡目录文件查询")
@RestController
public class SdFileController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ISdFileService sdFileService;
    @Autowired
    private RedisService redisService;

    @Value("${access.service.url}")
    private String accessServiceUrl;

    @Value("${getRedisValue.retryTimes}")
    private int retryTimes;

    private RestTemplate restTemplate =new RestTemplate();


    /**
     * sd目录查询
     * @param fileparam
     * @return
     */
    @PostMapping("/directory/doQuery")
    public ResponseObj readDir(@RequestBody SDFileEntity fileparam){

        logger.info("sd目录查询："+fileparam.toString());
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        if(StringUtils.isBlank(fileparam.getVid())||StringUtils.isBlank(fileparam.getSim())){
            logger.error("参数校验错误 sim vid 不能为空");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "参数校验错误", "sim vid  不能为空");
            return responseObj;
        }


        try {
            String endPoint = accessServiceUrl +  "/service/command";
            SdDirQuery reqE001 = new SdDirQuery();
            int[] paramids ={0xF00C};

            reqE001.setVid(fileparam.getVid());
            reqE001.setSim(fileparam.getSim());
            reqE001.setParamIds(paramids);
            logger.info("开始向master发送查询终端参数命令:"+reqE001.toString());

            JSONObject json = restTemplate.postForObject(endPoint, reqE001, JSONObject.class);
            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }

            //等待返回
            Thread.sleep(1500);
            String redisE101key ="E101_"+fileparam.getSim();
            //String redisE101key =""+fileparam.getVid()+"_SDDIR";
            String  resStr= redisService.getRedisValue(redisE101key,retryTimes);

            if("notLogin".equals(resStr)){
                //下发成功但是5S内未查到结果
                logger.info("查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
            }
            else if("exception".equals(resStr)||"fail".equals(resStr)){
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询终端SD卡目录失败","client下发查询终端指令失败，result:"+resStr);

            }
            else if("nothing".equals(resStr)){
                //下发成功但是5S内未查到结果
                int time = fileparam.getIsWaiting()==0?5:60;
                logger.info("指令下发成功但是"+time+"S内未查到返回结果，result:"+resStr);
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"查询终端SD目录失败","指令下发成功但是"+time+"S内未查到返回结果，result:"+resStr);
            }else{
                JSONObject res = JSON.parseObject(resStr);
                String dir =   res.getJSONArray("paramItems").getJSONObject(0).getString("paramVal");
                logger.info("E101终端返回结果:"+resStr);
                JSONObject dirJson = DirectionUtils.getJsonFromDirection(dir);
                logger.info("目录JSON结构:"+dirJson);
                responseObj.setData(dirJson);
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, "查询终端SD目录成功", "");
                redisService.removeStringValue(redisE101key);
            }


        }catch (Exception e){
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "向master发送请求失败", e.getMessage());
        }

        return responseObj;
    }


    /**
     * sd文件查询
     * @param fileparam
     * @return
     */
    @PostMapping("/fileUp/doUp")
    public ResponseObj readFile(@RequestBody SDFileEntity fileparam){
        logger.info("sd目录查询："+fileparam.toString());
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        if(StringUtils.isBlank(fileparam.getVid())||StringUtils.isBlank(fileparam.getSim())||StringUtils.isBlank(fileparam.getFilePath())){
            logger.error("参数校验错误 sim vid filePath不能为空");
            HashMap data = new HashMap();
            data.put("result","-3");
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "参数校验错误", "sim vid filePath不能为空");
            return responseObj;
        }
        //完全上传完成后删除key
        String redisE102key ="E102_"+fileparam.getSim();
        String field = fileparam.getFilePath();
        //String resStr = redisService.hgetValue(redisE102key,field);

        String resStr= redisService.getRedisValue(redisE102key,field,1);

        if("notLogin".equals(resStr)){
            //下发成功但是超时时间内未查到结果
            logger.info("查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
            HashMap data = new HashMap();
            data.put("result",resStr);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
        }
        else if("-1".equals(resStr)||"-2".equals(resStr)){
            HashMap data = new HashMap();
            data.put("result",resStr);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件到ftp失败","client下发查询终端指令失败，result:"+resStr);
        }
        else if("1".equals(resStr)){
            HashMap data = new HashMap();
            data.put("result",resStr);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端文件还在上传中","请稍后查询，result:"+resStr);
        }
        else if("5".equals(resStr)){
            HashMap data = new HashMap();
            data.put("result",resStr);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端ftp文件上传失败","终端上传失败，请重新下发指令，result:"+resStr);
        }
        else if("nothing".equals(resStr)){
            logger.info("指令下发成功但是超时时间内未查到返回结果，result:"+resStr);
            HashMap data = new HashMap();
            data.put("result",resStr);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件到ftp失败","指令下发成功但是超时时间内未查到返回结果，result:"+resStr);
        }else if("4".equals(resStr)){
            String redisE002key = "E002_"+fileparam.getSim();
            String res = redisService.hgetValue(redisE002key,fileparam.getFilePath());

            logger.info("终端上传的文件ftp路径，result:"+res);

            HashMap data = new HashMap();
            data.put("result",resStr);
            if(StringUtils.isNotBlank(res)){
                data.put("filePath",res);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_SUCCESS,"抓取文件成功","");
            }else{
                data.put("filePath",res);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件到ftp失败","redis结果不一致");
            }
        }

        return responseObj;
    }


    /**
     * sd文件上传到ftp服务器
     * @param fileparam
     * @return
     */
    @PostMapping("/fileUp/doUp/command")
    public ResponseObj readFileCommand(@RequestBody SDFileEntity fileparam){

        logger.info("sd目录查询指令下发："+fileparam.toString());
        ResponseObj responseObj=  ResponseObj.createResponseObj();
        if(StringUtils.isBlank(fileparam.getVid())||StringUtils.isBlank(fileparam.getSim())||StringUtils.isBlank(fileparam.getFilePath())){
            logger.error("参数校验错误 sim vid filePath不能为空");
            HashMap data = new HashMap();
            data.put("result","-3");
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "参数校验错误", "sim vid  filePath 不能为空");
            return responseObj;

        }
        String redisE102key ="E102_"+fileparam.getSim();
        String field = fileparam.getFilePath();
        String  resStr= redisService.hgetValue(redisE102key,field);
        //文件正在上传时，短时间没返回，强制再次下发
        //设置等待的文件，每次重新下发
        if(0==fileparam.getIsForce()&&"1".equals(resStr)&&0==fileparam.getIsWaiting()){

            HashMap data = new HashMap();
            data.put("result",resStr);
            responseObj.setData(data);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "该文件正在上传文件", "正在上传的,不需要重复发送命令!");
            return responseObj;
        }

        try {
            String endPoint = accessServiceUrl +  "/service/command";

            SdFileQuery reqE002 = new SdFileQuery();

            reqE002.setParamValue(fileparam.getFilePath());
            if(StringUtils.isNotBlank(fileparam.getParamId())){
                String paramIdstr = fileparam.getParamId();
                if(fileparam.getParamId().startsWith("0x")||fileparam.getParamId().startsWith("0X")){
                    paramIdstr=fileparam.getParamId().substring(2,fileparam.getParamId().length());
                }
                int paramId = Integer.parseInt(paramIdstr,16);
                reqE002.setParamId(paramId);
            }
            else {
                if (fileparam.getFilePath() != null && fileparam.getFilePath().toUpperCase().endsWith("DBC")) {
                    reqE002.setParamId(0xF00D);
                } else if (fileparam.getFilePath().toUpperCase().endsWith("MDF") || fileparam.getFilePath().toUpperCase().endsWith("MD4")) {
                    reqE002.setParamId(0xF00E);
                }
            }

            reqE002.setVid(fileparam.getVid());
            reqE002.setSim(fileparam.getSim());


            reqE002.setParamLength(fileparam.getFilePath().getBytes("GBK").length);
            logger.info("开始向master发送查询终端参数命令:"+reqE002.toString());
            //下发前,清除状态值
            redisService.removeStringValue(redisE102key,field);

            JSONObject json = restTemplate.postForObject(endPoint,reqE002,JSONObject.class);

            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                HashMap data = new HashMap();
                data.put("result","-4");
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
            //等待返回

            if(fileparam.getIsWaiting()==0) {

                resStr = redisService.getRedisValue(redisE102key,field,retryTimes);
            }else {
                //不是4,5的时候一直去取值
                resStr = redisService.getRedisValue(redisE102key, field, 60,"4","5","-1","-2","notLogin");
            }

            if("notLogin".equals(resStr)){
                //下发成功但是5S内未查到结果
                logger.info("查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"vid和sim代表的终端未登录","查询指令下发失败,请检查vid和sim是否正确，result:"+resStr);
                redisService.removeStringValue(redisE102key,field);
            }
            else if("-1".equals(resStr)||"-2".equals(resStr)){
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件失败","client下发查询终端指令失败，result:"+resStr);
                redisService.removeStringValue(redisE102key,field);
            }
            else if("1".equals(resStr)){
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                if(fileparam.getIsWaiting()==0){
                    responseObj.setStatus(ResponseObj.CODE_SUCCESS,"下发指令成功,终端上传文件中","终端还在上传文件，result:"+resStr);
                }else{
                    responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件到ftp失败","终端还在上传文件，result:"+resStr);
                }
            }
            else if("5".equals(resStr)){
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端ftp文件上传失败","终端上传失败result，result:"+resStr);
            }
            else if("nothing".equals(resStr)){
                logger.info("指令下发成功但是超时时间内未查到返回结果，result:"+resStr);
                HashMap data = new HashMap();
                data.put("result",resStr);
                responseObj.setData(data);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件到ftp失败","指令下发成功但是超时时间内未查到返回结果，result:"+resStr);
             }else if("4".equals(resStr)){

                String redisE002key = "E002_"+fileparam.getSim();
                String res = redisService.hgetValue(redisE002key,fileparam.getFilePath());
                logger.info("终端上传的文件ftp路径，result:"+res);

                HashMap data = new HashMap();
                data.put("result",resStr);
                if(StringUtils.isNotBlank(res)){

                    data.put("filePath",res);
                    responseObj.setData(data);
                    responseObj.setStatus(ResponseObj.CODE_SUCCESS,"抓取文件成功","");

                }else{
                    data.put("filePath",res);
                    responseObj.setData(data);
                    responseObj.setStatus(ResponseObj.CODE_FAIL_B,"终端上传文件到ftp失败","redis结果不一致");
                }

                //redisService.removeStringValue(redisE102key,fileparam.getFilePath());

            }

        }catch (Exception e){
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "指令下发失败", e.getMessage());
        }
        return responseObj;
        }



}
