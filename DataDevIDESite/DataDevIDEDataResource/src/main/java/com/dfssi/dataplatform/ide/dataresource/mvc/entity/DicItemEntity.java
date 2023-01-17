package com.dfssi.dataplatform.ide.dataresource.mvc.entity;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 字典子表
 */
@Data
public class DicItemEntity implements Serializable {
    @Min(1)
    private String id;
    @Size(min=1,max=50)
    @NotBlank(message = "类型不能为空")
    private String dicType;
    @Size(min=1,max=50)
    @NotBlank(message = "名称不能为空")
    private String itemName;
    @Size(min = 1,max = 50)
    @NotBlank(message = "值不能为空")
    private String value;
    @Min(0)
    @Max(1)
    @NotBlank(message = "是否有效不能为空")
    private String isValid;
}
