package com.dfssi.dataplatform.ide.dataresource.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 *dv_dataresource表对应实体DataResourceEntity
 */
@Data
public class  DataResourceEntity  extends BaseVO {
    @Size(max = 50)
    private String dataresourceId;

    @Size(min=1,max = 30)
    @NotBlank(message = "名称不能为空")
    private String dataresourceName;

    @Size(min=1,max=100)
    @NotBlank(message = "描述不能为空")
    private String dataresourceDesc;

   @Size(max=20)
    @NotBlank(message = "类型不能为空")
    private String dataresourceType;

    @Min(0)
    @Max(1)
    private String isShared;

    private String startCreateTime;

    private String endCreateTime;

    private String selectresourceType;

    @Valid
    private List<DataResourceSubEntity> dataResourceSubEntity;

    private List<DataResourceTableColumnEntity> dataResourceColumnEntity;
}
