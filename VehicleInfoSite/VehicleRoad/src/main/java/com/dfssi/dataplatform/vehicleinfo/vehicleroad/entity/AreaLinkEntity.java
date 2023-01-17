package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import joptsimple.internal.Strings;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/9 20:26
 */
@Setter
@ToString
@ApiModel(value = "AreaLinkEntity", description = "区域与VID绑定实体")
public class AreaLinkEntity {

    @ApiModelProperty(name = "id", value = "id", dataType = "string")
    private String id = UUID.randomUUID().toString().replaceAll("-", "");

    @ApiModelProperty(name = "vid", value = "车辆vid", dataType = "string", required = true)
    @NotBlank(message = "车辆vid不能为空!")
    private String vid;

    @ApiModelProperty(name = "areaId", value = "区域id", dataType = "string",  required = true)
    @NotBlank(message = "区域id不能为空!")
    private String areaId;

    @ApiModelProperty(name = "areaName", value = "区域名称", dataType = "string",  required = true)
    @NotBlank(message = "区域名称不能为空!")
    private String areaName;

    @ApiModelProperty(name = "areaAlarmType", value = "区域停车报警类型", dataType = "string")
    private String areaAlarmType;

    @ApiModelProperty(name = "alarmDesc", value = "告警描述", dataType = "string")
    private String alarmDesc;

    @ApiModelProperty(name = "areaType",  value = "区域类型, 0代表违停区域，1代表偏航区域", dataType = "int", required = true, allowableValues = "0,1")
    @Min(value = 0, message = "区域类型不能为小于0的值")
    @Max(value = 1, message = "区域类型不能为大于1的值")
    private int areaType;

    @ApiModelProperty(name = "startTime", value = "报警开始时间", dataType = "long", required = true)
    @Min(value = 1514736000000L, message = "报警开始时间过小")
    private long startTime;

    @ApiModelProperty(name = "stopTime", value = "报警结束时间", dataType = "long",  required = true)
    @Min(value = 1514736000000L, message = "报警结束时间过小")
    private long stopTime;

    @ApiModelProperty(name = "stayTime", value = "报警持续时长", dataType = "long")
    @Min(value = 0, message = "报警持续时长不能为小于0")
    private long stayTime;

    @ApiModelProperty(name = "createTime", value = "绑定创建时间戳", dataType = "long")
    private long createTime = System.currentTimeMillis();

    public String getId() {
        if(Strings.isNullOrEmpty(id)){
            id = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return id;
    }

    public String getVid() {
        return vid;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getAreaAlarmType() {

        if(areaAlarmType == null){
            areaAlarmType = "";
        }

        return areaAlarmType;
    }

    public String getAlarmDesc() {
        if(alarmDesc == null){
            alarmDesc = "";
        }
        return alarmDesc;
    }

    public int getAreaType() {
        return areaType;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public long getStayTime() {
        return stayTime;
    }

    public long getCreateTime() {
        if(createTime <= 0){
            createTime = System.currentTimeMillis();
        }
        return createTime;
    }
}
