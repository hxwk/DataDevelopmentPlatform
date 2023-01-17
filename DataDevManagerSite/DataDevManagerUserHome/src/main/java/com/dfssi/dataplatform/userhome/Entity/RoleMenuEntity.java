package com.dfssi.dataplatform.userhome.Entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import java.util.Date;
@Data
public class RoleMenuEntity extends BaseVO {
    private String roleId;

    private String menuId;

    private Date relationDate;

    private String buttonIds;

}