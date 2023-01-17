package com.dfssi.dataplatform.ide.cleantransform.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * Created by hongs on 2018/5/10.
 * 字段映射子表
 */
 @Data
public class TaskCleanTransformMapperEntity extends BaseVO {
    //主键id
    String id;
    //映射的外键id
    String mappingId;

    @NotEmpty(message = "字段名称不能为空")
    @Length(max = 25, message = "最多输入25个字符")
    String field;//字段

    @NotEmpty(message = "规则名称不能为空")
    @Length(max = 25, message = "最多输入25个字符")
    String ruleName;//规则名称

    @NotEmpty(message = "不能为空")
    String param;//参数

    @Length(max = 255, message = "描述最多输入255个字符")
    String descr;//描述


}
