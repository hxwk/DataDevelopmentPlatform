package com.dfssi.dataplatform.util;

import com.dfssi.dataplatform.client.NettyClient;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 自定义简单Kafka消费者， 使用高级API
 * Created by gerry on 12/21.
 */
public class KafkaConsumer implements Runnable {
    private static Logger logger = Logger.getLogger(KafkaConsumer.class);
    /**
     * 透传数据到目的平台类型  //消费kafka数据的过程中需要用到这个变量
     */
    public static int platformType;

    /**
     * Kafka数据消费对象
     */
    private ConsumerConnector consumer;

    /**
     * Kafka Topic名称
     */
    private String topic;

    /**
     * 线程数量，一般就是Topic的分区数量
     */
    private int numThreads;

    /**
     * 线程池
     */
    private ExecutorService executorPool;

    //kafka消费者里嵌入一个netty客户端
    private static NettyClient nettyClient ;





    /**
     * 构造函数
     *
     * @param topic      Kafka消息Topic主题
     * @param numThreads 处理数据的线程数/可以理解为Topic的分区数
     * @param zookeeper  Kafka的Zookeeper连接字符串
     * @param groupId    该消费者所属group ID的值
     */
    public KafkaConsumer(String topic, int numThreads, String zookeeper, String groupId) {
        // 1. 创建Kafka连接器
        this.consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
        // 2. 数据赋值
        this.topic = topic;
        this.numThreads = numThreads;
    }

    public int initAndRunNettyClient(String ip,int port) {
        nettyClient = new NettyClient(ip, port);
        int res = -1;
        try {
            res = nettyClient.start();
        } catch (Exception e) {
            logger.error("启动数据传输客户端异常",e);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void run() {
        logger.debug("开始指定kafka-topic");
        // 1. 指定Topic
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(this.topic, this.numThreads);

        // 2. 指定数据的解码器
        logger.debug("开始指定数据的解码器");
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        // 3. 获取连接数据的迭代器对象集合
        /**
         * Key: Topic主题
         * Value: 对应Topic的数据流读取器，大小是topicCountMap中指定的topic大小
         */
        logger.debug("开始获取连接数据的迭代器对象集合");
        Map<String, List<KafkaStream<String, String>>> consumerMap = this.consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);

        // 4. 从返回结果中获取对应topic的数据流处理器
        logger.debug("从返回结果中获取对应topic的数据流处理器");
        List<KafkaStream<String, String>> streams = consumerMap.get(this.topic);

        // 5. 创建线程池
        logger.debug("开始创建线程池");
        this.executorPool = Executors.newFixedThreadPool(this.numThreads);

        // 6. 构建数据输出对象
        logger.debug("开始构建数据输出对象");
        int threadNumber = 0;
        for (final KafkaStream<String, String> stream : streams) {
            Future future=this.executorPool.submit(new ConsumerKafkaStreamProcesser(stream, threadNumber));
            try {
                future.get();
            } catch (InterruptedException e) {
                logger.error("ConsumerKafkaStreamProcesser线程抛出异常，停止掉kafka消费者进程，请重启数据传输进程！",e);
                shutdown();
            } catch (ExecutionException e) {
                logger.error("ConsumerKafkaStreamProcesser线程抛出异常，停止掉kafka消费者进程，请重启数据传输进程！",e);
                shutdown();
            }
            threadNumber++;
        }

    }

    public void shutdown() {
        // 1. 关闭和Kafka的连接，这样会导致stream.hashNext返回false
        if (this.consumer != null) {
            this.consumer.shutdown();
        }

        // 2. 关闭线程池，会等待线程的执行完成
        if (this.executorPool != null) {
            // 2.1 关闭线程池
            this.executorPool.shutdown();

            // 2.2. 等待关闭完成, 等待五秒
            try {
                if (!this.executorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly!!");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted during shutdown, exiting uncleanly!!");
            }
        }

    }

    /**
     * 根据传入的zk的连接信息和groupID的值创建对应的ConsumerConfig对象
     *
     * @param zookeeper zk的连接信息，类似于：<br/>
     *                  hadoop-senior01.ibeifeng.com:2181,hadoop-senior02.ibeifeng.com:2181/kafka
     * @param groupId   该kafka consumer所属的group id的值， group id值一样的kafka consumer会进行负载均衡
     * @return Kafka连接信息
     */
    private ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
        // 1. 构建属性对象
        Properties prop = new Properties();
        // 2. 添加相关属性
        prop.put("group.id", groupId); // 指定分组id
        prop.put("zookeeper.connect", zookeeper); // 指定zk的连接url
        prop.put("zookeeper.session.timeout.ms", "400"); //
        prop.put("zookeeper.sync.time.ms", "200");
        prop.put("auto.commit.interval.ms", "1000");
        // 3. 构建ConsumerConfig对象
        return new ConsumerConfig(prop);
    }


    /**
     * Kafka消费者数据处理线程
     */
    public static class ConsumerKafkaStreamProcesser implements Runnable {
        // Kafka数据流
        private KafkaStream<String, String> stream;
        // 线程ID编号
        private int threadNumber;

        public ConsumerKafkaStreamProcesser(KafkaStream<String, String> stream, int  threadNumber) {
            this.stream = stream;
            this.threadNumber = threadNumber;
        }

        //因为消息已经从kafka取出，取出的数据必须备份，另外无法发送的数据或者发送失败的数据必须有重发机制 如果某一条数据发送失败(网络传输，或目的平台异常)那么必须停下消费kafka的进程保证数据的完整性
        //取出kafkaTopic数据后发送失败 首先重新启动netty客户端，重现尝试透传那一条失败的报文，重试3次，如果还是失败则保存下那条发送失败的报文，停掉kafka消费者 再停掉netty客户端
        @Override
        public void run() {
            logger.info("client端开始消费kafka里的数据,并通过netty向目的平台发送数据");
            // 1. 获取数据迭代器
            ConsumerIterator<String, String> iter = this.stream.iterator();
            // 2. 迭代输出数据
            while (iter.hasNext()) {
                // 2.1 获取数据值
                MessageAndMetadata value = iter.next();
                // 2.2 输出
                logger.info("线程号："+this.threadNumber + ",偏移量:" + value.offset() +",KafkaKey:"+ value.key() + ",KafkaMessage:" + value.message());
                String kafaMsg = value.message().toString();
                //在这个地方对报文数据进行排序，然后再透传数据到国家平台
                 String[] kafaMsgArray = value.message().toString().split("-");
                 String data = kafaMsgArray[4];
                 if(platformType == 1){
                     int res = nettyClient.sendMessageToServer(data);
                     if(res != 0){
                         logger.error("通过netty向目的平台发送数据异常！");
                         return ;
                     }
                 }else if(platformType == 2){
                     //在这个地方对报文数据进行解析，并和geode的库比对，进行筛选，符合向中寰系统发送要求的才发送
                     int messageType = 1;
                     String isFilterZhongHuan = "true";
                     if(1 == messageType){
                         if("true".equals(isFilterZhongHuan)){
                             logger.info("筛选出属于中寰系统车的报文！");
                             if(Zhonghuan.belongToZhonghuan(data)){
                                 int res = nettyClient.sendMessageToServer(data);
                                 if(res != 0){
                                     logger.error("通过netty向目的平台发送数据异常！");
                                     return;
                                 }
                             }else{
                                 logger.info("该报文不属于中寰系统故不传输该报文，data:"+data);
                             }
                         }
                     }else{
                         logger.error("透传数据至中寰业务系统的消息类型暂不支持，请检查配置文件！messageType："+messageType);
                     }
                 }else{
                     logger.error("透传数据至目的平台的类型暂不支持，请检查配置文件！platformType："+platformType);
                 }
            }
            // 3. 表示当前线程执行完成
            logger.info("线程执行完成");
        }
    }
}