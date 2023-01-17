package com.dfssi.dataplatform.ide.datasource.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class DataSourceConfEntity extends BaseVO {

    private String id;
    private String datasourceId;
    private String datasourceName;
    private String datasourceDescription;
    private String datasourceType;
    private String showName;
}
