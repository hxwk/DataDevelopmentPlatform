package com.dfssi.dataplatform.analysis.route;

import com.dfssi.common.geo.Geos;
import com.dfssi.dataplatform.analysis.route.fence.FenceSearcher;
import com.dfssi.dataplatform.analysis.route.order.OrderRecord;
import com.dfssi.dataplatform.analysis.route.order.OrderSearcher;
import com.dfssi.resources.ConfigDetail;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.sql.Connection;
import java.util.List;

/**
 * Description:
 *   线路查询  根据数据中的vid查询对应的订运单数据，获取 线路ID
 *   再根据线路ID 获取 对应的 起始栅栏ID
 *   根据起始栅栏ID 获取 对应的 栅栏 经纬度范围。
 * @author LiXiaoCong
 * @version 2018/1/24 18:53
 */
public class RouteSearcher {
    private final Logger logger = LoggerFactory.getLogger(RouteSearcher.class);

    private FenceSearcher fenceSearcher;
    private OrderSearcher orderSearcher;

    public RouteSearcher(ConfigDetail config){

        String orderTable = config.getConfigItemValue("fuel.config.order.table", "SSI_ORDER_DD");
        this.orderSearcher = new OrderSearcher(orderTable);

        String routeFenceTable = config.getConfigItemValue("fuel.config.routefence.table", "SSI_ROUTE_REGION");
        String fenceTable = config.getConfigItemValue("fuel.config.fence.table", "SSI_REGION");
        this.fenceSearcher = new FenceSearcher(routeFenceTable, fenceTable);
    }

    public OrderRecord search(String vid, Connection connection){
        //获取最新订单
        OrderRecord orderRecord = orderSearcher.searchOrder(vid, connection);
        if(orderRecord != null){
            //查询最新订单中的线路的起始位置的栅栏
            String[] fences = fenceSearcher.searchFence(orderRecord.getRouteId(), connection);
            if(fences != null){
                List<Point2D.Double> startFence = getFenceRange(fences[0]);
                List<Point2D.Double> endFence = getFenceRange(fences[1]);

                orderRecord.setStartFence(startFence);
                orderRecord.setEndFence(endFence);
            }
        }
        return orderRecord;
    }


    private List<Point2D.Double> getFenceRange(String fence){
        String[] points = fence.split(";");
        List<Point2D.Double> pts = Lists.newArrayList();
        for(String point : points){
            String[] split = point.split(",");
            if(split.length == 3)
                pts.add(new Point2D.Double(Double.parseDouble(split[1]), Double.parseDouble(split[2])));

        }
        return pts;
    }

    public static void main(String[] args) {

        String fence = "0,114.3004636757965,30.54637909809649;1,114.31007671290592,30.54637909809649;2,114.31007671290592,30.539208696411333;3,114.3004636757965,30.539208696411333;";

        String[] points = fence.split(";");
        List<Point2D.Double> pts = Lists.newArrayList();
        for(String point : points){
            String[] split = point.split(",");
            if(split.length == 3)
                pts.add(new Point2D.Double(Double.parseDouble(split[1]), Double.parseDouble(split[2])));

        }

        long t1 = System.currentTimeMillis();
        boolean b = Geos.IsPtInPoly(new Point2D.Double(114.3004636757965, 30.54637909809649), pts);
        System.out.println(b + " -- " + (System.currentTimeMillis() - t1));

    }

}
