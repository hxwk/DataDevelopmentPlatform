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
package com.dfssi.dataplatform.datasync.flume.agent;


import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleAware;
import com.dfssi.dataplatform.datasync.flume.agent.sink.SinkGroup;

import java.util.List;

/**
 * <p>
 * Interface for a device that allows abstraction of the behavior of multiple
 * sinks, always assigned to a SinkRunner
 * </p>
 * <p>
 * A sink processors {@link SinkProcessor#process()} method will only be
 * accessed by a single runner thread. However configuration methods
 * such as {@link Configurable#configure} may be concurrently accessed.
 *
 * @see Sink
 * @see SinkRunner
 * @see SinkGroup
 */
public interface SinkProcessor extends LifecycleAware, Configurable {
  /**
   * <p>Handle a request to poll the owned sinks.</p>
   *
   * <p>The processor is expected to call {@linkplain Sink#process()} on
   *  whatever sink(s) appropriate, handling failures as appropriate and
   *  throwing {@link EventDeliveryException} when there is a failure to
   *  deliver any events according to the delivery policy defined by the
   *  sink processor implementation. See specific implementations of this
   *  interface for delivery behavior and policies.</p>
   *
   * @return Returns {@code READY} if events were successfully consumed,
   * or {@code BACKOFF} if no events were available in the channel to consume.
   * @throws EventDeliveryException if the behavior guaranteed by the processor
   * couldn't be carried out.
   */
  Sink.Status process() throws EventDeliveryException;

  /**
   * <p>Set all sinks to work with.</p>
   *
   * <p>Sink specific parameters are passed to the processor via configure</p>
   *
   * @param sinks A non-null, non-empty list of sinks to be chosen from by the
   * processor
   */
  void setSinks(List<Sink> sinks);
}