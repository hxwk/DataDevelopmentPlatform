package com.dfssi.dataplatform.userhome.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserEntity extends BaseVO {
    private String uRoleName;

    @Size(max = 50)
    private String id;

    @Size(min=1,max = 20)
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Size(min=1,max=20)
    @NotBlank(message = "用户名不能为空")
    private String uName;

    @Size(min=6,max = 18)
    @NotBlank(message = "密码不能为空")
    private String uPsword;

    private Date createTime;

    @Min(0)
    @Max(1)
    private String isDelete;

    @Size(max=1000)
    @NotBlank(message = "用户角色不能为空")
    private String uRole;

    private String menuData;

}