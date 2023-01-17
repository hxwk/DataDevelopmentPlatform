package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class DataResourceConfEntity extends BaseVO {

    private String id;
    private String dataresourceId;
    private String dataresourceName;
    private String dataresourceDesc;
    private String dataresourceType;
    private String showName;
    private String commonType;
}
