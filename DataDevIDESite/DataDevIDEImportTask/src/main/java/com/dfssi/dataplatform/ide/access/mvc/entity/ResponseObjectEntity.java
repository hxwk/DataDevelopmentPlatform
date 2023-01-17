package com.dfssi.dataplatform.ide.access.mvc.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseObjectEntity implements Serializable {
    private String statusCode;
    private String message;
    private Boolean flag;
    private Object data;
    private String total;

}
