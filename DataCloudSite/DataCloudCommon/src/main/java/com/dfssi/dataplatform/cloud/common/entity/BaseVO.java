package com.dfssi.dataplatform.cloud.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by cxq on 2018/1/3.
 */
@Data
public class BaseVO implements Serializable {
    private String isDeleted;
    private String isValid;
    private String createDate;
    private String createUser;
    private String updateDate;
    private String updateUser;
   
}
