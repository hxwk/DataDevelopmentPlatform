package com.dfssi.dataplatform.ide.datasource.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * dv_datasource_sub表对应实体DataSourceSubEntity
 */
@Data
public class DataSourceSubEntity extends BaseVO {

    @Size(max = 50)
    private String dsSubId;

    @Size(max = 50)
    private String datasourceId;

    @Size(min=1,max=50)
    @NotBlank(message = "参数名不能为空")
    private String parameterName;

    @Size(min=1,max=200)
    @NotBlank(message = "参数值不能为空")
    private String parameterValue;

    @Size(min=1,max=100)
    @NotBlank(message = "参数描述不能为空")
    private String parameterDesc;

}
