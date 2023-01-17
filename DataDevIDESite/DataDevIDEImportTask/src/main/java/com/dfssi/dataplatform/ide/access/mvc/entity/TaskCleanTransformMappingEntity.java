package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

/**
 * Created by hongs on 2018/6/1.
 */
@Data
public class TaskCleanTransformMappingEntity extends BaseVO {
    String id;
    //清洗名称
    String cleanName;
    //描述
    String descr;
    //显示名称
    String showName;
    //附加一个公共字段type
    String commonType;

}
