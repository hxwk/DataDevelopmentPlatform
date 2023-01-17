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
@ApiModel(value = "AreaAlarmEntity", description = "区域实体")
public class AreaAlarmEntity {

    @ApiModelProperty(name = "id", value = "区域Id", dataType = "string")
    private String id = UUID.randomUUID().toString().replaceAll("-", "");

    @ApiModelProperty(name = "name", value = "区域名称", dataType = "string",  required = true)
    @NotBlank(message = "区域名称不能为空!")
    private String name;

    @ApiModelProperty(name = "type", value = "区域类型, 0代表违停区域，1代表偏航区域", dataType = "int", required = true, allowableValues = "0,1")
    @Min(value = 0, message = "区域类型不能为小于0的值")
    @Max(value = 1, message = "区域类型不能为大于1的值")
    private int type;

    @ApiModelProperty(name = "locations", value = "区域经纬度范围", dataType = "string",
            required = true, example = "longitude_1:latitude_1;...;longitude_n:latitude_n")
    @NotBlank(message = "区域经纬度范围不能为空!")
    private String locations;

    @ApiModelProperty(name = "createTime", value = "区域创建时间戳", dataType = "long")
    private long createTime = System.currentTimeMillis();

    public String getId() {
        if(Strings.isNullOrEmpty(id)){
            id = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getLocations() {
        return locations;
    }

    public long getCreateTime() {
        if(createTime <= 0){
            createTime = System.currentTimeMillis();
        }
        return createTime;
    }
}
