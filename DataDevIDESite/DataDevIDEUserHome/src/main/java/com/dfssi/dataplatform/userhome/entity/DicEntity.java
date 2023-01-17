package com.dfssi.dataplatform.userhome.entity;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 字典主表
 */
@Data
public class DicEntity implements Serializable {
   @Min(1)
   private String id;
   @Size(min=1,max=50)
   @NotBlank(message = "数据类型不能为空")
   private String dicType;
   @Size(min=1,max=50)
   @NotBlank(message = "名称不能为空")
   private String name;
   @Size(min=1,max=100)
   @NotBlank(message = "描述不能为空")
   private String description;
   @Min(0)
   @Max(1)
   @NotBlank(message = "isValid不能为空")
   private String isValid;
   @Min(1)
   @Max(9999)
   @NotBlank(message = "排序不能为空")
   private String orderNum;
}
