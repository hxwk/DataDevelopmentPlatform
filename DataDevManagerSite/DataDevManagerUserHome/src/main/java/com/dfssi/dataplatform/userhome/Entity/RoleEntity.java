package com.dfssi.dataplatform.userhome.Entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import java.io.Serializable;
@Data
public class RoleEntity extends BaseVO {
    private String id;

    private String roleName;

    private String remark;

    private String valid;

    private String createUser;

    private String createTime;

    private String updateUser;

    private String updateTime;

}