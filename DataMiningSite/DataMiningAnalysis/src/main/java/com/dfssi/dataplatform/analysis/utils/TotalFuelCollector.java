package com.dfssi.dataplatform.analysis.utils;

import com.dfssi.common.databases.DBCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description:
 *   全量油耗统计
 *       统计每辆车从出厂到现在的 总运行时长、总行驶里程、总油耗
 * @author LiXiaoCong
 * @version 2018/2/23 10:06
 */
public class TotalFuelCollector implements Serializable {
    private transient Logger logger;

    private String table;
    private String vid;
    private TotalFuel totalFuel;

    public TotalFuelCollector(String table, String vid){
        this.table = table;
        this.vid = vid;

        //initTotalFuel();
    }

    //从数据库中获取总计油耗数据
    private void initTotalFuel(Connection connection) throws SQLException {
        String sql = String.format("select endtime, totalmile, totalfuel, totaltime from %s where vid = '%s'", table, vid);

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            this.totalFuel = new TotalFuel(resultSet.getLong("endtime"),
                    resultSet.getDouble("totalmile"),
                    resultSet.getDouble("totalfuel"),
                    resultSet.getLong("totaltime"));
        }
        DBCommon.close(resultSet);
        DBCommon.close(preparedStatement);
    }


    public void updateTotalFuel(long gpsTime,
                                double totalmile,
                                double totalfuel,
                                long interval,
                                Connection connection) throws SQLException {
        if(totalFuel == null) {
            initTotalFuel(connection);
            //首次记录
            if(totalFuel == null){
                insertTotalFuel(gpsTime, totalmile, totalfuel, interval, connection);
                totalFuel = new TotalFuel(gpsTime, totalmile, totalfuel, interval);
            }else {
                updateTotalFuelDetail(gpsTime, totalmile, totalfuel, interval);
            }
        }else {
            updateTotalFuelDetail(gpsTime, totalmile, totalfuel, interval);
        }
    }

    private void updateTotalFuelDetail(long gpsTime,
                                       double totalmile,
                                       double totalfuel,
                                       long interval){
        totalFuel.endtime = gpsTime;
        totalFuel.totalmile = totalmile;
        totalFuel.totalfuel = totalfuel;
        totalFuel.totaltime += interval;

        // executeUpdate(connection);
    }

    public void updateTotalFuelEndTime(long gpsTime, long interval){
        if(totalFuel != null){
            totalFuel.endtime = gpsTime;
            totalFuel.totaltime += interval;
        }
    }


    public void executeUpdate(Connection connection) throws SQLException {
        if(totalFuel != null) {
            String sql = String.format("update %s set endtime=?, totalmile=?, totalfuel=?, totaltime=? where vid=? ", table);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, totalFuel.endtime);
            preparedStatement.setDouble(2, totalFuel.totalmile);
            preparedStatement.setDouble(3, totalFuel.totalfuel);
            preparedStatement.setLong(4, totalFuel.totaltime);
            preparedStatement.setString(5, vid);

            preparedStatement.executeUpdate();
            DBCommon.close(preparedStatement);
        }
    }



    private void insertTotalFuel(long gpsTime,
                                 double totalmile,
                                 double totalfuel,
                                 long interval,
                                 Connection connection) throws SQLException {
        String sql = String.format("insert into %s (vid, starttime, endtime, totalmile, totalfuel, totaltime) values (?,?,?,?,?,?) ", table);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, vid);

        preparedStatement.setLong(2, gpsTime);
        preparedStatement.setLong(3, gpsTime);

        preparedStatement.setDouble(4, totalmile);
        preparedStatement.setDouble(5, totalfuel);

        preparedStatement.setLong(6, interval);

        preparedStatement.executeUpdate();
        DBCommon.close(preparedStatement);
    }


    private Logger getLogger(){
        if(logger == null){
            this.logger = LoggerFactory.getLogger(TotalFuelCollector.class);
        }
        return logger;
    }

    private class TotalFuel implements Serializable{
        private long endtime;
        private double totalmile;
        private double totalfuel;
        private long totaltime;

        public TotalFuel(long endtime,
                         double totalmile,
                         double totalfuel,
                         long totaltime) {
            this.endtime = endtime;
            this.totalmile = totalmile;
            this.totalfuel = totalfuel;
            this.totaltime = totaltime;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TotalFuel{");
            sb.append("vid=").append(vid);
            sb.append("endtime=").append(endtime);
            sb.append(", totalmile=").append(totalmile);
            sb.append(", totalfuel=").append(totalfuel);
            sb.append(", totaltime=").append(totaltime);
            sb.append('}');
            return sb.toString();
        }
    }

}
