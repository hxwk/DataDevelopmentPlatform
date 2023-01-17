package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.common.RedisService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SDFileEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SdDirQuery;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SdFileQuery;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.ISdFileService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName SdFileService
 * @Description SD目录查询，文件上传
 * @Author chenf
 * @Date 2018/9/27
 * @Versiion 1.0
 **/
@Service
public class SdFileService implements ISdFileService {

    private RestTemplate restTemplate =new RestTemplate();


    @Value("${access.service.url}")
    private String accessServiceUrl;

    @Autowired
    private RedisService redisService;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *查询SD卡目录列表
     * @param file
     * @return
     */
    @Override
    public Object fileQuery(SDFileEntity file) {

        try {

           String res = redisService.getStringValue(file.getVid()+"_SDDIR");
           JSONObject json = JSON.parseObject(res);
           if(json!=null){
               String dir =   json.getJSONArray("paramItems").getJSONObject(0).getString("paramVal");

               //JSONObject dir =   json.getJSONArray("paramItems").getJSONObject(0).getJSONObject("paramVal");

               return  dir;
           }
        }catch (Exception e){
            logger.error("查询SD卡目录列表失败",e.getMessage());
        }
        return null;
    }
    @Override
    public ResponseObj fileQueryCommand(SDFileEntity file) {
        ResponseObj responseObj=  ResponseObj.createResponseObj();

        try {
            String endPoint = accessServiceUrl +  "/service/command";
            SdDirQuery reqE001 = new SdDirQuery();
            //int[]  paramids = new int[file.getParamIds().length];
            int[] paramids ={0xF00C};
            for(int i = 0 ;i <file.getParamIds().length;i++){
                paramids[i] = Integer.valueOf(file.getParamIds()[i]);
            }

            reqE001.setVid(file.getVid());
            reqE001.setSim(file.getSim());
            reqE001.setParamIds(paramids);
            logger.info("开始向master发送查询终端参数命令:"+reqE001.toString());

            JSONObject json = restTemplate.postForObject(endPoint, reqE001, JSONObject.class);
            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
            responseObj.setData(json);
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");


        }catch (Exception e){
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "向master发送请求失败", "");
        }

        return responseObj;

    }

    /**
     *读取文件
     * @param file
     * @return
     */
    @Override
    public Object readFile(SDFileEntity file) {
        try {
            String redisE002key = "E002_"+file.getSim();
            String res = redisService.hgetValue(redisE002key,file.getFilePath());

            if(StringUtils.isNotBlank(res)) {
                return res;
            }

        }catch (Exception e){
            logger.error("SD文件读取失败",e.getMessage());
        }
        return null;
    }

    /**
     *读取文件 指令下发
     * @param file
     * @return
     */
    @Override
    public ResponseObj readFileCommand(SDFileEntity file) {
        ResponseObj responseObj=  ResponseObj.createResponseObj();

        try {
            String endPoint = accessServiceUrl +  "/service/command";

            SdFileQuery reqE002 = new SdFileQuery();

            reqE002.setParamValue(file.getFilePath());
            if(file.getFilePath()!=null&&file.getFilePath().toUpperCase().endsWith("DBC")) {
                reqE002.setParamId(0xF00D);
            }
            else if(file.getFilePath().toUpperCase().endsWith("MDF")||file.getFilePath().toUpperCase().endsWith("MD4")) {
                reqE002.setParamId(0xF00E);
            }
            reqE002.setVid(file.getVid());
            reqE002.setSim(file.getSim());


            reqE002.setParamLength(file.getFilePath().getBytes("GBK").length);
            logger.info("开始向master发送查询终端参数命令:"+reqE002.toString());

            JSONObject json = restTemplate.postForObject(endPoint,reqE002,JSONObject.class);

            if(!"0".equals(json.getString("rc"))){
                String msg = json.getString("errMsg");
                logger.error("master向client转发请求失败"+msg);
                responseObj.setStatus(ResponseObj.CODE_FAIL_B,msg,"");
                return responseObj;
            }
                responseObj.setData(json);
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, "向master下发成功", "");

        }catch (Exception e){
            logger.error("向master发送请求失败",e);
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "向master发送请求失败", "");

        }
        return responseObj;
    }

}
