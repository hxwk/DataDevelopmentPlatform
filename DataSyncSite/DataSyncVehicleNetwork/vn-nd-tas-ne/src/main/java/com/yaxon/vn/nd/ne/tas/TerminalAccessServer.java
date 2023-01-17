/**
 * 版权所有：厦门雅迅网络股份有限公司
 * Copyright (C) 2012 Xiamen Yaxon Networks CO.,LTD.
 * All right reserved.
 *====================================================
 * 文件名称: Bootstrap
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2012-04-18			zhangwz(创建:创建文件)
 *====================================================
 */
package com.yaxon.vn.nd.ne.tas;

import com.yaxon.vn.nd.ne.tas.net.tcp.TcpChannel;
import com.yaxon.vn.nd.ne.tas.net.tcp.TcpChannelConfig;
import com.yaxon.vn.nd.ne.tas.net.tcp.TcpChannelFactory;
import com.yaxon.vn.nd.ne.tas.net.udp.UdpChannel;
import com.yaxon.vn.nd.ne.tas.net.udp.UdpChannelConfig;
import com.yaxon.vn.nd.ne.tas.net.udp.UdpChannelFactory;
import com.yaxon.vndp.common.util.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 终端接入服务器
 */
public class TerminalAccessServer extends Bootstrap {
    private static Logger logger = LoggerFactory.getLogger(TerminalAccessServer.class);

    public static final String APP_NAME = "app.name";
    public static final String SPRING_CONFIG = "spring.config";
    public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";

    private ApplicationContext appContext;
    private TcpChannel tcpChannel;
    private UdpChannel udpChannel;


    public static void main(String[] args) {
        try {
            TerminalAccessServer app = new TerminalAccessServer();
            app.run(args);
        } catch (Throwable e) {
            logger.error("", e);
            System.exit(3);
        }
    }

    @Override
    public String appName() {
        return System.getProperty(APP_NAME);
    }

    public void setup() throws Exception {
        String configPath = System.getProperty(SPRING_CONFIG);
        if (configPath == null || "".equals(configPath.trim())) {
            configPath = DEFAULT_SPRING_CONFIG;
        }
        try {
            appContext = new ClassPathXmlApplicationContext(configPath.split("[,\\s]+"));
            TcpChannelConfig tcpConfig = appContext.getBean(TcpChannelConfig.class);
            if (tcpConfig == null) {
                throw new Exception("获取 TcpChannelConfig 失败");
            }
            tcpChannel = TcpChannelFactory.createTcpChannel(appContext, tcpConfig);

            UdpChannelConfig udpConfig = appContext.getBean(UdpChannelConfig.class);
            if (udpConfig == null) {
                throw new Exception("获取 UdpChannelConfig 失败");
            }
            udpChannel = UdpChannelFactory.createUdpChannel(appContext, udpConfig);
        } catch (Exception e) {
            throw new Exception("程序初始化失败", e);
        }
    }

    public void start() throws Exception {
        try {
            tcpChannel.start();
            udpChannel.start();
        } catch (Exception e) {
            throw new Exception("程序启动失败", e);
        }
    }

    public void stop() throws Exception {
        try {
            if (tcpChannel != null) {
                tcpChannel.stop();
                tcpChannel = null;
            }
            if (udpChannel != null) {
                udpChannel.stop();
                udpChannel = null;
            }
        } catch (Throwable e) {
            throw new Exception("程序关闭失败", e);
        }
    }
}
