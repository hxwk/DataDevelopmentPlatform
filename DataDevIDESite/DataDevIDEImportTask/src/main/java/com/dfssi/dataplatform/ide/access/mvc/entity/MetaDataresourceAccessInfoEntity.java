package com.dfssi.dataplatform.ide.access.mvc.entity;


import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class MetaDataresourceAccessInfoEntity extends BaseVO {
    private String drsSubId;
    private String dataresourceId;
    private String parameterName;
    private String parameterValue;
    private String parameterDesc;

}
