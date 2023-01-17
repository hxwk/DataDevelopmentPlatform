package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskAccessPluginsEntity extends BaseVO {

    private String id;

    private String stepTypeName;

    private String stepTypeCode;

    private String stepTypePcode;

    private String description;

    private List children = new ArrayList();

    private String showName;

    private String commonType;


}
