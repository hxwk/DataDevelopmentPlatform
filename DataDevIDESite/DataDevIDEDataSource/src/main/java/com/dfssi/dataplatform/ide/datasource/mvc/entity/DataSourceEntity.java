package com.dfssi.dataplatform.ide.datasource.mvc.entity;


import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * dv_datasource表对应实体DataSourceEntity
 */
@Data
public class DataSourceEntity extends BaseVO {
    @Size(max = 50)
    private String datasourceId;

    @Size(min=1,max = 30)
    @NotBlank(message = "名称不能为空")
    private String datasourceName;

    @Size(min=1,max=100)
    @NotBlank(message = "描述不能为空")
    private String datasourceDesc;

    @Size(max = 20)
    @NotBlank(message = "类型不能为空")
    private String datasourceType;
    private String startCreateTime;
    private String endCreateTime;

    @Valid
    private List<DataSourceSubEntity> dataSourceSubEntity;
}
