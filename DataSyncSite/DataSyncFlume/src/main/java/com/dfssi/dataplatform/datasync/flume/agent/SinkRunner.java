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

import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleAware;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * A driver for {@linkplain Sink sinks} that polls them, attempting to
 * {@linkplain Sink#process() process} events if any are available in the
 * {@link Channel}.
 * </p>
 *
 * <p>
 * Note that, unlike {@linkplain Source sources}, all sinks are polled.
 * </p>
 *
 * @see Sink
 * @see SourceRunner
 */
public class SinkRunner implements LifecycleAware {

  private static final Logger logger = LoggerFactory
      .getLogger(SinkRunner.class);
  private static final long backoffSleepIncrement = 1000;
  private static final long maxBackoffSleep = 5000;

  private CounterGroup counterGroup;
  private PollingRunner runner;
  private Thread runnerThread;
  private LifecycleState lifecycleState;

  private SinkProcessor policy;

  public SinkRunner() {
    counterGroup = new CounterGroup();
    lifecycleState = LifecycleState.IDLE;
  }

  public SinkRunner(SinkProcessor policy) {
    this();
    setSink(policy);
  }

  public SinkProcessor getPolicy() {
    //logger.info("????????????SinkProcessor??????????????????????????????????????????");
    return policy;
  }

  public void setSink(SinkProcessor policy) {
    this.policy = policy;
  }

  @Override
  public void start() {
    SinkProcessor policy = getPolicy();

    policy.start();

    runner = new PollingRunner();

    runner.policy = policy;
    runner.counterGroup = counterGroup;
    runner.shouldStop = new AtomicBoolean();

    runnerThread = new Thread(runner);
    runnerThread.setName("SinkRunner-PollingRunner-" +
        policy.getClass().getSimpleName());
    runnerThread.start();
    lifecycleState = LifecycleState.START;
  }

  @Override
  public void stop() {
    if (runnerThread != null) {
      runner.shouldStop.set(true);
//      runnerThread.interrupt();
      while (runnerThread.isAlive()) {
        try {
          logger.info("Waiting for runner thread to exit");
          runnerThread.join();
        } catch (InterruptedException e) {
          logger.info("Interrupted while waiting for runner thread to exit. Exception follows.",
                       e);
        }
      }
    }
    getPolicy().stop();
    lifecycleState = LifecycleState.STOP;
  }

  @Override
  public String toString() {
    return "SinkRunner: { policy:" + getPolicy() + " counterGroup:"
        + counterGroup + " }";
  }

  @Override
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * {@link Runnable} that {@linkplain SinkProcessor#process() polls} a
   * {@link SinkProcessor} and manages event delivery notification,
   * {@link Sink.Status BACKOFF} delay handling, etc.
   */
  public static class PollingRunner implements Runnable {

    private SinkProcessor policy;
    private AtomicBoolean shouldStop;
    private CounterGroup counterGroup;

    //debug ????????????info
    @Override
    public void run() {
      logger.info("Polling sink runner starting");
      while (!shouldStop.get()) {//???false?????????????????????,????????????value=0
        try {
          if (policy.process().equals(Sink.Status.BACKOFF)) {
            counterGroup.incrementAndGet("runner.backoffs");
            long vvv = Math.min(
                    counterGroup.incrementAndGet("runner.backoffs.consecutive")
                            * backoffSleepIncrement, maxBackoffSleep);
            Thread.sleep(vvv);
          } else {
            counterGroup.set("runner.backoffs.consecutive", 0L);
          }
        } catch (InterruptedException e) {
          counterGroup.incrementAndGet("runner.interruptions");
        } catch (Exception e) {
          if (e instanceof EventDeliveryException) {
            counterGroup.incrementAndGet("runner.deliveryErrors");
          } else {
            counterGroup.incrementAndGet("runner.errors");
          }
          try {
            Thread.sleep(maxBackoffSleep);
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
          }
        }
      }
    }

  }
}
