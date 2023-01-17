package com.dfssi.dataplatform.ide.access.mvc.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 启动任务拼接报文所需数据源实体类
 */
@Data
public class TaskStartEntity implements Serializable {

    private String taskId;
    private String clientId;
    private String channelType;
    private String taskType;
    private String taskAction;
    private TaskDataSourceEntity taskDataSource;
    private List<TaskDataCleanTransformsEntity> taskDataCleanTransforms;
    private List<TaskDataDestinationsEntity> taskDataDestinations;

}
