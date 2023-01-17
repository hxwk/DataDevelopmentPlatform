package com.dfssi.dataplatform.service;

import com.dfssi.dataplatform.utils.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/4/13 13:21
 */
@Service
public class ProcessService {
    @Autowired
    RestTemplate restTemplate;
    /**
     * @author bin.Y
     * Description:对前台接收的检测报文进行解析入库，并发送rest请求部署及启动任务
     * Date:  2018/4/17 15:47
     */
    public HashMap<String, String> processCheckInfo(String json) {
        String checkId = "";
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("code","1");
        resultMap.put("msg","success");
        resultMap.put("detail","");
        try {
            InputCheckInformation process = new InputCheckInformation();
            checkId = process.processData(json);
            if(checkId.equals("0")){
                resultMap.put("code","0");
                resultMap.put("msg","该车企正在检测中！");
                resultMap.put("detail","");
                return resultMap;
            }
        } catch (Exception e) {
            resultMap.put("code","0");
            resultMap.put("msg","检测信息解析入库失败！");
            resultMap.put("detail",e.toString());
            e.printStackTrace();
            return resultMap;
        }
        try{
            sendRestFull(checkId);
        }catch (Exception e){
            resultMap.put("code","0");
            resultMap.put("msg","任务调度失败！");
            resultMap.put("detail",e.toString());
            e.printStackTrace();
            return resultMap;
        }
        return resultMap;
    }

    /**
     * @author bin.Y
     * Description:发送restful请求
     * Date:  2018/4/17 15:47
     */
    public void sendRestFull(String checkId) {
        Properties properties = ConformanceCommon.loadProperties("rest.properties");
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return true;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);
        String request = "{\"id\":\"20180408104711000001\",\"name\":\"Conformance Check\",\"modelTypeId\":\"20171123152036000003\",\"description\":\"Platform Conformance Check\",\"steps\":[{\"id\":\"20180408104711000002\",\"name\":\"Platform Conformance Check\",\"stepTypeId\":\"20180408100900000001\",\"guiX\":10,\"guiY\":10,\"params\":[{\"conformance.check.id\":{\"valueStr\":\"" + checkId + "\"}}]}],\"links\":[]}\n";
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", UUID.randomUUID().toString());
        postParameters.add("request", request);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, headers);
//        Object o = restTemplate.postForObject(properties.getProperty("deployUrl"), requestEntity, Object.class);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Shell shell = new Shell(properties.getProperty("sparkTaskUrl"), Integer.parseInt(properties.getProperty("sparkTaskPort")),properties.getProperty("sparkTaskName"), properties.getProperty("sparkTaskPassword"));
        shell.executeCommands(new String[]{"spark-submit --class com.dfssi.dataplatform.analysis.preprocess.process.platformConformance.offline.ConformanceCheckSubmit --master yarn --driver-memory 512m --num-executors 2 --executor-memory 512m  --executor-cores 2 --total-executor-cores 4  /home/yubin/eason/DataMiningPreProcess-1.0-SNAPSHOT.jar \""+checkId+"\"> /home/yubin/eason/conformanceCheck.log 2>&1 \n"});
//        Object forObject = restTemplate.getForObject(properties.getProperty("startUpUrl") + "/20180408104711000001", Object.class);
    }

    /**
     * @author bin.Y
     * Description:
     * Date:  2018/5/31 13:26
     */
    public Properties loadProperties(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        InputStream stream = classloader.getResourceAsStream(path);
        try {
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
