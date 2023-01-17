/**
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
package com.dfssi.dataplatform.datasync.flume.agent.sink;

import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.EventDeliveryException;
import com.dfssi.dataplatform.datasync.flume.agent.Sink;
import com.dfssi.dataplatform.datasync.flume.agent.SinkProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ComponentConfiguration;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ConfigurableComponent;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleState;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Default sink processor that only accepts a single sink, passing on process
 * results without any additional handling. Suitable for all sinks that aren't
 * assigned to a group.
 */
public class DefaultSinkProcessor implements SinkProcessor, ConfigurableComponent {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSinkProcessor.class);
  private Sink sink;
  private LifecycleState lifecycleState;

  @Override
  public void start() {
    Preconditions.checkNotNull(sink, "DefaultSinkProcessor sink not set");
    sink.start();
    lifecycleState = LifecycleState.START;
  }

  @Override
  public void stop() {
    Preconditions.checkNotNull(sink, "DefaultSinkProcessor sink not set");
    sink.stop();
    lifecycleState = LifecycleState.STOP;
  }

  @Override
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  @Override
  public void configure(Context context) {
  }

  @Override
  public Sink.Status process() throws EventDeliveryException {
      //LOGGER.info("DefaultSinkProcess的process()--sink："+sink.toString()+"sink.getClass："+sink.getClass());
      return sink.process();
  }

  @Override
  public void setSinks(List<Sink> sinks) {
    Preconditions.checkNotNull(sinks);
    Preconditions.checkArgument(sinks.size() == 1, "DefaultSinkPolicy can "
        + "only handle one sink, "
        + "try using a policy that supports multiple sinks");
    sink = sinks.get(0);
  }

  @Override
  public void configure(ComponentConfiguration conf) {

  }

}
