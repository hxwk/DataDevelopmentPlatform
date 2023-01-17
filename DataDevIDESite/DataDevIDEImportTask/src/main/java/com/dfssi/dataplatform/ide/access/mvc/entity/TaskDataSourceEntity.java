package com.dfssi.dataplatform.ide.access.mvc.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 启动任务报文拼接实体类
 */
@Data
public class TaskDataSourceEntity implements Serializable {

    private String srcId;

    private String type;

    private Map<String,String> config;

}
