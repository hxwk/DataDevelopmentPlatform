package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util;


import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.constants.PropertiUtil;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class KafkaDataTrans {
    private static final Logger logger = LoggerFactory.getLogger(KafkaDataTrans.class);

    Producer producer;

    String isTransCommercialVehicleData;

    //String zkAddress;

    String commercialVehicleKafkaAddress;

    String commercialVehicleDataTransTopic;

    String nowTimeHour;
    String nowTimeHourTem; //存储上一次的时间 用于累加timekey
    boolean hasNotInit = true;
    long  currentTimeStamp;
    int timekey = 1;  //定义更新策略 一天刷新一次 还是一个月刷新一次  因为单位是1个小时 一个月也才24*30=720(最多)
    double rowkey=0;

    //初始化生产者
    public KafkaDataTrans(){
        try {
            //String conf = commandLine.getOptionValue("conf");
            //String conf = "DataSyncSite/DataSyncPlugin/api-tcp-ne-source/src/main/resources/com.dfssi.dataplatform.plugin.tcpnesource/dataTrans.properties";
            String conf = "../../config/dataTrans.properties";
            File configurationFile = new File(conf);
            logger.info("获得配置文件路径："+configurationFile.getCanonicalPath());
            System.setProperty("conf", configurationFile.getCanonicalPath());
        }  catch (Exception e) {
            e.printStackTrace();
        }
        try{
            isTransCommercialVehicleData = PropertiUtil.getStr("is_trans_commercial_vehicle_data");
        }catch(Exception e){
            logger.error("PropertiUtil由is_trans_new_energy_data获取配置文件中的值失败，请检查配置文件是否存在,异常信息:{}",e);
        }
        if("true".equals(isTransCommercialVehicleData)){
            /*try{
                zkAddress = PropertiUtil.getStr("new_energy_zk_address");
            }catch(Exception e){
                logger.error("PropertiUtil由zk_address获取配置文件中的值失败,异常信息:{}",e);
            }*/
            try{
                commercialVehicleKafkaAddress = PropertiUtil.getStr("commercial_vehicle_kafka_address");
            }catch(Exception e){
                logger.error("PropertiUtil由commercial_vehicle_kafka_address获取配置文件中的值失败,异常信息:{}",e);
            }
            try{
                commercialVehicleDataTransTopic = PropertiUtil.getStr("commercialVehicleDataTransTopic");
            }catch(Exception e){
                logger.error("PropertiUtil由commercialVehicleDataTransTopic获取配置文件中的值失败,异常信息:{}",e);
            }
            Properties properties = new Properties();
            //properties.put("zookeeper.connect", "172.16.1.210:2181");//声明zk
            properties.put("serializer.class", StringEncoder.class.getName());
            //properties.put("metadata.broker.list", "172.16.1.121:9092");// 声明kafka broker
            properties.put("metadata.broker.list", commercialVehicleKafkaAddress);// 声明kafka broker
            producer = new Producer<Integer, String>(new ProducerConfig(properties));
            //logger.info("实例化kafka结束，isTransCommercialVehicleData:"+isTransCommercialVehicleData+".commercialVehicleKafkaAddress："+commercialVehicleKafkaAddress+",zkAddress:"+zkAddress+",commercialVehicleDataTransTopic:"+commercialVehicleDataTransTopic);
            logger.info("实例化kafka结束，isTransCommercialVehicleData:"+ isTransCommercialVehicleData +".commercialVehicleKafkaAddress："+ commercialVehicleKafkaAddress +",commercialVehicleDataTransTopic:"+ commercialVehicleDataTransTopic);

        }else{
            logger.info("配置文件isTransData属性为"+ isTransCommercialVehicleData +",不传输数据");
        }
    }



    //kafkaAddress支持用,分隔的ip+端口地址   其中只要有一个可以连接上就不报错  如果全部都无法连接就报错
    //向kafka发送的数据需定义规范，以保证报文的顺序
    //格式：2018083011-timestamp-timekey-rowkey-msg  将报文数据按分钟分区
    public  void sendMessageToKafka(String msg){
        if("true".equals(isTransCommercialVehicleData)){
            logger.info("向kafkaToic:"+ commercialVehicleDataTransTopic +"发送数据："+msg);
            nowTimeHour = getNowTime("yyyyMMddHH");//将数据按小时分片 一次只处理一个分片内的数据
            if(timekey == 1 && hasNotInit){
                nowTimeHourTem = nowTimeHour;
                hasNotInit = false;
            }
            currentTimeStamp=System.currentTimeMillis();
            timekey = getTimekey(nowTimeHour);
            rowkey++;
            String msgkafka = nowTimeHour+"-"+currentTimeStamp+"-"+timekey+"-"+rowkey+"-"+msg;
            producer.send(new KeyedMessage<Integer, String>(commercialVehicleDataTransTopic, msgkafka));
        }else{
            logger.info("配置文件isTransCommercialVehicleData属性为"+ isTransCommercialVehicleData +",不传输数据");
        }
    }

    public String getNowTime(String format){
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    public  int getTimekey(String nowTimeHour){
        if (nowTimeHourTem.equals(nowTimeHour)){
            return timekey;
        }else{
            //每天都会重置rowkey  防止报文数据量过大溢出报错
            if(!nowTimeHourTem.substring(0,8).equals(nowTimeHour.substring(0,8))){
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
