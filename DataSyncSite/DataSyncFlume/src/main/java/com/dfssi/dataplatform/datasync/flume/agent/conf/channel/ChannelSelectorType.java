/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dfssi.dataplatform.datasync.flume.agent.conf.channel;

/**
 * Enumeration of built in channel selector types available in the system.
 */
public enum ChannelSelectorType {

  /**
   * Place holder for custom channel selectors not part of this enumeration.
   */
  OTHER(null),

  /**
   * Replicating channel selector.
   */
  REPLICATING("ReplicatingChannelSelector"),

  /**
   * Multiplexing channel selector.
   */
  MULTIPLEXING("org.apache.flume.channel.MultiplexingChannelSelector");

  private final String channelSelectorClassName;

  private ChannelSelectorType(String channelSelectorClassName) {
    this.channelSelectorClassName = channelSelectorClassName;
  }

  public String getChannelSelectorClassName() {
    return channelSelectorClassName;
  }
}
