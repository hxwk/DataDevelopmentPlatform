package com.dfssi.dataplatform.ide.dataresource.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

/**
 * dv_dataresource对应实体
 * @author hongs
 */
@Data
public class DataResourceConfEntity extends BaseVO {

    private String id;
    private String dataresourceId;
    private String dataresourceName;
    private String dataresourceDesc;
    private String dataresourceType;
    private String isDeleted;
    private String createDate;
    private String createUser;
    private String updateDate;
    private String updateUser;
    private String showName;
}
