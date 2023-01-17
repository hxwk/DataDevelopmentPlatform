package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

@Data
public class TaskAccessInfoEntity extends BaseVO {

    private String id;//主键id

    @Length(max = 25, message = "任务名称最多输入25个字符")
    private String taskName;//任务名称

    @Length(max = 255, message = "描述最多输入255个字符")
    private String taskDescription;//描述

    private int approveStatus;

    private String status;//运行状态   0 未执行  1 执行中  2 停止

    private String startCreateTime;

    private String endCreateTime;

    private String taskStartTime;

    private String taskEndTime;

    private Integer taskFrequency;

    @Max(value = 999999999, message = "为数字类型，任务间隔最多输入9位数")
    private Integer taskInterval;

    private String field;//排序字段

    private String orderType;//排序类型

    private String clientIds;//客户端ID字符串


}
