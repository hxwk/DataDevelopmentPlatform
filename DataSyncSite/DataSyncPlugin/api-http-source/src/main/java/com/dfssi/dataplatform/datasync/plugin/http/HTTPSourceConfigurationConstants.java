/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfssi.dataplatform.datasync.plugin.http;

/**
 *
 */
public class HTTPSourceConfigurationConstants {

  public static final String CONFIG_PORT = "port";
  public static final String CONFIG_HANDLER = "handler";
  public static final String CONFIG_HANDLER_PREFIX =
          CONFIG_HANDLER + ".";

  public static final String CONFIG_HOST = "host";

  public static final String DEFAULT_BIND = "0.0.0.0";

  public static final String DEFAULT_HANDLER =
          "com.dfssi.dataplatform.datasync.plugin.http.JSONHandler";

  public static final String SSL_KEYSTORE = "keystore";
  public static final String SSL_KEYSTORE_PASSWORD = "keystorePassword";
  public static final String SSL_ENABLED = "enableSSL";
  public static final String EXCLUDE_PROTOCOLS = "excludeProtocols";
  public static final String CONTEXT_PATH = "contextPath";   //请求路径
    public static final String CONTEXT_PARAMS = "contextParams"; //请求参数,json格式

  public static final String RECEIVE_DATA_MODE = "receiveMode";//数据接收模式，主动 or 被动
  public static final String HTTP_REQUEST_TYPE = "httpRequestType"; //http请求类型，get or post

  public static final String SCHEDULER_JOB_GROUP_NAME = "JOB_GROUP_NAME";
  public static final String SCHEDULER_TRIGGER_GROUP_NAME = "TRIGGER_GROUP_NAME";
  public static final String SCHEDULER_CRON_TIME = "cronTime";

  public static final String TASK_ID = "taskId";
  public static final String HTTP_CHANNEL_PROCESSOR = "channelProcessor";

}
