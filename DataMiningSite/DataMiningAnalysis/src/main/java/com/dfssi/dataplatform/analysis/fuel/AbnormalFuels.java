package com.dfssi.dataplatform.analysis.fuel;

import com.dfssi.common.UUIDs;
import com.dfssi.common.databases.DBCommon;
import com.dfssi.common.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Description:
 *   异常油耗检测
 * @author LiXiaoCong
 * @version 2018/2/5 8:53
 */
public class AbnormalFuels implements Serializable{

    private transient Logger logger;

    private double maxConsumption;
    private String table;
    private String vid;

    private boolean empty;

    private String id;
    private long starttime;
    private long endtime;
    private String positions;
    private int count;

    public AbnormalFuels(String table, String vid, double maxConsumption){
        this.table = table;
        this.vid = vid;
        this.maxConsumption = maxConsumption;
        this.empty = true;
    }

    public void checkAbnormalFuel(double fuel,
                                  double mile,
                                  long time,
                                  double lon,
                                  double lat,
                                  Connection connection) {

        double consumption = Maths.precision(fuel * 100 / mile, 3);
        if(consumption > maxConsumption){

            this.empty = false;

            //两条油耗异常超出了时间合并范围
            if(this.id != null && this.endtime - time > 5 * 60 * 1000L){
                    this.id = null;
                    this.count = 0;
            }

            try {
                //油耗异常
                if(this.id == null){
                    //生成新的油耗异常数据
                    this.id = UUIDs.uuidFromBytesWithNoSeparator(String.format("%s,%s,%s,%s,%s", vid, fuel, mile, lon, lat).getBytes());
                    this.starttime = time;
                    this.endtime = time;
                    this.positions = String.format("%s,%s", lon, lat);
                    this.count = 1;

                    insertAbnormalFuel(connection);
                }else{
                    this.endtime = time;
                    this.positions = String.format("%s;%s,%s", this.positions, lon, lat);
                    this.count = this.count + 1;
                    updateAbnormalFuel(connection);
                }
            } catch (SQLException e) {
                getLogger().error(String.format("检查油耗异常失败：vid = %s, fuel = %s, gpsTime = %s", vid, fuel, time), e);
            }
        }
    }

    public boolean isEmpty(){
        return empty;
    }


    private void updateAbnormalFuel(Connection connection) throws SQLException {

        String sql = String.format("update %s set endtime=?, positions=?, count=? where id=? ", table);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, endtime);
        preparedStatement.setString(2, positions);
        preparedStatement.setInt(3, count);
        preparedStatement.setString(4, id);

        preparedStatement.executeUpdate();
        DBCommon.close(preparedStatement);
    }

    private void insertAbnormalFuel(Connection connection) throws SQLException {

        String sql = String.format("insert into %s (id, vid, starttime, endtime, positions, count) values (?,?,?,?,?,?) ", table);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, vid);

        preparedStatement.setLong(3, starttime);
        preparedStatement.setLong(4, endtime);

        preparedStatement.setString(5, positions);

        preparedStatement.setInt(6, count);

        preparedStatement.executeUpdate();
        DBCommon.close(preparedStatement);
    }

    private Logger getLogger(){
        if(logger == null){
            this.logger = LoggerFactory.getLogger(AbnormalFuels.class);
        }
        return logger;
    }

}
