package com.dfssi.dataplatform.datasync.flume.agent.node;

import com.dfssi.dataplatform.datasync.flume.agent.Channel;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.SinkRunner;
import com.dfssi.dataplatform.datasync.flume.agent.SourceRunner;
import com.dfssi.dataplatform.datasync.flume.agent.instrumentation.MonitorService;
import com.dfssi.dataplatform.datasync.flume.agent.instrumentation.MonitoringType;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleAware;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleState;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleSupervisor;
import com.google.common.base.Throwables;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class Application {

  private static final Logger logger = LoggerFactory
      .getLogger(Application.class);

  public static final String CONF_MONITOR_CLASS = "flume.monitoring.type";
  public static final String CONF_MONITOR_PREFIX = "flume.monitoring.";

  private final List<LifecycleAware> components;
  private final LifecycleSupervisor supervisor;
  private MaterializedConfiguration materializedConfiguration;
  private MonitorService monitorServer;

  public Application() {
    this(new ArrayList<LifecycleAware>(0));
  }

  public Application(List<LifecycleAware> components) {
    this.components = components;
    supervisor = new LifecycleSupervisor();
  }

  public synchronized void start() {
/*    try {
      new HeartBeatsClient().connect(8080, "127.0.0.1");
    } catch (Exception e) {
      e.printStackTrace();
    }*/
    for (LifecycleAware component : components) {
      supervisor.supervise(component,
          new LifecycleSupervisor.SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
    }
  }

  @Subscribe
  public synchronized void handleConfigurationEvent(MaterializedConfiguration conf) {
    stopAllComponents();
    startAllComponents(conf);
  }

  public synchronized void stop() {
      //logger.info("模拟stopAllComponents，测试netty连接是否关闭,就是这个地方导致netty连接关闭");
      stopAllComponents();

      //logger.info("修改supervisor.stop()的内容，测试netty连接是否关闭");
      supervisor.stop();
      if (monitorServer != null) {
          monitorServer.stop();
      }

  }

  private void stopAllComponents() {
    if (this.materializedConfiguration != null) {
      logger.info("flume的materializedConfiguration文件不为空，开始停止flume任务！");
      logger.info("Shutting down configuration: {}", this.materializedConfiguration);
      for (Entry<String, SourceRunner> entry :
           this.materializedConfiguration.getSourceRunners().entrySet()) {
        try {
          logger.info("Stopping Source " + entry.getKey());
          supervisor.unsupervise(entry.getValue());
        } catch (Exception e) {
          logger.error("Error while stopping {}", entry.getValue(), e);
        }
      }

      //logger.info("注释掉getSinkRunners，测试netty是否断开连接--就是这个地方导致netty连接中断");
      for (Entry<String, SinkRunner> entry :
           this.materializedConfiguration.getSinkRunners().entrySet()) {
        try {
          logger.info("开始停止sink:"+entry.toString()+entry.getValue());
          supervisor.unsupervise(entry.getValue());
        } catch (Exception e) {
          logger.error("停止sink发生错误，Error while stopping {}", entry.getValue(), e);
        }
      }

      for (Entry<String, Channel> entry :
           this.materializedConfiguration.getChannels().entrySet()) {
        try {
          logger.info("Stopping Channel " + entry.getKey());
          supervisor.unsupervise(entry.getValue());
        } catch (Exception e) {
          logger.error("Error while stopping {}", entry.getValue(), e);
        }
      }
    }
    if (monitorServer != null) {
      monitorServer.stop();
    }
  }

  private void startAllComponents(MaterializedConfiguration materializedConfiguration) {
    logger.debug("Starting new configuration:{}", materializedConfiguration);
    logger.info("flume 任务开始startAllComponents 开启新配置:{}", materializedConfiguration);
    if (materializedConfiguration == null) {
      return;
    }

    this.materializedConfiguration = materializedConfiguration;

    for (Entry<String, Channel> entry :
        materializedConfiguration.getChannels().entrySet()) {
      try {
        logger.info("Starting Channel " + entry.getKey());
        supervisor.supervise(entry.getValue(),
            new LifecycleSupervisor.SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      } catch (Exception e) {
        logger.error("Error while starting {}", entry.getValue(), e);
      }
    }

    /*
     * Wait for all channels to start.
     */
    for (Channel ch : materializedConfiguration.getChannels().values()) {
      while (ch.getLifecycleState() != LifecycleState.START
          && !supervisor.isComponentInErrorState(ch)) {
        try {
          logger.info("Waiting for channel: " + ch.getName() +
              " to start. Sleeping for 500 ms");
          Thread.sleep(500);
        } catch (InterruptedException e) {
          logger.error("Interrupted while waiting for channel to start.", e);
          Throwables.propagate(e);
        }
      }
    }

    for (Entry<String, SinkRunner> entry : materializedConfiguration.getSinkRunners().entrySet()) {
      try {
        logger.info("Starting Sink " + entry.getKey());
        supervisor.supervise(entry.getValue(),
            new LifecycleSupervisor.SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      } catch (Exception e) {
        logger.error("Error while starting {}", entry.getValue(), e);
      }
    }

    for (Entry<String, SourceRunner> entry :
         materializedConfiguration.getSourceRunners().entrySet()) {
      try {
        logger.info("Starting Source " + entry.getKey());
        supervisor.supervise(entry.getValue(),
            new LifecycleSupervisor.SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      } catch (Exception e) {
        logger.error("Error while starting {}", entry.getValue(), e);
      }
    }

    this.loadMonitoring();
  }

  @SuppressWarnings("unchecked")
  private void loadMonitoring() {
    Properties systemProps = System.getProperties();
    Set<String> keys = systemProps.stringPropertyNames();
    try {
      if (keys.contains(CONF_MONITOR_CLASS)) {
        String monitorType = systemProps.getProperty(CONF_MONITOR_CLASS);
        Class<? extends MonitorService> klass;
        try {
          //Is it a known type?
          klass = MonitoringType.valueOf(
              monitorType.toUpperCase(Locale.ENGLISH)).getMonitorClass();
        } catch (Exception e) {
          //Not a known type, use FQCN
          klass = (Class<? extends MonitorService>) Class.forName(monitorType);
        }
        this.monitorServer = klass.newInstance();
        Context context = new Context();
        for (String key : keys) {
          if (key.startsWith(CONF_MONITOR_PREFIX)) {
            context.put(key.substring(CONF_MONITOR_PREFIX.length()),
                systemProps.getProperty(key));
          }
        }
        monitorServer.configure(context);
        monitorServer.start();
      }
    } catch (Exception e) {
      logger.warn("Error starting monitoring. "
          + "Monitoring might not be available.", e);
    }

  }
  
  public static void  startApp(){
    StaticZooKeeperConfigurationProvider zookeeperConfigurationProvider =
            new StaticZooKeeperConfigurationProvider(
                    "n1", "127.0.0.1:8080", "");
    Application application = new Application();
    application.handleConfigurationEvent(zookeeperConfigurationProvider.getConfiguration());

    application.start();

    final Application appReference = application;
    Runtime.getRuntime().addShutdownHook(new Thread("agent-shutdown-hook") {
      @Override
      public void run() {
        appReference.stop();
      }
    });
  }
/*
  public static void main(String[] args) {

    try {

      boolean isZkConfigured = false;

      Options options = new Options();

      Option option = new Option("n", "name", true, "the name of this agent");
      option.setRequired(true);
      options.addOption(option);

      option = new Option("f", "conf-file", true,
          "specify a config file (required if -z missing)");
      option.setRequired(false);
      options.addOption(option);

      option = new Option(null, "no-reload-conf", false,
          "do not reload config file if changed");
      options.addOption(option);

      // Options for Zookeeper
      option = new Option("z", "zkConnString", true,
          "specify the ZooKeeper connection to use (required if -f missing)");
      option.setRequired(false);
      options.addOption(option);

      option = new Option("p", "zkBasePath", true,
          "specify the base path in ZooKeeper for agent configs");
      option.setRequired(false);
      options.addOption(option);

      option = new Option("h", "help", false, "display help text");
      options.addOption(option);

      CommandLineParser parser = new GnuParser();
      CommandLine commandLine = parser.parse(options, args);

      if (commandLine.hasOption('h')) {
        new HelpFormatter().printHelp("flume-ng agent", options, true);
        return;
      }

      String agentName = commandLine.getOptionValue('n');
      boolean reload = !commandLine.hasOption("no-reload-conf");

      if (commandLine.hasOption('z') || commandLine.hasOption("zkConnString")) {
        isZkConfigured = true;
      }
      Application application = null;
      if (isZkConfigured) {
        // get options
        String zkConnectionStr = commandLine.getOptionValue('z');
        String baseZkPath = commandLine.getOptionValue('p');

        if (reload) {
          EventBus eventBus = new EventBus(agentName + "-event-bus");
          List<LifecycleAware> components = Lists.newArrayList();
          PollingZooKeeperConfigurationProvider zookeeperConfigurationProvider =
              new PollingZooKeeperConfigurationProvider(
                  agentName, zkConnectionStr, baseZkPath, eventBus);
          components.add(zookeeperConfigurationProvider);
          application = new Application(components);
          eventBus.register(application);
        } else {
          StaticZooKeeperConfigurationProvider zookeeperConfigurationProvider =
              new StaticZooKeeperConfigurationProvider(
                  agentName, zkConnectionStr, baseZkPath);
          application = new Application();
          application.handleConfigurationEvent(zookeeperConfigurationProvider.getConfiguration());
        }
      } else {
        File configurationFile = new File(commandLine.getOptionValue('f'));

        *//*
         * The following is to ensure that by default the agent will fail on
         * startup if the file does not exist.
         *//*
        if (!configurationFile.exists()) {
          // If command line invocation, then need to fail fast
          if (System.getProperty(Constants.SYSPROP_CALLED_FROM_SERVICE) ==
              null) {
            String path = configurationFile.getPath();
            try {
              path = configurationFile.getCanonicalPath();
            } catch (IOException ex) {
              logger.error("Failed to read canonical path for file: " + path,
                  ex);
            }
            throw new ParseException(
                "The specified configuration file does not exist: " + path);
          }
        }
        List<LifecycleAware> components = Lists.newArrayList();

        if (reload) {
          EventBus eventBus = new EventBus(agentName + "-event-bus");
          PollingPropertiesFileConfigurationProvider configurationProvider =
              new PollingPropertiesFileConfigurationProvider(
                  agentName, configurationFile, eventBus, 30);
          components.add(configurationProvider);
          application = new Application(components);
          eventBus.register(application);
        } else {
          PropertiesFileConfigurationProvider configurationProvider =
              new PropertiesFileConfigurationProvider(agentName, configurationFile);
          application = new Application();
          application.handleConfigurationEvent(configurationProvider.getConfiguration());
        }
      }
      application.start();

      final Application appReference = application;
      Runtime.getRuntime().addShutdownHook(new Thread("agent-shutdown-hook") {
        @Override
        public void run() {
          appReference.stop();
        }
      });

    } catch (Exception e) {
      logger.error("A fatal error occurred while running. Exception follows.", e);
    }
  }*/
}