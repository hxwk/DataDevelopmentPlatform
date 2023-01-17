package com.dfssi.dataplatform.analysis.route.order;

import com.dfssi.common.databases.DBCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Description:
 *   根据vid 查询 最后一条订运单信息
 * @author LiXiaoCong
 * @version 2018/1/26 15:07
 */
public class OrderSearcher {
    private final Logger logger = LoggerFactory.getLogger(OrderSearcher.class);

    private String table;

    public OrderSearcher(String table){
        this.table = table;
    }

    public OrderRecord searchOrder(String vid, Connection connection){

        String sql = String.format("select ORDER_NO, TRSPORT_LINE_ID, STATUS, START_TIME, END_TIME from %s where VID = '%s' order by START_TIME desc limit 1",
                table, vid);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                //0、添加 1、运输中 2、已到达
                int status = resultSet.getInt("STATUS");
                String orderNo = resultSet.getString("ORDER_NO");
                String lineId = resultSet.getString("TRSPORT_LINE_ID");

                Date startTime = resultSet.getDate("START_TIME");
                Date endTime = resultSet.getDate("END_TIME");

                OrderRecord orderRecord = new OrderRecord();
                orderRecord.setOrderNo(orderNo);
                orderRecord.setRouteId(lineId);
                orderRecord.setStatus(status);
                orderRecord.setStarttime(startTime.getTime());

                if(endTime != null)orderRecord.setEndtime(endTime.getTime());

                return orderRecord;
            }

        } catch (SQLException e) {
            logger.error(String.format("车辆%s的订单查询失败。", vid), e);
        }finally {
            DBCommon.close(resultSet);
            DBCommon.close(preparedStatement);
        }

        return null;

    }

}
