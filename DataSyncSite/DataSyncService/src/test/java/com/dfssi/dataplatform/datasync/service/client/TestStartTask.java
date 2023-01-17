package com.dfssi.dataplatform.datasync.service.client;

import com.dfssi.dataplatform.datasync.common.platform.entity.TaskEntity;
import com.dfssi.dataplatform.datasync.flume.agent.node.Application;
import com.dfssi.dataplatform.datasync.flume.agent.node.SimpleMaterializedConfiguration;
import com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment.PluginManager;
import com.dfssi.dataplatform.datasync.flume.agent.source.AbstractSource;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HSF on 2018/1/18.
 */
public class TestStartTask {
    public static final String  taskJson = "{\n" +
            "    \"taskId\": \"6de0haphg8-disapgahpghe-afopi-afhpgh-7ajdp5gagh5\",\n" +
            "    \"clientId\": \"client_1\",\n" +
            "    \"channelType\": \"com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel\",\n" +
            "    \"taskType\": \"FLUME\",\n" +
            "    \"taskAction\": \"START\",\n" +
            "    \"taskDataSource\": {\n" +
            "        \"srcId\": \"514372249507\",\n" +
            "        \"type\": \"com.dfssi.dataplatform.tbpsource.server.TBPSource\",\n" +
            "        \"config\": {\n" +
            "               \"taskID\": \"6de0haphg8-disapgahpghe-afopi-afhpgh-fasdf342\"\n" +
            "            }\n" +
            "    },\n" +
            "    \"taskDataDestinations\": [\n" +
            "        {\n" +
            "            \"destId\": \"74\",\n" +
            "            \"type\": \"com.dfssi.dataplatform.datasync.plugin.sink.kafka.KafkaSink\",\n" +
            "            \"columns\": [\n" +
            "                \"id\",\n" +
            "                \"name\",\n" +
            "                \"addr\",\n" +
            "                \"age\"\n" +
            "            ],\n" +
            "            \"mapping\": {\n" +
            "                \"0\": [\n" +
            "                    0\n" +
            "                ],\n" +
            "                \"1\": [\n" +
            "                    1\n" +
            "                ],\n" +
            "                \"2\": [\n" +
            "                    2\n" +
            "                ],\n" +
            "                \"3\": [\n" +
            "                    3\n" +
            "                ]\n" +
            "            },\n" +
            "            \"config\": {\n" +
            "                \"kafka.topic\": \"testDataOn230\",\n" +
            "                \"kafka.consumer.group.id\": \"10_2\",\n" +
            "                \"kafka.consumer.request.timeout.ms\": \"40000\",\n" +
            "                \"kafka.consumer.session.timeout.ms\": 30000,\n" +
            "                \"kafka.bootstrap.servers\": \"172.16.1.121:9092,172.16.1.122:9092,172.16.1.123:9092\",\n" +
            "                \"taskId\": \"6de0haphg8-disapgahpghe-afopi-afhpgh-7ajdp5gagh5\"\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    static ConcurrentHashMap<String,AbstractSource> sourceMap = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        TaskEntity taskEntity = new Gson().fromJson(taskJson, TaskEntity.class);
        String conf = "F:\\svn文件\\SSIDataPlatform\\05-Implement\\SourceCode\\trunk\\SSIDataPlatform\\DataDevelopmentPlatform\\DataSyncSite\\DataSyncService\\src\\main\\resources\\config\\client.properties";
        File configurationFile = new File(conf);
        //如果配置文件不存在,结束
        if(!configurationFile.exists()) return;

        try {
            System.setProperty("conf", configurationFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.start();
        Application application = new Application();
        try {
            SimpleMaterializedConfiguration simple = new SimpleMaterializedConfiguration(taskEntity, sourceMap);
            application.handleConfigurationEvent(simple);
            application.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
