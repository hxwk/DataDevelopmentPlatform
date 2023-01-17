package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;/*
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

public final class SyslogSourceConfigurationConstants {

  public static final String CONFIG_PORT = "port";

  public static final String CONFIG_RECEIVETIMEOUTMILLISPERPACK = "receiveTimeoutMillisPerPack";

  public static final String CONFIG_REQUESTTIMEOUTMILLIS = "requestTimeoutMillis";

  public static final String CONFIG_TERMINALMAXIDLETIMEMILLIS = "terminalMaxIdleTimeMillis";

  /**
   * List of ports to listen to.
   */
  public static final String CONFIG_PORTS = "ports";

  public static final String CONFIG_HOST = "host";

  public static final String CONFIG_FORMAT_PREFIX = "format.";

  public static final String CONFIG_REGEX = "regex";

  public static final String CONFIG_SEARCH = "search";

  public static final String CONFIG_REPLACE = "replace";

  public static final String CONFIG_DATEFORMAT = "dateFormat";

  public static final String CONFIG_TASKID = "taskId";

  public static final String CONFIG_DELIMITER = "delimiter";

  public static final String CONFIG_COLUMNS = "columns";

  public static final String DEFAULT_DELIMITER = "\\s";

  /**
   * Number of processors used to calculate number of threads to spawn.
   */
  public static final String CONFIG_NUMPROCESSORS = "numProcessors";

  /**
   * Maximum allowable size of events.
   */
  public static final String CONFIG_EVENTSIZE = "eventSize";

  public static final String CONFIG_BATCHSIZE = "batchSize";

  public static final String CONFIG_CHARSET = "charset.default";

  public static final String DEFAULT_CHARSET = "UTF-8";

  public static final String CONFIG_PORT_CHARSET_PREFIX = "charset.port.";

  public static final int DEFAULT_BATCHSIZE = 100;

  public static final String CONFIG_PORT_HEADER = "portHeader";

  public static final String DEFAULT_PORT_HEADER = "port";

  public static final String CONFIG_READBUF_SIZE = "readBufferBytes";
  public static final int DEFAULT_READBUF_SIZE = 1024;

  public static final String CONFIG_KEEP_FIELDS = "keepFields";
  public static final String DEFAULT_KEEP_FIELDS = "none";

  public static final String CONFIG_KEEP_FIELDS_PRIORITY = "priority";
  public static final String CONFIG_KEEP_FIELDS_VERSION = "version";
  public static final String CONFIG_KEEP_FIELDS_TIMESTAMP = "timestamp";
  public static final String CONFIG_KEEP_FIELDS_HOSTNAME = "hostname";
  public static final String CONFIG_CONNECT_PROTOCOL_TYPE= "connectProtocolType";

  private SyslogSourceConfigurationConstants() {
    // Disable explicit creation of objects.
  }

}
