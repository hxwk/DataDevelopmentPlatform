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


import com.dfssi.dataplatform.datasync.flume.agent.FlumeException;

/**
 *
 * Exception thrown by an HTTP Handler if the request was not parsed correctly
 * into an event because the request was not in the expected format.
 *
 */
public class HTTPBadRequestException extends FlumeException {

  private static final long serialVersionUID = -3540764742069390951L;

  public HTTPBadRequestException(String msg) {
    super(msg);
  }

  public HTTPBadRequestException(String msg, Throwable th) {
    super(msg, th);
  }

  public HTTPBadRequestException(Throwable th) {
    super(th);
  }
}
