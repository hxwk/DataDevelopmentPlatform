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

package com.dfssi.dataplatform.datasync.flume.agent.interceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Factory used to register instances of Interceptors & their builders,
 * as well as to instantiate the builders.
 */
public class InterceptorBuilderFactory {
  static final Logger logger = LoggerFactory.getLogger(InterceptorBuilderFactory.class);
  private static Class<? extends Interceptor.Builder> lookup(String name) {
    try {
      return InterceptorType.valueOf(name.toUpperCase(Locale.ENGLISH)).getBuilderClass();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Instantiate specified class, either alias or fully-qualified class name.
   */
  public static Interceptor.Builder newInstance(String name)
      throws ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    logger.info("name:{}",name);
    Class<?> clazz = Class.forName(name);
    return (Interceptor.Builder) clazz.newInstance();
  }
}
