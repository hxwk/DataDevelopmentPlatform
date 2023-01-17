package com.dfssi.dataplatform.abs.check;

import lombok.Data;

/**
 * Description:
 *    检测规则
 * @author LiXiaoCong
 * @version 2018/9/20 12:16
 */
@Data
public class ABSCheckRule {

    private int vehicleType;
    private int vehicleStatus;

    //制动初速度限制
    private double minStartBreakSpeed;

    //最小减速度
    private double minBreakLessSpeed;

    //最大制动距离
    private double maxBreakDistance;


    public ABSCheckRule(int vehicleType,
                        int vehicleStatus,
                        double minStartBreakSpeed,
                        double minBreakLessSpeed,
                        double maxBreakDistance) {
        this.vehicleType = vehicleType;
        this.vehicleStatus = vehicleStatus;
        this.minStartBreakSpeed = minStartBreakSpeed;
        this.minBreakLessSpeed = minBreakLessSpeed;
        this.maxBreakDistance = maxBreakDistance;
    }
}
