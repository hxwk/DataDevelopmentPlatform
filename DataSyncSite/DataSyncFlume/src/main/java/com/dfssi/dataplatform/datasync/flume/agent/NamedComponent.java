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
package com.dfssi.dataplatform.datasync.flume.agent;


import com.dfssi.dataplatform.datasync.flume.agent.annotations.InterfaceAudience;
import com.dfssi.dataplatform.datasync.flume.agent.annotations.InterfaceStability;

/**
 * Enables a component to be tagged with a name so that it can be referred
 * to uniquely within the configuration system.
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface NamedComponent {

  public void setName(String name);

  public String getName();

}
