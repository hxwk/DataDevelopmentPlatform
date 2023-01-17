package com.dfssi.dataplatform.userhome.Entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class MenuEntity extends BaseVO {
    private String id;

    private String menuName;

    private String orderNum;

    private String remark;

    private String parentMenu;

    private String parentMenuName;

    private String valid;

    private String accessUrl;

}