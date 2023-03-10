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

package com.dfssi.dataplatform.datasync.flume.agent.instrumentation;

/**
 * Enum for Monitoring types.
 */
public enum MonitoringType {
  OTHER(null)/*,
  GANGLIA(org.apache.flume.instrumentation.GangliaServer.class),
  HTTP(org.apache.flume.instrumentation.http.HTTPMetricsServer.class)*/;

  private Class<? extends MonitorService> monitoringClass;

  private MonitoringType(Class<? extends MonitorService> klass) {
    this.monitoringClass = klass;
  }

  public Class<? extends MonitorService> getMonitorClass() {
    return this.monitoringClass;
  }
}
