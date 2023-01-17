package com.dfssi.dataplatform.userhome.Entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import java.util.Date;

@Data
public class UserEntity extends BaseVO {
    private String uRoleName;

    private String id;

    private String name;

    private String uName;

    private String uPsword;

    private Date createTime;

    private String isDelete;

    private String uType;

    private String uRole;

    private String telephone;

    private String site;

    private String menuData;

}