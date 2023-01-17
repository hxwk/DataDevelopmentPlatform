package com.dfssi.dataplatform.userhome.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RoleEntity extends BaseVO {
    @Size(max = 50)
    private String id;

    @Size(min = 1,max = 20)
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    @Size(min = 1,max = 100)
    @NotBlank(message = "备注不能为空")
    private String remark;

    @Min(0)
    @Max(1)
    private String valid;

    private String createUser;

    private String createTime;

    private String updateUser;

    private String updateTime;

}