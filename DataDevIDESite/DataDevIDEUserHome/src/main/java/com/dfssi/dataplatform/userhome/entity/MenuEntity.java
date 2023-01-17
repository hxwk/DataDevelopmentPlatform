package com.dfssi.dataplatform.userhome.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MenuEntity extends BaseVO {
    @Size(max = 50)
    private String id;

    @Size(max=20)
    @NotBlank(message = "菜单名不能为空")
    private String menuName;

    @Size(min=1,max = 100)
    @NotBlank(message = "排序不能为空")
    private String orderNum;

    @Size(min=1,max=100)
    @NotBlank(message = "备注不能为空")
    private String remark;

    @Size(max=50)
    private String pid;

    private String parentMenuName;

    @Min(0)
    @Min(1)
    private String valid;

    @Size(max=50)
    @NotBlank(message = "地址不能为空")
    private String accessUrl;

    private String system;
}