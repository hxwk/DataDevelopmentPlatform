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

import com.dfssi.dataplatform.datasync.flume.agent.ChannelException;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.EventDrivenSource;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.instrumentation.SourceCounter;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleAware;
import com.dfssi.dataplatform.datasync.flume.agent.source.AbstractSource;
import com.dfssi.dataplatform.datasync.flume.agent.tools.FlumeBeanConfigurator;
import com.dfssi.dataplatform.datasync.plugin.http.constants.DataReceiveMode;
import com.dfssi.dataplatform.datasync.plugin.http.scheduler.HttpExecuteJob;
import com.dfssi.dataplatform.datasync.plugin.http.scheduler.QuartzManager;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * A source which accepts Flume Events by HTTP POST and GET. GET should be used
 * for experimentation only. HTTP requests are converted into flume events by a
 * pluggable "handler" which must implement the
 * {@linkplain HTTPSourceHandler} interface. This handler takes a
 * {@linkplain HttpServletRequest} and returns a list of flume events.
 *
 * The source accepts the following parameters: <p> <tt>port</tt>: port to which
 * the server should bind. Mandatory <p> <tt>handler</tt>: the class that
 * deserializes a HttpServletRequest into a list of flume events. This class
 * must implement HTTPSourceHandler. Default:
 * {@linkplain JSONHandler}. <p> <tt>handler.*</tt> Any configuration
 * to be passed to the handler. <p>
 *
 * All events deserialized from one Http request are committed to the channel in
 * one transaction, thus allowing for increased efficiency on channels like the
 * file channel. If the handler throws an exception this source will return
 * a HTTP status of 400. If the channel is full, or the source is unable to
 * append events to the channel, the source will return a HTTP 503 - Temporarily
 * unavailable status.
 *
 * A JSON handler which converts JSON objects to Flume events is provided.
 *
 */
public class HTTPSource extends AbstractSource implements
        EventDrivenSource, Configurable, LifecycleAware {
  /*
   * There are 2 ways of doing this:
   * a. Have a static server instance and use connectors in each source
   *    which binds to the port defined for that source.
   * b. Each source starts its own server instance, which binds to the source's
   *    port.
   *
   * b is more efficient than a because Jetty does not allow binding a
   * servlet to a connector. So each request will need to go through each
   * each of the handlers/servlet till the correct one is found.
   *
   */

  private static final Logger LOG = LoggerFactory.getLogger(HTTPSource.class);
  private volatile Integer port;
  private volatile Server srv;
  private volatile String host;
  private HTTPSourceHandler handler;
  private SourceCounter sourceCounter;

  // SSL configuration variable
  private volatile String keyStorePath;
  private volatile String keyStorePassword;
  private volatile Boolean sslEnabled;
  private final List<String> excludedProtocols = new LinkedList<String>();

  private Context sourceContext;
  private volatile String contextPath;  //请求路径
  private volatile String contextParams; //请求参数
  private volatile int  dataReceiveMode; //数据接收模式，主动 or 被动
  private volatile String cronTime; //定时任务时间表达式
  private String taskId;


  @Override
  public void configure(Context context) {
    sourceContext = context;
    try {
      // SSL related config
      sslEnabled = context.getBoolean(HTTPSourceConfigurationConstants.SSL_ENABLED, false);

      port = context.getInteger(HTTPSourceConfigurationConstants.CONFIG_PORT);
      host = context.getString(HTTPSourceConfigurationConstants.CONFIG_HOST,
          HTTPSourceConfigurationConstants.DEFAULT_BIND);

      contextPath = context.getString(HTTPSourceConfigurationConstants.CONTEXT_PATH); //获取请求路径
      dataReceiveMode = context.getInteger(HTTPSourceConfigurationConstants.RECEIVE_DATA_MODE); //数据接收模式
      contextParams = context.getString(HTTPSourceConfigurationConstants.CONTEXT_PARAMS); //请求参数，json格式
      cronTime = context.getString(HTTPSourceConfigurationConstants.SCHEDULER_CRON_TIME); //crontab表达式
      taskId = context.getString(HTTPSourceConfigurationConstants.TASK_ID);
      Preconditions.checkState(host != null && !host.isEmpty(),
                "HTTPSource hostname specified is empty");
      Preconditions.checkNotNull(port, "HTTPSource requires a port number to be"
          + " specified");

      String handlerClassName = context.getString(
              HTTPSourceConfigurationConstants.CONFIG_HANDLER,
              HTTPSourceConfigurationConstants.DEFAULT_HANDLER).trim();

      if (sslEnabled) {
        LOG.debug("SSL configuration enabled");
        keyStorePath = context.getString(HTTPSourceConfigurationConstants.SSL_KEYSTORE);
        Preconditions.checkArgument(keyStorePath != null && !keyStorePath.isEmpty(),
                                    "Keystore is required for SSL Conifguration" );
        keyStorePassword =
            context.getString(HTTPSourceConfigurationConstants.SSL_KEYSTORE_PASSWORD);
        Preconditions.checkArgument(keyStorePassword != null,
            "Keystore password is required for SSL Configuration");
        String excludeProtocolsStr =
            context.getString(HTTPSourceConfigurationConstants.EXCLUDE_PROTOCOLS);
        if (excludeProtocolsStr == null) {
          excludedProtocols.add("SSLv3");
        } else {
          excludedProtocols.addAll(Arrays.asList(excludeProtocolsStr.split(" ")));
          if (!excludedProtocols.contains("SSLv3")) {
            excludedProtocols.add("SSLv3");
          }
        }
      }

      @SuppressWarnings("unchecked")
      Class<? extends HTTPSourceHandler> clazz =
              (Class<? extends HTTPSourceHandler>)
              Class.forName(handlerClassName);
      handler = clazz.getDeclaredConstructor().newInstance();

      Map<String, String> subProps =
              context.getSubProperties(
              HTTPSourceConfigurationConstants.CONFIG_HANDLER_PREFIX);
//      handler.configure(new Context(subProps));
      handler.configure(sourceContext);
    } catch (ClassNotFoundException ex) {
      LOG.error("Error while configuring HTTPSource. Exception follows.", ex);
      Throwables.propagate(ex);
    } catch (ClassCastException ex) {
      LOG.error("Deserializer is not an instance of HTTPSourceHandler."
              + "Deserializer must implement HTTPSourceHandler.");
      Throwables.propagate(ex);
    } catch (Exception ex) {
      LOG.error("Error configuring HTTPSource!", ex);
      Throwables.propagate(ex);
    }
    if (sourceCounter == null) {
      sourceCounter = new SourceCounter(getName());
    }
  }

  @Override
  public void start() {
    //主动获取数据
    if (DataReceiveMode.INITIATIVA_RECEIVE.ordinal() == dataReceiveMode){
      try {
        QuartzManager.addJob(this.getClass().getName(),HTTPSourceConfigurationConstants.SCHEDULER_JOB_GROUP_NAME,
                this.getClass().getName(),HTTPSourceConfigurationConstants.SCHEDULER_TRIGGER_GROUP_NAME,
                HttpExecuteJob.class,cronTime,sourceContext.getParameters());
        HttpSourceEventProcessor httpSourceEventProcessor = HttpSourceEventProcessor.getInstance();
        httpSourceEventProcessor.setChannelProcessor(getChannelProcessor());
        httpSourceEventProcessor.setTaskId(taskId);
      }catch (Exception e){
        //e.printStackTrace();
        LOG.error("HttpSource start error ,msg = {}",e);
      }
      //被动接收外部接口发送过来的数据
    }else if (DataReceiveMode.PASSIVE_MODE.ordinal() == dataReceiveMode){

    Preconditions.checkState(srv == null,
            "Running HTTP Server found in source: " + getName()
            + " before I started one."
            + "Will not attempt to start.");
    QueuedThreadPool threadPool = new QueuedThreadPool();
    if (sourceContext.getSubProperties("QueuedThreadPool.").size() > 0) {
      FlumeBeanConfigurator.setConfigurationFields(threadPool, sourceContext);
    }
    srv = new Server(threadPool);

    //Register with JMX for advanced monitoring
    MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
    srv.addEventListener(mbContainer);
    srv.addBean(mbContainer);

    HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.addCustomizer(new SecureRequestCustomizer());

    FlumeBeanConfigurator.setConfigurationFields(httpConfiguration, sourceContext);
    ServerConnector connector;

    if (sslEnabled) {
      SslContextFactory sslCtxFactory = new SslContextFactory();
      FlumeBeanConfigurator.setConfigurationFields(sslCtxFactory, sourceContext);
      sslCtxFactory.setExcludeProtocols(excludedProtocols.toArray(new String[0]));
      sslCtxFactory.setKeyStorePath(keyStorePath);
      sslCtxFactory.setKeyStorePassword(keyStorePassword);

      httpConfiguration.setSecurePort(port);
      httpConfiguration.setSecureScheme("https");

      connector = new ServerConnector(srv,
          new SslConnectionFactory(sslCtxFactory, HttpVersion.HTTP_1_1.asString()),
          new HttpConnectionFactory(httpConfiguration));
    } else {
      connector = new ServerConnector(srv, new HttpConnectionFactory(httpConfiguration));
    }

    connector.setPort(port);
    connector.setHost(host);
    connector.setReuseAddress(true);

    FlumeBeanConfigurator.setConfigurationFields(connector, sourceContext);

    srv.addConnector(connector);

    try {
      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/");
      srv.setHandler(context);
      if (!contextPath.startsWith("/")){
        context.addServlet(new ServletHolder(new FlumeHTTPServlet()),"/"+contextPath); //把访问路径加上
      }else {
        context.addServlet(new ServletHolder(new FlumeHTTPServlet()),contextPath); //把访问路径加上
      }

      context.setSecurityHandler(new ConstraintSecurityHandler());
      srv.start();
    } catch (Exception ex) {
      LOG.error("Error while starting HTTPSource. Exception follows.", ex);
      Throwables.propagate(ex);
    }
    Preconditions.checkArgument(srv.isRunning());
    }
    sourceCounter.start();
    super.start();
  }


  @Override
  public void stop() {
    if (DataReceiveMode.INITIATIVA_RECEIVE.ordinal() == dataReceiveMode){
      try {
        QuartzManager.removeJob(this.getClass().getName(),HTTPSourceConfigurationConstants.SCHEDULER_JOB_GROUP_NAME,
                this.getClass().getName(),HTTPSourceConfigurationConstants.SCHEDULER_TRIGGER_GROUP_NAME);
      }catch (Exception e){
        LOG.error("close quartzJob error,e = {}",e);
      }
    try {
      srv.stop();
      srv.join();
      srv = null;
    } catch (Exception ex) {
      LOG.error("Error while stopping HTTPSource. Exception follows.", ex);
    }
    }
    sourceCounter.stop();
    LOG.info("Http source {} stopped. Metrics: {}", getName(), sourceCounter);
  }





  private class FlumeHTTPServlet extends HttpServlet {

    private static final long serialVersionUID = 4891924863218790344L;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

      List<Event> events = Collections.emptyList(); //create empty list
      try {
        events = handler.getEvents(request);
      } catch (HTTPBadRequestException ex) {
        LOG.warn("Received bad request from client. ", ex);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Bad request from client. "
                + ex.getMessage());
        return;
      } catch (Exception ex) {
        LOG.warn("Deserializer threw unexpected exception. ", ex);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Deserializer threw unexpected exception. "
                + ex.getMessage());
        return;
      }
      sourceCounter.incrementAppendBatchReceivedCount();
      sourceCounter.addToEventReceivedCount(events.size());
      try {
        getChannelProcessor().processEventBatch(events);
      } catch (ChannelException ex) {
        LOG.warn("Error appending event to channel. "
                + "Channel might be full. Consider increasing the channel "
                + "capacity or make sure the sinks perform faster.", ex);
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                "Error appending event to channel. Channel might be full."
                + ex.getMessage());
        return;
      } catch (Exception ex) {
        LOG.warn("Unexpected error appending event to channel. ", ex);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Unexpected error while appending event to channel. "
                + ex.getMessage());
        return;
      }
      response.setCharacterEncoding(request.getCharacterEncoding());
      response.setStatus(HttpServletResponse.SC_OK);
      response.flushBuffer();
      sourceCounter.incrementAppendBatchAcceptedCount();
      sourceCounter.addToEventAcceptedCount(events.size());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      doPost(request, response);
    }
  }
}
