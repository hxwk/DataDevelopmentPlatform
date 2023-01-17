package com.dfssi.dataplatform.datasync.plugin.http.scheduler;

import com.dfssi.dataplatform.datasync.common.utils.GsonUtil;
import com.dfssi.dataplatform.datasync.plugin.http.HTTPSource;
import com.dfssi.dataplatform.datasync.plugin.http.HTTPSourceConfigurationConstants;
import com.dfssi.dataplatform.datasync.plugin.http.HttpSourceEventProcessor;
import com.dfssi.dataplatform.datasync.plugin.http.constants.HttpRequestType;
import com.dfssi.dataplatform.datasync.plugin.http.util.HttpUtil;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.event.EventBuilder;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.google.common.base.Preconditions;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static javafx.scene.input.KeyCode.H;

/**
 * Created by HSF on 2018/6/1.
 */
public class HttpExecuteJob implements Job{
    private  Integer port;
    private  String host;
    private  String contextPath;  //请求路径
    private  String contextParams; //请求参数
    private  int httpRequestType;//http请求类型，get or post
    private String taskId;
    private static final Logger logger = LoggerFactory.getLogger(HttpExecuteJob.class);


    public  void execute(JobExecutionContext var1) throws JobExecutionException{
        JobDataMap paramsMap = var1.getJobDetail().getJobDataMap();
        taskId = paramsMap.getString(HTTPSourceConfigurationConstants.TASK_ID);
        port = paramsMap.getIntegerFromString(HTTPSourceConfigurationConstants.CONFIG_PORT);
        host = paramsMap.getString(HTTPSourceConfigurationConstants.CONFIG_HOST);
        contextPath = paramsMap.getString(HTTPSourceConfigurationConstants.CONTEXT_PATH); //获取请求路径
        contextParams = paramsMap.getString(HTTPSourceConfigurationConstants.CONTEXT_PARAMS); //请求参数，json格式
        httpRequestType = paramsMap.getIntegerFromString(HTTPSourceConfigurationConstants.HTTP_REQUEST_TYPE); //http请求类型

        String result = "";
        String url = host+":"+port+contextPath;
        try{
            if(HttpRequestType.HTTP_REQUEST_GET.ordinal() == httpRequestType){  //doGet
                result = HttpUtil.doGet(url);
            }else if (HttpRequestType.HTTP_REQUEST_POST.ordinal() == httpRequestType){ //doPost
                result = HttpUtil.doPost(url,contextParams);
            }
            HttpJobResult httpJobResult = new HttpJobResult();
            httpJobResult.addListener( HttpSourceEventProcessor.getInstance());
            httpJobResult.setResult(result);
            logger.info("HttpSource->HttpExecuteJob  dorequest successfully ，taskId = {}" , taskId);
        }catch (Exception e){
            logger.error("HttpExecuteJob execute request error,e ={}",e);
        }
    }

    public static void main(String[] args) {
        SimpleEvent event = new SimpleEvent();
        Map<String, String> header = new HashMap<>();
        header.put(HTTPSourceConfigurationConstants.TASK_ID, "aaa");
        event.setHeaders(header);
        event.setBody(new String("TEST BODY").getBytes());

        String jsonStr = GsonUtil.toGson(event);
        System.out.println(jsonStr);
        SimpleEvent simpleEvent = GsonUtil.fromeGson(jsonStr,SimpleEvent.class);
    }
}
