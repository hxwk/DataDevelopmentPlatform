package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.server;

import com.dfssi.dataplatform.datasync.common.utils.UUIDUtil;
import com.dfssi.dataplatform.datasync.flume.agent.Channel;
import com.dfssi.dataplatform.datasync.flume.agent.ChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.EventDrivenSource;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ReplicatingChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurables;
import com.dfssi.dataplatform.datasync.flume.agent.instrumentation.SourceCounter;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleAware;
import com.dfssi.dataplatform.datasync.flume.agent.source.AbstractSource;
import com.dfssi.dataplatform.datasync.flume.agent.source.SyslogSourceConfigurationConstants;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanConstants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.HandlersManger;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.MessageHandlerReader;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.SyslogUtils;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.TaskInfo;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.handler.BaseProtoHandler;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.tcp.TcpChannel;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.tcp.TcpChannelConfig;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.tcp.TcpChannelFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

public class TCPSource extends AbstractSource implements
		EventDrivenSource, Configurable, LifecycleAware {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private volatile boolean isRunning = false;

	private static final int ST_STOPPED = 0;
	private static final int ST_STARTED = 1;

	private volatile int state = ST_STOPPED;

	private int port;
	private String host;
	private Integer numProcessors;
	private int maxEventSize;
	private int batchSize;
	private int readBufferSize;
	private String portHeader;
	private SourceCounter sourceCounter = null;
	private Charset defaultCharset;
	private Set<String> keepFields;
	private TcpChannelConfig tcpChannelConfig;

	private TcpChannel tcpChannel;


	/***********************与task相关——begin*******************/
	private String taskId;
	List<String> columns;

	/***********************与task相关——end**********************/

	public TCPSource() {
	}

	@VisibleForTesting
	public TCPSource(int port) {
		this();
		this.port = port;
	}

	@Override
	public void configure(Context context){

		tcpChannelConfig = new TcpChannelConfig();

		String portsStr = context.getString(
				SyslogSourceConfigurationConstants.CONFIG_PORTS);

		//Preconditions.checkNotNull(portsStr, "Must define config "
		//		+ "parameter for MultiportSyslogTCPSource: ports");

		if (StringUtils.isNotBlank(portsStr)) {
			port = Integer.parseInt(portsStr);
			tcpChannelConfig.setPort(port);
		}

		host = context.getString(SyslogSourceConfigurationConstants.CONFIG_HOST);
		logger.info("获取到flume任务的host："+host);
		if (StringUtils.isNotBlank(host)) {
			tcpChannelConfig.setHost(host);
		}

		String receivePack = context.getString(SyslogSourceConfigurationConstants.CONFIG_RECEIVETIMEOUTMILLISPERPACK);
		if (StringUtils.isNotBlank(receivePack)) {
			tcpChannelConfig.setReceiveTimeoutMillisPerPack(Integer.parseInt(receivePack));
		}

		String requestTime = context.getString(SyslogSourceConfigurationConstants.CONFIG_REQUESTTIMEOUTMILLIS);
		if (StringUtils.isNotBlank(requestTime)) {
			tcpChannelConfig.setRequestTimeoutMillis(Integer.parseInt(requestTime));
		}

		String terminalIdel = context.getString(SyslogSourceConfigurationConstants.CONFIG_TERMINALMAXIDLETIMEMILLIS);
		if (StringUtils.isNotBlank(terminalIdel)) {
			tcpChannelConfig.setTerminalMaxIdleTimeMillis(Integer.parseInt(terminalIdel));
		}

		taskId = context.getString(SyslogSourceConfigurationConstants.CONFIG_TASKID);
		logger.debug("taskId={}",taskId);

		tcpChannelConfig.checkConfig();

		numProcessors = context.getInteger(
				SyslogSourceConfigurationConstants.CONFIG_NUMPROCESSORS);

		maxEventSize = context.getInteger(
				SyslogSourceConfigurationConstants.CONFIG_EVENTSIZE,
				SyslogUtils.DEFAULT_SIZE);

		String defaultCharsetStr = context.getString(
				SyslogSourceConfigurationConstants.CONFIG_CHARSET,
				SyslogSourceConfigurationConstants.DEFAULT_CHARSET);

//		String columnsStr = context.getString(SyslogSourceConfigurationConstants.CONFIG_COLUMNS);

		try {
			defaultCharset = Charset.forName(defaultCharsetStr);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to parse charset "
					+ "string (" + defaultCharsetStr + ") from port configuration.", ex);
		}

		//初始化消息处理器
		logger.info("开始初始化消息处理器,需要打印出具体的消息处理器--todo");
		MessageHandlerReader.getInstance();

		batchSize = context.getInteger(
				SyslogSourceConfigurationConstants.CONFIG_BATCHSIZE,
				SyslogSourceConfigurationConstants.DEFAULT_BATCHSIZE);

		portHeader = context.getString(
				SyslogSourceConfigurationConstants.CONFIG_PORT_HEADER);

		readBufferSize = context.getInteger(
				SyslogSourceConfigurationConstants.CONFIG_READBUF_SIZE,
				SyslogSourceConfigurationConstants.DEFAULT_READBUF_SIZE);

		keepFields = SyslogUtils.chooseFieldsToKeep(
				context.getString(
						SyslogSourceConfigurationConstants.CONFIG_KEEP_FIELDS,
						SyslogSourceConfigurationConstants.DEFAULT_KEEP_FIELDS));

		if (sourceCounter == null) {
			sourceCounter = new SourceCounter(getName());
		}

	}

	private void bind() throws Exception {

		tcpChannelConfig.setTaskId(taskId);
		tcpChannelConfig.setChannelProcessor(getChannelProcessor());
 		logger.info("Tcp通道配置: {}", tcpChannelConfig + ", getChannelProcessor = " + getChannelProcessor());
		try {

			try {
				tcpChannel = TcpChannelFactory.createTcpChannel(tcpChannelConfig);
			} catch (Exception e) {
				throw new IllegalArgumentException("程序初始化失败", e);
			}

			tcpChannel.start();
		} catch (Exception e) {
			throw new Exception("程序启动失败", e);
		}
	}

	@Override
	public void start() {
		logger.info("flume任务开始Starting {}...", this);
		if (this.isRunning) {
			throw new IllegalStateException(this.getName() + " is already started .");
		}
		this.isRunning = true;

		TaskInfo taskInfo = TaskInfo.getInstance();
		taskInfo.setTaskId(taskId);
		taskInfo.setChannelProcessor(getChannelProcessor());

		//此处开始记录所有dbc文件版本
		CanConstants.dbcNameVersion = Sets.newTreeSet();
		CanConstants.fileDataVersion = Maps.newConcurrentMap();
		//启动获取车辆数据及更新车辆状态线程
//		ExecutorService exe = Executors.newFixedThreadPool(2);
//        VehicleCacheThread cache= new VehicleCacheThread();
//        VehicleStatusThread status= new VehicleStatusThread();
//		exe.submit(cache);
//		exe.submit(status);

        new Thread(() -> {
			try {
				this.bind();
			} catch (Exception e) {
				this.logger.info("TCP服务启动出错:{}", e.getMessage());
				e.printStackTrace();
			}
		}, this.getName()).start();

		sourceCounter.start();
		super.start();
		logger.info("{} started.", this);
	}

	@Override
	public void stop() {
		logger.info("TCPSource进行stop");
		try {
			if (tcpChannel != null) {
				tcpChannel.stop();
				tcpChannel = null;
			}

		} catch (Throwable e) {
			logger.error("程序关闭失败", e);
		}
	}

	@Override
	public VnndResMsg receive(Message req) {

		logger.info("TCPSource接收消息 req =" + req);

		BaseProtoHandler handler = HandlersManger.getDownHandlers(req.id());
		VnndResMsg jtsresmsg=null;
		if (handler == null) {
			logger.warn("TcpSource未找到消息处理器:msgId=" + req.id());
		} else {
			try {
				handler.doDnReq(req, taskId, getChannelProcessor());
			} catch (Exception e) {
				logger.error("消息处理器处理下行消息失败:msgId=" + req.id(),e);
			}
		}

		return null;//
	}



	@Override
	public String getName(){
		return new String("TCPSource");
	}

	public static void main(String[] args) {
		TCPSource server = new TCPSource();
		Context ctx = new Context();
		ctx.put(SyslogSourceConfigurationConstants.CONFIG_TASKID,UUIDUtil.getTimeBasedUuidByTrim());
		ctx.put("ports","10001");
		server.configure(ctx);
		MemoryChannel channel = new MemoryChannel();
		Configurables.configure(channel, new Context());
		List<Channel> channels = Lists.newArrayList();
		channels.add(channel);

		ChannelSelector rsc = new ReplicatingChannelSelector();
		rsc.setChannels(channels);

		server.setChannelProcessor(new ChannelProcessor(rsc));
		server.start();

		// Thread.sleep(3000);
		// server.stop();	
	}
}