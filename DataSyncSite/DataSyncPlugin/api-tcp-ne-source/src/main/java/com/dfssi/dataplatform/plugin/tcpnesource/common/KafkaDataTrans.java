package com.dfssi.dataplatform.plugin.tcpnesource.common;


import com.dfssi.dataplatform.plugin.tcpnesource.util.PropertiesFileUtil;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class KafkaDataTrans {
    private static final Logger logger = LoggerFactory.getLogger(KafkaDataTrans.class);

    private Producer producer;

    private String isTransNewEnergyData;//is_trans_new_energy_data

    private String newEnergyKafkaAddress;

    private String newEnergykafkaTopic;

    private String nowTimeHour;

    private String nowTimeHourTem; //存储上一次的时间 用于累加timekey
    private boolean hasNotInit = true;
    private long currentTimeStamp;
    private int timekey = 1;  //定义更新策略 一天刷新一次 还是一个月刷新一次  因为单位是1个小时 一个月也才24*30=720(最多)
    private double rowkey = 0;

    //初始化生产者
    public KafkaDataTrans() {
        try {
            PropertiesConfiguration propertiesConfiguration = PropertiesFileUtil.instanceConfig();
            isTransNewEnergyData = propertiesConfiguration.getString("is_trans_new_energy_data");

            if ("true".equals(isTransNewEnergyData)) {
                newEnergyKafkaAddress = propertiesConfiguration.getString("new_energy_kafka_address");
                newEnergykafkaTopic = propertiesConfiguration.getString("newEnergyDataTransTopic");
                Properties properties = new Properties();
                properties.put("serializer.class", StringEncoder.class.getName());
                properties.put("metadata.broker.list", newEnergyKafkaAddress);// 声明kafka broker
                producer = new Producer<Integer, String>(new ProducerConfig(properties));
                logger.debug("实例化kafka结束，isTransNewEnergyData:" + isTransNewEnergyData + ".newEnergyKafkaAddress：" + newEnergyKafkaAddress + ",newEnergykafkaTopic:" + newEnergykafkaTopic);
                //properties.put("zookeeper.connect", "172.16.1.210:2181");//声明zk
                //properties.put("metadata.broker.list", "172.16.1.121:9092");// 声明kafka broker
                //logger.info("实例化kafka结束，isTransNewEnergyData:"+isTransNewEnergyData+".newEnergyKafkaAddress："+newEnergyKafkaAddress+",zkAddress:"+zkAddress+",newEnergykafkaTopic:"+newEnergykafkaTopic);
            } else {
                logger.debug("配置文件isTransNewEnergyData属性为" + isTransNewEnergyData + ",不传输数据");
            }
        } catch (Exception e) {
            logger.error("请检查配置文件是否正确,异常信息:{}", e);
        }
    }


    //kafkaAddress支持用,分隔的ip+端口地址   其中只要有一个可以连接上就不报错  如果全部都无法连接就报错
    //向kafka发送的数据需定义规范，以保证报文的顺序
    //格式：2018083011-timestamp-timekey-rowkey-msg  将报文数据按分钟分区
    public void sendMessageToKafka(String msg) {
        if ("true".equals(isTransNewEnergyData)) {
            logger.debug("向kafkaToic:" + newEnergykafkaTopic + "发送数据：" + msg);
            nowTimeHour = getNowTime("yyyyMMddHH");//将数据按小时分片 一次只处理一个分片内的数据
            if (timekey == 1 && hasNotInit) {
                nowTimeHourTem = nowTimeHour;
                hasNotInit = false;
            }
            currentTimeStamp = System.currentTimeMillis();
            timekey = getTimekey(nowTimeHour);
            rowkey++;
            String msgkafka = nowTimeHour + "-" + currentTimeStamp + "-" + timekey + "-" + rowkey + "-" + msg;
            producer.send(new KeyedMessage<Integer, String>(newEnergykafkaTopic, msgkafka));
        } else {
            logger.debug("配置文件isTransData属性为"+ isTransNewEnergyData +",不传输数据");
        }
    }

    public String getNowTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    public int getTimekey(String nowTimeHour) {
        if (nowTimeHourTem.equals(nowTimeHour)) {
            return timekey;
        } else {
            //每天都会重置rowkey  防止报文数据量过大溢出报错
            if (!nowTimeHourTem.substring(0, 8).equals(nowTimeHour.substring(0, 8))) {
                nowTimeHourTem = nowTimeHour;
                rowkey = 0;
                return 1;
            }
            nowTimeHourTem = nowTimeHour;
            timekey++;
            return timekey;
        }
    }
}
