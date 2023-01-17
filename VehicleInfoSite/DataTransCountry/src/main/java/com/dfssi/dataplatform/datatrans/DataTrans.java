package com.dfssi.dataplatform.datatrans;

import com.dfssi.dataplatform.constants.PropertiUtil;
import com.dfssi.dataplatform.util.KafkaCommon;
import com.dfssi.dataplatform.util.KafkaConsumer;
import org.apache.commons.cli.*;
//import org.apache.log4j.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DataTrans {
    private static Logger logger =  LoggerFactory.getLogger(KafkaCommon.class);

    public static void main(String[] args) throws Exception {
        try {
            Options options = new Options();
            Option option = new Option("f", "conf", true,"specify a config file (required if -z missing)");
            option.setRequired(true);
            options.addOption(option);
            CommandLineParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption('h')) {
                new HelpFormatter().printHelp("DataTrans", options, true);
                return;
            }
            String conf = commandLine.getOptionValue("conf");
            File configurationFile = new File(conf);
            System.setProperty("conf", configurationFile.getCanonicalPath());
        }  catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String property = System.getProperty("java.class.path");
        //logger.info("数据传输进程加载path = " + property);

        String zkAddress;
        try{
            zkAddress = PropertiUtil.getStr("zk_address");
        }catch(Exception e){
            logger.error("PropertiUtil由zk_address获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由zk_address获取配置文件中的值");
        }
        String topic;
        try{
            topic = PropertiUtil.getStr("kafkaTopic");
        }catch(Exception e){
            logger.error("PropertiUtil由kafkaTopic获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由kafkaTopic获取配置文件中的值");
        }
        String groupId;
        try{
            groupId = PropertiUtil.getStr("kafkaGroupId");
        }catch(Exception e){
            logger.error("PropertiUtil由kafkaGroupId获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由kafkaGroupId获取配置文件中的值");
        }
        int threads;
        try{
            threads = PropertiUtil.getInt("kafkaThreads");
        }catch(Exception e){
            logger.error("PropertiUtil由kafkaThreads获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由kafkaThreads获取配置文件中的值");
        }
        int platformType;
        try{
            platformType = PropertiUtil.getInt("platformType");
        }catch(Exception e){
            logger.error("PropertiUtil由platformType获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由platformType获取配置文件中的值");
        }

        String ip;
        try{
            ip = PropertiUtil.getStr("platformIP");
        }catch(Exception e){
            logger.error("PropertiUtil由platformIP获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由platformIP获取配置文件中的值");
        }

        int port;
        try{
            port = PropertiUtil.getInt("platformPort");
        }catch(Exception e){
            logger.error("PropertiUtil由platformPort获取配置文件中的值失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由platformPort获取配置文件中的值");
        }

        KafkaConsumer kafkaConsumer = null;
        KafkaConsumer.platformType = platformType;
        try{
            kafkaConsumer = new KafkaConsumer(topic, threads, zkAddress, groupId);
        }catch(Exception e) {
            logger.error("创建kafka消费者异常！",e);
        }
        logger.info("开始启动数据传输进程，向目的平台透传数据！");
        //先开启nettyClient端的线程，再开启kafka消费线程，最后消费线程往nettyClient端的线程丢数据，这样保证数据的完整性
        int res = kafkaConsumer.initAndRunNettyClient(ip, port);
        if (res == 0){
            logger.info("开启nettyClient端连接目的平台成功，现在开始消费kafka里的数据！");
            try{
                new Thread(kafkaConsumer).start();
            }catch(Exception e){
                logger.error("kafka客戶端发生异常:",e);
                kafkaConsumer.shutdown();
            }
            logger.info("数据传输进程启动成功,开始消费kafka里的数据并透传到目的平台！");
        }else{
            logger.error("nettyClient端连接服务端失败，请检查配置文件里的ip端口！");
        }
    }
}
