package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

@Data
public class TaskAccessLinkInfoEntity extends BaseVO {

    private String id;

    private String taskId;

    private String stepFromId;

    private String stepToId;

    private String stepFromPos;

    private String stepToPos;


}
