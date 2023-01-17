package com.dfssi.dataplatform.external.chargingPile.service;

import com.dfssi.dataplatform.external.chargingPile.entity.ChargeConnectorStatusInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeOrderInfoEntity;
import com.dfssi.dataplatform.external.common.GpJdbcManger;
import com.dfssi.dataplatform.external.common.JsonUtils;
import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ConnectorStatusConsumer extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Consumer<String, String> consumer;
    List<String> tpos = new ArrayList<String>();
    //单次处理条数
    private int batchLength = 50;
    private ConcurrentMap<String, ChargeConnectorStatusInfoEntity> connectorStatusMap = Maps.newConcurrentMap();

    public ConnectorStatusConsumer(String topic) {
        tpos.add(topic);
        createConsumer();
    }

    private void createConsumer() {
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", "172.16.1.121:9092,172.16.1.122:9092,172.16.1.123:9092");
            props.put("group.id", "group1");
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("auto.offset.reset", "earliest");
            props.put("key.deserializer", StringDeserializer.class.getName());
            props.put("value.deserializer", StringDeserializer.class.getName());
            consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(tpos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (records.isEmpty() && connectorStatusMap.size() > 0) {
                processStatusMap();
            }
            for (ConsumerRecord<String, String> record : records) {
                try {
                    ChargeConnectorStatusInfoEntity chargeStationInfoEntity = JsonUtils.fromJson(record.value(), new ChargeConnectorStatusInfoEntity().getClass());
                    connectorStatusMap.put(chargeStationInfoEntity.getConnectorID(), chargeStationInfoEntity);
                    if (connectorStatusMap.size() == batchLength) {
                        processStatusMap();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void processStatusMap() {
        Connection conn = null;
        try {
            conn = new GpJdbcManger().getConnection();
            PreparedStatement d_prest = conn.prepareStatement("delete from charge_connector_status_info where connector_id=?");
            PreparedStatement i_prest = conn.prepareStatement("INSERT INTO charge_connector_status_info(connector_id, status, current_a,current_b,\n" +
                    "        current_c,voltage_a,voltage_b,voltage_c,park_status,lock_status,soc,connector_time\n" +
                    "        )\n" +
                    "        values\n" +
                    "        (?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,\n" +
                    "        ?,current_timestamp\n" +
                    "        )");
            Iterator<Map.Entry<String, ChargeConnectorStatusInfoEntity>> entries = connectorStatusMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, ChargeConnectorStatusInfoEntity> entry = entries.next();
                //批量删除
                d_prest.setString(1, entry.getKey());
                d_prest.addBatch();
                //批量插入
                i_prest.setString(1,entry.getValue().getConnectorID());
                i_prest.setInt(2,entry.getValue().getStatus());
                i_prest.setInt(3,entry.getValue().getCurrentA());
                i_prest.setInt(4,entry.getValue().getCurrentB());
                i_prest.setInt(5,entry.getValue().getCurrentC());
                i_prest.setInt(6,entry.getValue().getVoltageA());
                i_prest.setInt(7,entry.getValue().getVoltageB());
                i_prest.setInt(8,entry.getValue().getVoltageC());
                i_prest.setInt(9,entry.getValue().getParkStatus());
                i_prest.setInt(10,entry.getValue().getLockStatus());
                i_prest.setDouble(11,entry.getValue().getSOC());
            }
            d_prest.executeBatch();
            i_prest.executeBatch();
            conn.commit();//提交事务
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectorStatusMap.clear();
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

