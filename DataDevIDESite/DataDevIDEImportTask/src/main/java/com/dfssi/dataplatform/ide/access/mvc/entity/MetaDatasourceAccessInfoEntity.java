package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class MetaDatasourceAccessInfoEntity extends BaseVO {
    private String dsSubId;
    private String datasourceId;
    private String parameterName;
    private String parameterValue;
    private String parameterDesc;

}
