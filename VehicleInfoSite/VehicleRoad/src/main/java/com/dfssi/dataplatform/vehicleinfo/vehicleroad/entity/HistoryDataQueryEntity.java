package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/30 14:01
 */
@Setter
@Getter
@ToString
@ApiModel(value = "HistoryDataQueryEntity", description = "历史数据查询参数实体")
public class HistoryDataQueryEntity implements Serializable {

    @ApiModelProperty(name = "vid", value = "车辆vid", dataType = "Array[String]")
    private String[] vid;

    @ApiModelProperty(name = "startTime", value = "开始日期时间戳，单位：毫秒", dataType = "long", required = true, position = 1)
    private Long startTime;

    @ApiModelProperty(name = "stopTime", value = "开始日期时间戳，单位：毫秒", dataType = "long", required = true, position = 2)
    private Long endTime;

    @ApiModelProperty(name = "columns", value = "返回字段", dataType = "Array[String]", position = 3)
    private String[] columns;

    @ApiModelProperty(name = "pageNow", value = "查询页码", dataType = "int", example ="1", position = 4)
    private int pageNow = 1;
    @ApiModelProperty(name = "pageSize", value = "页面大小", dataType = "int", example ="10", position = 5)
    private int pageSize = 10;


    @ApiModelProperty(name = "export", value = "是否导出, 默认false", dataType = "boolean", example ="false", position = 6)
    private boolean export = false;
    @ApiModelProperty(name = "exportType", value = "导出文件格式, 目前支持：excel,txt", dataType = "string", allowableValues = "excel,txt", example ="excel", position = 7)
    private String exportType = "excel";
    @ApiModelProperty(name = "maxRowPerFile", value = "每个导出文件的最大记录数，默认：10000", dataType = "int", example ="10000", position = 8)
    private int maxRowPerFile = 10000;

}