/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.dfssi.dataplatform.datasync.service.rpc;

import com.dfssi.dataplatform.datasync.common.platform.entity.TaskDataCleanTransform;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskDataDestination;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskDataSource;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskEntity;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.service.client.RpcClient;
import com.dfssi.dataplatform.datasync.service.master.RpcCommand;
import com.dfssi.dataplatform.datasync.service.master.RpcServer;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcRequest;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


public class TestRpcSend {

  private RpcClient client;
  private RpcServer server;

  @Before
  public void setUp() {

//    ServiceRegistry serviceRegistry = new ServiceRegistry("172.16.1.222:2181");
    client = new RpcClient(PropertiUtil.getStr("server_address"),PropertiUtil.getInt("serverport"));
//    ServiceDiscovery serviceDiscovery = new ServiceDiscovery("172.16.1.222:2181", Constant.ZK_CLIENT_REGISTRY_PATH);
//    server = new RpcServer("192.168.20.175:8081",serviceRegistry,serviceDiscovery);

    server =RpcServer.getRpcService();
      try {
          client.start();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

    @After
    public void testStop() throws Exception {
        server.stop();
        client.close();
    }


  @Test
  public void testSend(){


      RpcRequest rpcRequest = new RpcRequest();
      RpcResponse rpcResponse = new RpcResponse();
      {UUID responseId = UUID.randomUUID();
      rpcResponse.setResponseId(responseId.toString());

      RpcCommand command = new RpcCommand();
      Map map = new HashMap();
      rpcResponse.setMsg(command);
      command.setAction("start");


      map.put("source1","aSource");
      command.setParams(map);
          try {
              server.send(rpcResponse);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }

      {
      UUID responseId = UUID.randomUUID();
      rpcResponse.setResponseId(responseId.toString());
      TaskEntity task = new TaskEntity();
      task.setTaskId("6de0be88-9ead-4d56-82ff-73eac5a50427");
      task.setClientId("client_1");
      task.setChannelType("kafkaChannel");
          TaskDataSource datasource = new TaskDataSource();
          datasource.setSrcId("6");
          datasource.setType("com.dfssi.dataplatform.datasync.plugin.source.tcp.MultiportSyslogTCPSource");
          {
              List<String> columns = new ArrayList<>();
              columns.add("id");
              columns.add("name");
              columns.add("age");
              columns.add("addr");
              datasource.setColumns(columns);
              Map<String, String> config = new HashMap<>();
              config.put("ip","192.168.1.130");
              config.put("port","80");
              datasource.setConfig(config);
              task.setTaskDataSource(datasource);
          }


          List<TaskDataCleanTransform> dataCleanTransforms = new ArrayList<>();
          {
              TaskDataCleanTransform e = new TaskDataCleanTransform();
              e.setCleanTransId("12");
              e.setType("com.dfssi.dataplatform.datasync.plugin.inteceptor.808.Protocol808Inteceptor");
              List<String> columns = new ArrayList<>();
              columns.add("can_id");
              columns.add("car_name");
              columns.add("product_addr");
              columns.add("car_age");
              e.setColumns(columns);
              Map<String, String> config = new HashMap<>();
              config.put("seperator",",");
              e.setConfig(config);
              dataCleanTransforms.add(e);
          }
          task.setTaskDataCleanTransforms(dataCleanTransforms);

          List<TaskDataDestination> dataDestinations = new ArrayList<>();
          {
              TaskDataDestination e = new TaskDataDestination();
              e.setDestId("73");
              e.setType("com.dfssi.dataplatform.datasync.plugin.sink.hdfs.SSIHDFSEventSink");
              List<String> columns = new ArrayList<>();
              columns.add("id");
              columns.add("name");
              columns.add("age");
              columns.add("addr");
              e.setColumns(columns);

              Map<String, String> config = new HashMap<>();
              config.put("hdfs.path","hdfs://172.16.1.210:8020/tmp/ssi_flume_t/");
              config.put("hdfs.filePrefix","flumeTest");
              config.put("hdfs.rollCount","10");
              config.put("hdfs.writeFormat","Text");
              config.put("hdfs.fileType","DataStream");
              e.setConfig(config);

              dataDestinations.add(e);
          }
          task.setTaskDataDestinations(dataDestinations);



      rpcResponse.setMsg(task);
          try {
              server.send(rpcResponse);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }

      try {
//          client.send(rpcRequest);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

}
