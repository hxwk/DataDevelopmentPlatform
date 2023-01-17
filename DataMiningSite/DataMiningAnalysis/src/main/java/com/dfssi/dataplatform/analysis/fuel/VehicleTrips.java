package com.dfssi.dataplatform.analysis.fuel;

import com.dfssi.common.databases.DBCommon;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/19 9:50
 */
public class VehicleTrips {

    private Connection connection;
    private String table;

    public VehicleTrips(String table, Connection connection){
        this.connection = connection;
        this.table = table;
    }

    public TripRecord queryTripByVid(String vid) throws SQLException {

        //获取最后一次行程
        String sql = String.format("select * from %s where vid = '%s' order by endtime desc limit 1", table, vid);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        TripRecord trip = parseResultSetForRecord(resultSet);
        if(!trip.isEmpty() && trip.getIsover() == 1 && trip.getIsvalid() == 1)
            trip = newEmptyTrip();

        DBCommon.close(resultSet);
        DBCommon.close(statement);

        return trip;
    }

    public TripRecord newEmptyTrip() {
        TripRecord trip = new TripRecord();
        trip.setEmpty(true);
        return trip;
    }

    public void deleteTrip(String tripid) throws SQLException{

        String sql = String.format("delete from %s where id='%s'", table, tripid);
        Statement statement = connection.createStatement();
        statement.execute(sql);
        DBCommon.close(statement);
    }

    public void updateTrip(TripRecord trip) throws SQLException {

        String sql = String.format("update %s set isover=?, sim=?, starttime=?, endtime=?, interval=?, starttotalmile=?, endtotalmile=?, starttotalfuel=?, endtotalfuel=?, startlat=?, startlon=?, endlat=?, endlon=?, isvalid=? where id=? ", table);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, trip.getIsover());
        preparedStatement.setString(2, trip.getSim());

        preparedStatement.setLong(3, trip.getStarttime());
        preparedStatement.setLong(4, trip.getEndtime());
        preparedStatement.setLong(5, trip.getInterval());

        preparedStatement.setDouble(6, trip.getStarttotalmile());
        preparedStatement.setDouble(7, trip.getEndtotalmile());
        preparedStatement.setDouble(8, trip.getStarttotalfuel());
        preparedStatement.setDouble(9, trip.getEndtotalfuel());

        preparedStatement.setDouble(10, trip.getStartlat());
        preparedStatement.setDouble(11, trip.getStartlon());
        preparedStatement.setDouble(12, trip.getEndlat());
        preparedStatement.setDouble(13, trip.getEndlon());

        preparedStatement.setInt(14, trip.getIsvalid());

        preparedStatement.setString(15, trip.getId());

        preparedStatement.executeUpdate();
        DBCommon.close(preparedStatement);
    }

    public void insertTrip(TripRecord trip) throws SQLException {

        String sql = String.format("insert into %s (isover, sim, starttime, endtime, interval, starttotalmile, endtotalmile, starttotalfuel, endtotalfuel, startlat, startlon, endlat, endlon, isvalid, id, iseco, vid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", table);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, trip.getIsover());
        preparedStatement.setString(2, trip.getSim());

        preparedStatement.setLong(3, trip.getStarttime());
        preparedStatement.setLong(4, trip.getEndtime());
        preparedStatement.setLong(5, trip.getInterval());

        preparedStatement.setDouble(6, trip.getStarttotalmile());
        preparedStatement.setDouble(7, trip.getEndtotalmile());
        preparedStatement.setDouble(8, trip.getStarttotalfuel());
        preparedStatement.setDouble(9, trip.getEndtotalfuel());

        preparedStatement.setDouble(10, trip.getStartlat());
        preparedStatement.setDouble(11, trip.getStartlon());
        preparedStatement.setDouble(12, trip.getEndlat());
        preparedStatement.setDouble(13, trip.getEndlon());

        preparedStatement.setInt(14, trip.getIsvalid());

        preparedStatement.setString(15, trip.getId());
        preparedStatement.setInt(16, trip.getIseco());
        preparedStatement.setString(17, trip.getVid());

        preparedStatement.executeUpdate();
        DBCommon.close(preparedStatement);
    }


    public void insertTrips(TripRecord[] trips) throws SQLException {

        String sql = String.format("insert into %s (isover, sim, starttime, endtime, interval, starttotalmile, endtotalmile, starttotalfuel, endtotalfuel, startlat, startlon, endlat, endlon, isvalid, id, iseco, vid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", table);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(TripRecord trip : trips) {

            preparedStatement.setInt(1, trip.getIsover());
            preparedStatement.setString(2, trip.getSim());

            preparedStatement.setLong(3, trip.getStarttime());
            preparedStatement.setLong(4, trip.getEndtime());
            preparedStatement.setLong(5, trip.getInterval());

            preparedStatement.setDouble(6, trip.getStarttotalmile());
            preparedStatement.setDouble(7, trip.getEndtotalmile());
            preparedStatement.setDouble(8, trip.getStarttotalfuel());
            preparedStatement.setDouble(9, trip.getEndtotalfuel());

            preparedStatement.setDouble(10, trip.getStartlat());
            preparedStatement.setDouble(11, trip.getStartlon());
            preparedStatement.setDouble(12, trip.getEndlat());
            preparedStatement.setDouble(13, trip.getEndlon());

            preparedStatement.setInt(14, trip.getIsvalid());

            preparedStatement.setString(15, trip.getId());
            preparedStatement.setInt(16, trip.getIseco());
            preparedStatement.setString(17, trip.getVid());

            preparedStatement.addBatch();
        }

        preparedStatement.executeUpdate();
        DBCommon.close(preparedStatement);
    }

    private TripRecord parseResultSetForRecord(ResultSet resultSet) throws SQLException{
        List<TripRecord> records = parseResultSet(resultSet);
        if(records.isEmpty()){
            return newEmptyTrip();
        }else {
            return records.get(0);
        }
    }

    private List<TripRecord> parseResultSet(ResultSet resultSet) throws SQLException {

        List<TripRecord> records = Lists.newArrayList();
        TripRecord trip;
        int n = 0;
        while (resultSet.next()){
            trip = new TripRecord();
            n = n + 1;

            trip.setId(resultSet.getString("id"));
            trip.setSim(resultSet.getString("sim"));
            trip.setVid(resultSet.getString("vid"));

            trip.setStarttime(resultSet.getLong("starttime"));
            trip.setEndtime(resultSet.getLong("endtime"));
            trip.setInterval(resultSet.getLong("interval"));

            trip.setStarttotalmile(resultSet.getDouble("starttotalmile"));
            trip.setEndtotalmile(resultSet.getDouble("endtotalmile"));

            trip.setStarttotalfuel(resultSet.getDouble("starttotalfuel"));
            trip.setEndtotalfuel(resultSet.getDouble("endtotalfuel"));

            trip.setStartlat(resultSet.getDouble("startlat"));
            trip.setEndlat(resultSet.getDouble("endlat"));

            trip.setStartlon(resultSet.getDouble("startlon"));
            trip.setEndlon(resultSet.getDouble("endlon"));

            trip.setIseco(resultSet.getInt("iseco"));
            trip.setIsover(resultSet.getInt("isover"));
            trip.setIsvalid(resultSet.getInt("isvalid"));

            trip.setEmpty(false);

            records.add(trip);
        }

        return records;
    }
}
