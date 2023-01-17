package com.dfssi.dataplatform.analysis.route.order;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/26 17:17
 */
public class OrderRecord implements Serializable {

    private String orderNo;
    private String routeId;
    private int status;
    private long starttime;


    private long endtime;
    //起点栅栏
    private List<Point2D.Double> startFence;
    //终点栅栏
    private List<Point2D.Double> endFence;



    public OrderRecord() { }

    public OrderRecord(String orderNo, String routeId, int status, long starttime) {
        this.orderNo = orderNo;
        this.routeId = routeId;
        this.status = status;
        this.starttime = starttime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public List<Point2D.Double> getStartFence() {
        return startFence;
    }

    public void setStartFence(List<Point2D.Double> startFence) {
        this.startFence = startFence;
    }

    public List<Point2D.Double> getEndFence() {
        return endFence;
    }

    public void setEndFence(List<Point2D.Double> endFence) {
        this.endFence = endFence;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OrderRecord{");
        sb.append("orderNo='").append(orderNo).append('\'');
        sb.append(", routeId='").append(routeId).append('\'');
        sb.append(", status=").append(status);
        sb.append(", starttime=").append(starttime);
        sb.append(", endtime=").append(endtime);
        sb.append('}');
        return sb.toString();
    }
}
