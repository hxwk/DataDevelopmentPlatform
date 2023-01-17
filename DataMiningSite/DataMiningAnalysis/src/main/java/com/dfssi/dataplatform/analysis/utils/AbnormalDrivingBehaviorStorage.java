package com.dfssi.dataplatform.analysis.utils;

import com.dfssi.common.UUIDs;
import com.dfssi.common.net.HttpPoster;
import com.dfssi.common.net.Https;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *  指定vid的车辆异常状态记录
 * @author LiXiaoCong
 * @version 2018/2/22 10:58
 */
public class AbnormalDrivingBehaviorStorage implements Serializable{

    private transient Logger logger;
    private String table;
    private String vid;
    private Map<String, Integer> alarmLabelMap;
    private String reportUrl;

    //最后位置
    private double lat;
    private double lon;
    private double speed;
    private int degree;
    private long gpsTime;

    //异常对应的id
    private Map<String, String> alarmIdMap;

    //异常最后出现时间
    private Map<String, Long> alarmTimsMap;

    //异常次数
    private Map<String, Integer> alarmCountMap;

    private transient PreparedStatement updateStatement;
    private transient PreparedStatement insertStatement;


    public AbnormalDrivingBehaviorStorage(String table,
                                          Map<String, Integer> alarmLabelMap,
                                          String vid,
                                          String reportUrl){
        this.table = table;
        this.vid = vid;
        this.alarmLabelMap = alarmLabelMap;
        this.reportUrl = reportUrl;

        this.alarmTimsMap = Maps.newHashMap();
        this.alarmCountMap = Maps.newHashMap();
        this.alarmIdMap = Maps.newHashMap();
    }


    public void checkAbnormalDrivingBehavior(Set<String> alarms,
                                             int degree,
                                             long gpsTime,
                                             double lon,
                                             double lat,
                                             double speed,
                                             Connection connection)  {
        if(alarms != null && !alarms.isEmpty()) {
            this.degree = degree;
            this.gpsTime = gpsTime;
            this.lon = lon;
            this.lat = lat;
            this.speed = speed;

            try {
                updateAlarmStatus(alarms, connection);
                //execute();
            } catch (SQLException e) {
                getLogger().error(String.format("检查驾驶行为告警信息失败：vid = %s, alarms = %s, gpsTime = %s", vid, alarms, gpsTime), e);
            }
        }
    }

    private void updateAlarmStatus(Set<String> alarms,
                                   Connection connection) throws SQLException {
        String id;
        for(String alarm : alarms){
            if("保留位".equals(alarm))continue;

           id = alarmIdMap.get(alarm);
           if(id == null){
              putNewAlarm(alarm, connection);
           }else{
               Long lastTime = alarmTimsMap.get(id);
               if(lastTime != null && gpsTime > lastTime && (gpsTime - lastTime <= 5 * 60 * 1000L)){
                   Integer count = alarmCountMap.get(id);
                   if(count == null)count = 0;
                   count = count + 1;
                   updateAbnormalDrivingBehaviorEvent(id, gpsTime, count, connection);

                   alarmCountMap.put(id, count);
                   alarmTimsMap.put(id, gpsTime);
               }else {
                   removeAlarm(alarm, id);
                   putNewAlarm(alarm, connection);
               }
           }
        }
    }

    private void putNewAlarm(String alarm, Connection connection) throws SQLException {
         //新的异常 入库 并记录
        String id = UUIDs.uuidFromBytesWithNoSeparator(String.format("%s,%s,%s", vid, alarm, gpsTime).getBytes());

        insertAbnormalDrivingBehaviorEvent(id, gpsTime, gpsTime, 1, alarm,  connection);

        alarmIdMap.put(alarm, id);
        alarmCountMap.put(id, 1);
        alarmTimsMap.put(id, gpsTime);
    }

    private void removeAlarm(String alarm, String id){
        this.alarmIdMap.remove(alarm);
        alarmTimsMap.remove(id);
        alarmCountMap.remove(id);
    }


    private void updateAbnormalDrivingBehaviorEvent(String id,
                                                    long endtime,
                                                    int count,
                                                    Connection connection) throws SQLException {

        PreparedStatement preparedStatement = getUpdateStatement(connection);
        preparedStatement.setLong(1, endtime);
        preparedStatement.setDouble(2, lon);
        preparedStatement.setDouble(3, lat);
        preparedStatement.setInt(4, count);
        preparedStatement.setDouble(5, speed);
        preparedStatement.setString(6, id);

        preparedStatement.addBatch();
    }


    private void insertAbnormalDrivingBehaviorEvent(String id,
                                                   long starttime,
                                                   long endtime,
                                                   int count,
                                                   String alarm,
                                                   Connection connection) throws SQLException {

        int alarmLabel = getAlarmLabel(alarm);

        PreparedStatement preparedStatement = getInsertStatement(connection);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, vid);

        preparedStatement.setLong(3, starttime);
        preparedStatement.setLong(4, endtime);

        preparedStatement.setDouble(5, lon);
        preparedStatement.setDouble(6, lat);

        preparedStatement.setDouble(7, speed);
        preparedStatement.setInt(8, count);
        preparedStatement.setString(9, alarm);
        preparedStatement.setInt(10, degree);
        preparedStatement.setInt(11, alarmLabel);

        preparedStatement.addBatch();

        //上报驾驶异常行为
        reportAbnormalDrivingBehavior(alarm, alarmLabel, endtime, degree);
    }

    private void reportAbnormalDrivingBehavior(String alarm,
                                               int alarmLabel,
                                               long time,
                                               int degree){

        if(reportUrl != null && reportUrl.length() > 0){
            HttpPoster post = Https.post(reportUrl, true);
            post.addParam("vid", vid);
            post.addParam("alarm", alarm);
            post.addParam("alarmLabel", alarmLabel);
            post.addParam("degree", degree);
            post.addParam("entime", time);

            try {
                post.execute();
            } catch (Exception e) {
                getLogger().error(String.format("上报驾驶异常行为失败，reportUrl = %s", reportUrl), e);
            }
        }

    }

    private Logger getLogger(){
        if(logger == null){
            this.logger = LoggerFactory.getLogger(AbnormalDrivingBehaviorStorage.class);
        }
        return logger;
    }

    private int getAlarmLabel(String alarm){
        Integer integer = alarmLabelMap.get(alarm);
        if(integer == null) integer = -1;
        return integer;
    }

    private PreparedStatement getUpdateStatement(Connection connection) throws SQLException {

        if(updateStatement == null){
            String sql = String.format("update %s set endtime=?, lon=?, lat=?, count=?, speed=? where id=? ", table);
            updateStatement = connection.prepareStatement(sql);
        }

        return updateStatement;
    }

    private PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        if(insertStatement == null){
            String sql = String.format("insert into %s (id, vid, starttime, endtime, lon, lat, speed, count, alarm, degree, alarmlabel) values (?,?,?,?,?,?,?,?,?,?,?) ", table);
            insertStatement = connection.prepareStatement(sql);
        }
        return insertStatement;
    }

    public void execute() throws SQLException {
        if(insertStatement != null){
            insertStatement.executeBatch();
            insertStatement.close();
            insertStatement = null;
        }

        if(updateStatement != null){
            updateStatement.executeBatch();
            updateStatement.close();
            updateStatement = null;
        }
    }
}
