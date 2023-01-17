package com.dfssi.dataplatform.analysis.route.fence;

import com.dfssi.common.databases.DBCommon;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description:
 *    栅栏查询：
 *        使用了内连接，
 *        查询理论步骤为：
 *            通过线路ID先从线路栅栏表中 获取 线路起点和终点的栅栏ID，
 *            然后通过栅栏ID获取对应的栅栏经纬度范围
 *
 *            type（1-起点栅栏，2-终点栅栏，3-休息点，4-黑点）
 *            select sg.POINT from SSI_ROUTE_REGION srg , SSI_REGION sg  where srg.route_id = '188' && srg.type < 3 && srg.region_id = sg.ID
 *
 * @author LiXiaoCong
 * @version 2018/1/29 10:29
 */
public class FenceSearcher {
    private final Logger logger = LoggerFactory.getLogger(FenceSearcher.class);

    private String routeFenceTable;
    private String fenceTable;

    public FenceSearcher(String routeFenceTable, String fenceTable){
        this.routeFenceTable = routeFenceTable;
        this.fenceTable = fenceTable;
    }

    public String[] searchFence(String routeId, Connection connection){

        String sql = String.format(" select sg.POINT, srg.type from %s srg , %s sg  where srg.route_id = '%s' && srg.type < 3 && srg.region_id = sg.ID",
                routeFenceTable, fenceTable, routeId);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            String startPoint = null;
            String endPoint = null;

            int type;
            while (resultSet.next()){
                type = resultSet.getInt("type");
                if(type == 1){
                    startPoint = resultSet.getString("POINT");
                }else if(type == 2){
                    endPoint = resultSet.getString("POINT");
                }
            }

            Preconditions.checkArgument(startPoint != null && endPoint != null,
                    String.format("线路 %s 不存在起点或终点。", routeId));


           return new String[]{startPoint, endPoint};

        } catch (SQLException e) {
            logger.error(String.format("路线%s的起点和终点栅栏查询失败。", routeId), e);
        }finally {
            DBCommon.close(resultSet);
            DBCommon.close(preparedStatement);
        }

        return null;
    }

}
