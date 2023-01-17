package com.dfssi.dataplatform.entity.count;

import com.dfssi.dataplatform.utils.NumberUtil;

/**
 * Description:
 *    仪表盘里程 以及 计算的gps里程
 * @author LiXiaoCong
 * @version 2018/4/26 15:11
 */
public class MileDetail {

    private double totalMile;
    private double totalGpsMile;

    public double getTotalMile() {
        return NumberUtil.rounding(totalMile, 1);
    }

    public void setTotalMile(double totalMile) {
        this.totalMile = totalMile;
    }

    public double getTotalGpsMile() {
        return NumberUtil.rounding(totalGpsMile, 1);
    }

    public void setTotalGpsMile(double totalGpsMile) {
        this.totalGpsMile = totalGpsMile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MileDetail{");
        sb.append("totalMile=").append(totalMile);
        sb.append(", totalGpsMile=").append(totalGpsMile);
        sb.append('}');
        return sb.toString();
    }
}
