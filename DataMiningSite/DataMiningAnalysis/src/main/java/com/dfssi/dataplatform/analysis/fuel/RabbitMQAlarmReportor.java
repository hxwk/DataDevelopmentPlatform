package com.dfssi.dataplatform.analysis.fuel;

import com.dfssi.common.json.Jsons;
import com.dfssi.resources.ConfigDetail;
import com.dfssi.resources.ConfigUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/17 13:12
 */
public class RabbitMQAlarmReportor implements Serializable {

    private String host;
    private String user;
    private String password;
    private Integer port;

    //创建连接工厂
    private transient ConnectionFactory factory;
    private transient Channel channel;
    private String queueName;

    public RabbitMQAlarmReportor(ConfigDetail configDetail){
        this.host = configDetail.getConfigItemValue("fuel.alarm.out.rabbitmq.host", null);
        Preconditions.checkNotNull(host, "fuel.alarm.out.rabbitmq.host 不能为空。");

        this.port = configDetail.getConfigItemInteger("fuel.alarm.out.rabbitmq.port");
        this.user = configDetail.getConfigItemValue("fuel.alarm.out.rabbitmq.user", null);
        this.password = configDetail.getConfigItemValue("fuel.alarm.out.rabbitmq.password", null);

        this.queueName = configDetail.getConfigItemValue("fuel.alarm.out.rabbitmq.queue", "vehicle_alarm");
    }

    private ConnectionFactory getConnectionFactory() {
        if(factory == null) {
            //创建连接工厂
            this.factory = new ConnectionFactory();
            //设置RabbitMQ相关信息
            factory.setHost(host);
            if(user != null)factory.setUsername(user);
            if(password != null)factory.setPassword(password);
            if(port != null)factory.setPort(port);

            ExecutorService service = Executors.newFixedThreadPool(6);
            factory.setSharedExecutor(service);
            // 设置自动恢复
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(5 * 1000L);// 设置 每5s ，重试一次
            factory.setTopologyRecoveryEnabled(false);// 设置不重新声明交换器，队列等信息
        }
        return factory;
    }

    private Channel getChannel() throws IOException, TimeoutException {
        if(channel == null){
            Connection connection = getConnectionFactory().newConnection();
            //创建一个通道
            channel = connection.createChannel();
        }
        return channel;
    }

    public void declareQueue(String ... queues) throws IOException, TimeoutException {
        Channel channel = getChannel();
        for(String queue : queues){
            channel.queueDeclare(queue, true, false, false, null);
        }
        close();
    }

    public void checkAlarmAndReport(String id, String sim, Map<String, Object> record) throws Exception{
        Object alarmObj = ConfigUtils.getAsWithKeys(record, "alarms", "ALARMS");
        if(alarmObj != null){
            List<String> alarms = (List<String>) alarmObj;
            if(alarms.size() > 0){
                reportAlarm(id, sim, alarms);
            }
        }
    }

    private void reportAlarm(String id, String sim, List<String> alarms) throws Exception{
        Map<String, Object> report;
        for(String alarm : alarms){
            report = Maps.newHashMap();
            report.put("id", id);
            report.put("sim", sim);
            report.put("alarm", alarm);
            sendMessage(queueName, Jsons.obj2JsonString(report));
        }
    }

    private void sendMessage(String queue, String message) throws IOException, TimeoutException {
        Channel channel = getChannel();
        channel.basicPublish("", queue, null, message.getBytes("UTF-8"));
    }

    private void sendMessages(String queue, String ... messages) throws IOException, TimeoutException {
        Channel channel = getChannel();
        for(String message : messages)
            channel.basicPublish("", queue, null, message.getBytes("UTF-8"));
    }

    public void close(){
        if(channel != null){
            try {
                channel.close();
                channel.getConnection().close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {}
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {

        String QUEUE_NAME="rabbitMQ.test";

        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ相关信息
        factory.setHost("172.16.1.201");
        factory.setUsername("app_user");
        factory.setPassword("112233");
        factory.setPort(5672);
        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();
        //  声明一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello RabbitMQ";
        //发送消息到队列中
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println("Producer Send +'" + message + "'");
        //关闭通道和连接
        channel.close();
        connection.close();
    }

}
