package com.dfssi.dataplatform.external.chargingPile.service;

import com.dfssi.dataplatform.external.chargingPile.entity.ChargeConnectorStatusInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeConnectorInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeEquipmentInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeOrderInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.ChargeStationInfoEntity;
import com.dfssi.dataplatform.external.common.GpJdbcManger;
import com.dfssi.dataplatform.external.common.JsonUtils;
import com.dfssi.dataplatform.external.common.RedisPoolManager;
import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class OrderConsumer extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Consumer<String, String> consumer;
    List<String> tpos = new ArrayList<String>();
    //单次处理条数
    private int batchLength = 1000;
    private int stationBatchLength = 500;
    private ConcurrentMap<String, ChargeOrderInfoEntity> orderMap = Maps.newConcurrentMap();
    private ConcurrentMap<String, ChargeConnectorStatusInfoEntity> connectorStatusMap = Maps.newConcurrentMap();
    private ConcurrentMap<String, ChargeStationInfoEntity> stationInfoMap = Maps.newConcurrentMap();
    private String processType;

    public OrderConsumer(String topic,String processType) {
        this.processType=processType;
        tpos.add(topic);
        createConsumer();
    }

    private void createConsumer() {
        try {
            InputStream configurationStream = RedisPoolManager.class.getClassLoader().getResourceAsStream("kafkaConsumer.properties");
            Properties props = new Properties();
            props.load(configurationStream);
            consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(tpos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        if("order".equals(processType)){
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                if (records.isEmpty() && orderMap.size() > 0) {
                    processOrderMap();
                }
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        ChargeOrderInfoEntity chargeOrderInfoEntity = JsonUtils.fromJson(record.value(), new ChargeOrderInfoEntity().getClass());
                        chargeOrderInfoEntity.setStartChargeSeq(chargeOrderInfoEntity.getStartChargeSeq());
                        orderMap.put(chargeOrderInfoEntity.getStartChargeSeq(), chargeOrderInfoEntity);
                        if (orderMap.size() == batchLength) {
                            processOrderMap();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if("connectorStatus".equals(processType)){
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
        }else if("stationInfo".equals(processType)){
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                if (records.isEmpty() && stationInfoMap.size() > 0) {
                    processStationInfoMap();
                }
                for (ConsumerRecord<String, String> record : records) {
                    try {

                        ChargeStationInfoEntity chargeStationInfoEntity = JsonUtils.fromJson(record.value(), new ChargeStationInfoEntity().getClass());
                        String[] pictures = chargeStationInfoEntity.getPictures();
                        if(pictures!=null){
                            chargeStationInfoEntity.setPicturesTmp(StringUtils.join(pictures, ","));
                        }
                        stationInfoMap.put(chargeStationInfoEntity.getStationID(), chargeStationInfoEntity);
                        if (stationInfoMap.size() == stationBatchLength) {
                            processStationInfoMap();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @author bin.Y
     * Description:批量处理订单数据
     * Date:  2018/10/5 11:37
     */
    public void processOrderMap() {
        Connection conn = null;
        try {
            conn = new GpJdbcManger().getConnection();
            PreparedStatement d_prest = conn.prepareStatement("delete from charge_order_info where start_charge_seq=?");
            PreparedStatement i_prest = conn.prepareStatement(" INSERT INTO charge_order_info(operator_id, connector_id, start_charge_seq,user_charge_type,\n" +
                    "        mobile_number,money,elect_money,service_money,elect,cusp_elect,cusp_elect_price,\n" +
                    "        cusp_service_price,cusp_money,cusp_elect_money,cusp_service_money,peak_elect,peak_elect_price,\n" +
                    "        peak_service_price,peak_money,peak_elect_money,peak_service_money,flat_elect,flat_elect_price,\n" +
                    "        flat_service_price,flat_money,flat_elect_money,flat_service_money,valley_elect,valley_elect_price,\n" +
                    "        valley_service_price,valley_money,valley_elect_money,valley_service_money,start_time,end_time,payment_amount,\n" +
                    "        pay_time,pay_channel,discount_info,total_power,total_elec_money,total_service_money,total_money,\n" +
                    "        stop_reason,sum_period\n" +
                    "        )\n" +
                    "        values\n" +
                    "        (?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,\n" +
                    "        ?,?,?,?,?,?,\n" +
                    "        ?,?,?\n" +
                    "        )");
            Iterator<Map.Entry<String, ChargeOrderInfoEntity>> entries = orderMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, ChargeOrderInfoEntity> entry = entries.next();
                //批量删除
                d_prest.setString(1, entry.getKey());
                d_prest.addBatch();
                //批量插入
                i_prest.setString(1, entry.getValue().getOperatorID());
                i_prest.setString(2, entry.getValue().getConnectorID());
                i_prest.setString(3, entry.getValue().getStartChargeSeq());
                i_prest.setInt(4, entry.getValue().getUserChargeType());
                i_prest.setString(5, entry.getValue().getMobileNumber()+"");
                i_prest.setDouble(6, entry.getValue().getMoney());
                i_prest.setDouble(7, entry.getValue().getElectMoney());
                i_prest.setDouble(8, entry.getValue().getServiceMoney());
                i_prest.setDouble(9, entry.getValue().getElect());
                i_prest.setDouble(10, entry.getValue().getCuspElect());

                i_prest.setDouble(11, entry.getValue().getCuspElectPrice());
                i_prest.setDouble(12, entry.getValue().getCuspServicePrice());
                i_prest.setDouble(13, entry.getValue().getCuspMoney());
                i_prest.setDouble(14, entry.getValue().getCuspElectMoney());
                i_prest.setDouble(15, entry.getValue().getCuspServiceMoney());

                i_prest.setDouble(16, entry.getValue().getPeakElect());
                i_prest.setDouble(17, entry.getValue().getPeakElectPrice());
                i_prest.setDouble(18, entry.getValue().getPeakServicePrice());
                i_prest.setDouble(19, entry.getValue().getPeakMoney());
                i_prest.setDouble(20, entry.getValue().getPeakElectMoney());


                i_prest.setDouble(21, entry.getValue().getPeakServiceMoney());
                i_prest.setDouble(22, entry.getValue().getFlatElect());
                i_prest.setDouble(23, entry.getValue().getFlatElectPrice());
                i_prest.setDouble(24, entry.getValue().getFlatServicePrice());
                i_prest.setDouble(25, entry.getValue().getFlatMoney());

                i_prest.setDouble(26, entry.getValue().getFlatElectMoney());
                i_prest.setDouble(27, entry.getValue().getFlatServiceMoney());
                i_prest.setDouble(28, entry.getValue().getValleyElect());
                i_prest.setDouble(29, entry.getValue().getValleyElectPrice());
                i_prest.setDouble(30, entry.getValue().getValleyServicePrice());
                i_prest.setDouble(31, entry.getValue().getValleyMoney());
                i_prest.setDouble(32, entry.getValue().getValleyElectMoney());
                i_prest.setDouble(33, entry.getValue().getValleyServiceMoney());
                i_prest.setString(34, entry.getValue().getStartTime());
                i_prest.setString(35, entry.getValue().getEndTime());

                i_prest.setDouble(36, entry.getValue().getPaymentAmount());
                i_prest.setString(37, entry.getValue().getPayTime());
                i_prest.setDouble(38, entry.getValue().getPayChannel());
                i_prest.setString(39, entry.getValue().getDiscountInfo());
                i_prest.setDouble(40, entry.getValue().getTotalPower());

                i_prest.setDouble(41, entry.getValue().getTotalElecMoney());
                i_prest.setDouble(42, entry.getValue().getTotalServiceMoney());
                i_prest.setDouble(43, entry.getValue().getTotalMoney());
                i_prest.setInt(44, entry.getValue().getStopReason());
                i_prest.setInt(45, entry.getValue().getSumPeriod());
                i_prest.addBatch();
            }
            d_prest.executeBatch();
            i_prest.executeBatch();
            conn.commit();//提交事务
            logger.debug("===============订单数据消费成功，消费条数："+orderMap.size());
        } catch (Exception e) {
            logger.debug("===============订单数据消费失败:"+e);
            e.printStackTrace();
        } finally {
            orderMap.clear();
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @author bin.Y
     * Description:批量处理充电接口状态数据
     * Date:  2018/10/5 11:36
     */
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
                i_prest.addBatch();
            }
            d_prest.executeBatch();
            i_prest.executeBatch();
            conn.commit();//提交事务
            logger.info("===============充电接口状态数据消费成功，消费条数："+connectorStatusMap.size());
        } catch (Exception e) {
            logger.info("===============充电接口状态数据消费失败:"+e);
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
    public void processStationInfoMap(){
        Connection conn = null;
        int stationCount=0;
        int equipmentCount=0;
        int connectorCount=0;
        try {
            conn = new GpJdbcManger().getConnection();
//            PreparedStatement d_prest_connector = conn.prepareStatement("delete from charge_connector_info\n" +
//                    "        where equipment_id in (\n" +
//                    "            select\n" +
//                    "                b.equipment_id\n" +
//                    "            from\n" +
//                    "                charge_station_info a,\n" +
//                    "                charge_equipment_info b\n" +
//                    "            where a.station_id = b.station_id\n" +
//                    "            and a.station_id = ?\n" +
//                    "        )");
            PreparedStatement d_prest_connector = conn.prepareStatement("delete from charge_connector_info where connector_id=?");
            PreparedStatement d_prest_equipment = conn.prepareStatement("delete from charge_equipment_info where equipment_id=?");
            PreparedStatement d_prest_station = conn.prepareStatement("delete from charge_station_info where station_id=?");
            PreparedStatement i_prest_station = conn.prepareStatement("INSERT INTO charge_station_info(station_id, operator_id, equipment_owner_id, station_name,\n" +
                    "            country_code, area_code,address,station_tel,service_tel,station_type,station_status,park_nums,\n" +
                    "            station_lng,station_lat,site_guide,construction,pictures,match_cars,park_info,park_owner,park_manager,\n" +
                    "            open_all_day,busine_hours,min_electricity_price,electricity_fee,service_fee,park_free,park_fee,payment,\n" +
                    "            support_order,remark)\n" +
                    "        VALUES\n" +
                    "        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
                    "\t ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
                    "\t ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
                    "         ?\n" +
                    "        )");//31个字段
            PreparedStatement i_prest_equipment = conn.prepareStatement(" INSERT INTO charge_equipment_info(equipment_id, manufacturer_id, equipment_model, equipment_name,\n" +
                    "        production_date,construction_time,equipment_type,equipment_status,equipment_power,\n" +
                    "        new_national_standard,equipment_lng,equipment_lat,station_id)\n" +
                    "        values\n" +
                    "            (?, ?, ?,\n" +
                    "             ?, ?, ?,\n" +
                    "             ?, ?, ?,\n" +
                    "             ?, ?, ?,?\n" +
                    "            )");//13个字段
            PreparedStatement i_prest_connector = conn.prepareStatement("INSERT INTO charge_connector_info(connector_id, connector_name, connector_type, voltage_upper_limits,\n" +
                    "            voltage_lower_limits,current,power,park_no,equipment_id)\n" +
                    "        values\n" +
                    "           (?, ?, ?,\n" +
                    "            ?, ?, ?,\n" +
                    "            ?, ?, ?\n" +
                    "            )");

            Iterator<Map.Entry<String, ChargeStationInfoEntity>> entries =stationInfoMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, ChargeStationInfoEntity> entry = entries.next();
                //删除充电站
                d_prest_station.setString(1, entry.getKey());
                d_prest_station.addBatch();
                //插入充电站
                i_prest_station.setString(1, entry.getValue().getStationID());
                i_prest_station.setString(2, entry.getValue().getOperatorID());
                i_prest_station.setString(3, entry.getValue().getEquipmentOwnerID());
                i_prest_station.setString(4, entry.getValue().getStationName());
                i_prest_station.setString(5, entry.getValue().getCountryCode());
                i_prest_station.setString(6, entry.getValue().getAreaCode());
                i_prest_station.setString(7, entry.getValue().getAddress());
                i_prest_station.setString(8, entry.getValue().getStationTel());
                i_prest_station.setString(9, entry.getValue().getServiceTel());
                i_prest_station.setInt(10, entry.getValue().getStationType());
                i_prest_station.setInt(11, entry.getValue().getStationStatus());
                i_prest_station.setInt(12, entry.getValue().getParkNums());
                i_prest_station.setDouble(13, entry.getValue().getStationLng());
                i_prest_station.setDouble(14,entry.getValue().getStationLat());
                i_prest_station.setString(15, entry.getValue().getSiteGuide());
                i_prest_station.setInt(16, entry.getValue().getConstruction());
                i_prest_station.setString(17, entry.getValue().getPicturesTmp());
                i_prest_station.setString(18, entry.getValue().getMatchCars());
                i_prest_station.setString(19, entry.getValue().getParkInfo());
                i_prest_station.setString(20, entry.getValue().getParkOwner());
                i_prest_station.setString(21, entry.getValue().getParkManager());
                i_prest_station.setInt(22, entry.getValue().getOpenAllDay());
                i_prest_station.setString(23, entry.getValue().getBusineHours());
                i_prest_station.setDouble(24,entry.getValue().getMinElectricityPrice());
                i_prest_station.setString(25, entry.getValue().getElectricityFee());
                i_prest_station.setString(26, entry.getValue().getServiceFee());
                i_prest_station.setInt(27, entry.getValue().getParkFree());
                i_prest_station.setString(28, entry.getValue().getParkFee());
                i_prest_station.setString(29, entry.getValue().getPayment());
                i_prest_station.setInt(30, entry.getValue().getSupportOrder());
                i_prest_station.setString(31, entry.getValue().getRemark());
                i_prest_station.addBatch();
                stationCount+=1;
                //插入充电桩
                for(ChargeEquipmentInfoEntity chargeEquipmentInfoEntity:entry.getValue().getEquipmentInfos()){
                    //删除充电桩
                    d_prest_equipment.setString(1, chargeEquipmentInfoEntity.getEquipmentID());
                    d_prest_equipment.addBatch();
                    i_prest_equipment.setString(1, chargeEquipmentInfoEntity.getEquipmentID());
                    i_prest_equipment.setString(2, chargeEquipmentInfoEntity.getManufacturerID());
                    i_prest_equipment.setString(3, chargeEquipmentInfoEntity.getEquipmentModel());
                    i_prest_equipment.setString(4, chargeEquipmentInfoEntity.getEquipmentName());
                    i_prest_equipment.setString(5, chargeEquipmentInfoEntity.getProductionDate());
                    i_prest_equipment.setString(6, chargeEquipmentInfoEntity.getConstructionTime());
                    i_prest_equipment.setInt(7,chargeEquipmentInfoEntity.getEquipmentType());
                    i_prest_equipment.setInt(8,chargeEquipmentInfoEntity.getEquipmentStatus());
                    i_prest_equipment.setDouble(9, chargeEquipmentInfoEntity.getEquipmentPower());
                    i_prest_equipment.setInt(10, chargeEquipmentInfoEntity.getNewNationalStandard());
                    i_prest_equipment.setDouble(11, chargeEquipmentInfoEntity.getEquipmentLng());
                    i_prest_equipment.setDouble(12, chargeEquipmentInfoEntity.getEquipmentLat());
                    i_prest_equipment.setString(13, entry.getKey());
                    i_prest_equipment.addBatch();
                    equipmentCount+=1;
                    //插入充电接口
                    for(ChargeConnectorInfoEntity chargeConnectorInfoEntity:chargeEquipmentInfoEntity.getConnectorInfos()){
                        //删除充电接口
                        d_prest_connector.setString(1, chargeConnectorInfoEntity.getConnectorID());
                        d_prest_connector.addBatch();
                        i_prest_connector.setString(1, chargeConnectorInfoEntity.getConnectorID());
                        i_prest_connector.setString(2, chargeConnectorInfoEntity.getConnectorName());
                        i_prest_connector.setInt(3, chargeConnectorInfoEntity.getConnectorType());
                        i_prest_connector.setInt(4, chargeConnectorInfoEntity.getVoltageUpperLimits());
                        i_prest_connector.setInt(5, chargeConnectorInfoEntity.getVoltageLowerLimits());
                        i_prest_connector.setInt(6, chargeConnectorInfoEntity.getCurrent());
                        i_prest_connector.setDouble(7, chargeConnectorInfoEntity.getPower());
                        i_prest_connector.setString(8, chargeConnectorInfoEntity.getParkNo());
                        i_prest_connector.setString(9, chargeEquipmentInfoEntity.getEquipmentID());
                        i_prest_connector.addBatch();
                        connectorCount+=1;
                    }
                }
            }
            d_prest_connector.executeBatch();
            d_prest_equipment.executeBatch();
            d_prest_station.executeBatch();
            i_prest_station.executeBatch();
            i_prest_equipment.executeBatch();
            i_prest_connector.executeBatch();
            conn.commit();//提交事务
            logger.info("===============充电接口状态数据消费成功，充电站消费数量:{},充电桩消费数量:{},充电接口消费数量:{}",stationCount,equipmentCount,connectorCount);
        } catch (Exception e) {
            logger.info("===============充电站基本信息数据消费失败:"+e);
            e.printStackTrace();
        } finally {
            stationInfoMap.clear();
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

