package com.dfssi.dataplatform.ide.access.mvc.entity;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
@Data
public class TaskModelEntity implements Serializable {
    @Valid
    private TaskAccessInfoEntity taskAccessInfoEntity;
    /**
     * 批量插入接入步骤信息
     */
    private List<TaskAccessStepInfoEntity> taskAccessStepInfoEntities;

    private List<TaskAccessLinkInfoEntity> taskAccessLinkInfoEntities;

}
