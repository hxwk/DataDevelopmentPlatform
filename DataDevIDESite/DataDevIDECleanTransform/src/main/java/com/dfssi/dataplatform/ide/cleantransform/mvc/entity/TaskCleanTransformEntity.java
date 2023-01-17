package com.dfssi.dataplatform.ide.cleantransform.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created by hongs on 2018/6/1.
 * 字段映射主表
 */
@Data
public class TaskCleanTransformEntity extends BaseVO {
    //主键id
    String id;

    @Length(max = 25, message = "最多输入25个字符")
    String cleanName;//映射名称

    @Length(max = 255, message = "最多输入255个字符")
    String descr;//映射描述
    //排序字段
    String field;
    //排序类型
    String orderType;

}
