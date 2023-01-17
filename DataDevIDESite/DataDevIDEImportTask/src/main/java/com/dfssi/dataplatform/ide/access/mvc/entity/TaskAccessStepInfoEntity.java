package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import java.util.List;

@Data
public class TaskAccessStepInfoEntity extends BaseVO {

    private String id;

    private String taskId;

    private String pluginId;

    private String stepName;

    private Integer guiX;

    private Integer guiY;

    private String cleantransformId;

    //添加左边节点ID
    private String leftnodeId;

    private List<TaskAccessStepAttrEntity> taskAccessStepAttrEntities;

}
