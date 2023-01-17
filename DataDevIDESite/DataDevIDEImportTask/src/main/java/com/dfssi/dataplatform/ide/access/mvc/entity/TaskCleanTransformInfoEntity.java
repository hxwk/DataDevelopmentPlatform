package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

/**
 * Created by hongs on 2018/5/10.
 */
@Data
public class TaskCleanTransformInfoEntity extends BaseVO {
    //id
    String id;
    //映射ID
    String mappingId;
    //字段
    String field;
    //规则名称
    String ruleName;
    //参数
    String param;
    //描述
    String descr;

}
