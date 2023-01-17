package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 历史数据查询
 * Created by yanghs on 2018/9/12.
 */

@Setter
@Getter
@ToString
@ApiModel(value = "DataStatisticsEntity", description = "")
public class DataStatisticsEntity implements Serializable{

    @ApiModelProperty(name = "vid", value = "车辆vid", dataType = "string")
    private String vid;

    @ApiModelProperty(name = "startTime", value = "开始日期时间戳，单位：毫秒", dataType = "long", required = true)
    private Long startTime;

    @ApiModelProperty(name = "stopTime", value = "开始日期时间戳，单位：毫秒", dataType = "long", required = true)
    private Long endTime;

    @ApiModelProperty(name = "columns", value = "返回字段", dataType = "Array[String]")
    private String[] columns;

    @ApiModelProperty(name = "pageNow", value = "查询页码", dataType = "int", required = true)
    private int pageNow = 1;
    @ApiModelProperty(name = "pageSize", value = "页面大小", dataType = "int", required = true)
    private int pageSize = 10;

}
