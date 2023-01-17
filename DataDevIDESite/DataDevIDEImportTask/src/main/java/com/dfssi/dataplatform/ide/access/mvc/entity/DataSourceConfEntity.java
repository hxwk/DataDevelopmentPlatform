package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class DataSourceConfEntity extends BaseVO {

    private String id;
    private String datasourceId;//主键id
    private String datasourceName;
    private String datasourceDescription;
    private String datasourceType;
    private int displayOrder;
    private String showName;
    private String commonType;

}
