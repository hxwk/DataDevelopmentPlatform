package com.dfssi.dataplatform.external.chargingPile.service;

import com.dfssi.dataplatform.external.chargingPile.dao.*;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeConnectorStatusInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeEquipmentInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeOrderInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeStationInfoEntity;
import com.dfssi.dataplatform.external.common.GpJdbcManger;
import com.dfssi.dataplatform.external.common.JsonUtils;
import com.dfssi.dataplatform.external.common.PropertiesUtil;
import com.dfssi.dataplatform.external.common.PubGlobal;
import com.dfssi.dataplatform.external.common.shedule.QuartzManager;
import com.dfssi.dataplatform.external.common.shedule.SheJob;
import com.dfssi.dataplatform.external.model.TopicManger;
import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/4 11:12
 */
@Configuration
public class MessageConsume {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected RestTemplate restTemplate;
    /**
     * @author bin.Y
     * Description:充电站基本信息消费
     * Date:  2018/10/5 10:13
     */
    @Bean
    public String startStationInfoConsumer() {
        OrderConsumer consumer = new OrderConsumer(TopicManger.stationInfoTopic, "stationInfo");
        consumer.start();
        return "";
    }

    /**
     * @author bin.Y
     * Description:充电接口状态变更消费
     * Date:  2018/10/5 10:13
     */
    @Bean
    public String startConnectorStatusConsumer() {
        OrderConsumer consumer = new OrderConsumer(TopicManger.stationStatusTopic, "connectorStatus");
        consumer.start();
        return "";
    }

    /**
     * @author bin.Y
     * Description:订单数据消费
     * Date:  2018/10/5 10:13
     */
    @Bean
    public String startOrderConsumer() {
        OrderConsumer consumer = new OrderConsumer(TopicManger.orderInfoTopic, "order");
        consumer.start();
        return "";
    }

    /**
     * @author bin.Y
     * Description:初始化jdbc配置
     * Date:  2018/10/12 14:43
     */
    @Bean
    public String initializeJdbc() throws Exception {
        new GpJdbcManger().setParam();
        return "";
    }


    /**
     * @author bin.Y
     * Description:初始化密匙管理
     * Date:  2018/10/12 14:43
     */
    @Bean
    public String initializeSecretMap() throws Exception {
        PubGlobal secretManager = new PubGlobal();
        //入参1代表换所有的密匙
        secretManager.cacheSecretMap(1);
        return "";
    }

    /**
     * @author bin.Y
     * Description:初始化定时任务
     * Date:  2018/10/12 14:43
     */
    @Bean
    public String initializeQuartzTask() throws Exception {
        Properties properties = PropertiesUtil.getProperties("application.properties");
        StationInfoQueryTask task =new StationInfoQueryTask(restTemplate);
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        QuartzManager.addJob("station_infro_query_task","group1",
                "station_infro_query_trigger","triggerGroup",task,SheJob.class,properties.getProperty("shedule.stationInfo.cron"));
        return "";
    }
}
