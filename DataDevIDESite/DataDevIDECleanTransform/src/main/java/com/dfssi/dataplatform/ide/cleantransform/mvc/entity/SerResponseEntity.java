package com.dfssi.dataplatform.ide.cleantransform.mvc.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author
 * @date 2018/9/19
 * @description service返回controller的实体
 */
@Data
public class SerResponseEntity implements Serializable {
    private String statusCode;

    private String message;

    private Boolean flag;

    private Object data;

    private String total;

}