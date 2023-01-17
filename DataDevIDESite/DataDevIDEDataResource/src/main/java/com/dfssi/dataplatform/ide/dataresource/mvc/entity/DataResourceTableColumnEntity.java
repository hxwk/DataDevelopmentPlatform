package com.dfssi.dataplatform.ide.dataresource.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

/**
 * mata_dataresource_tablecolumn_con_f对应实体
 */
@Data
public class DataResourceTableColumnEntity extends BaseVO {
    private String dataResourceColumnId;
    private String dataResourceColumnName;
    private String dataResourceId;
    private String dataResourceColumnType;
    private String dataResourceColumnDescription;
    private String dataResourceColumnPos;

}
