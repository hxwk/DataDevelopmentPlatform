package com.dfssi.dataplatform.ide.access.mvc.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class HandleTypeEntity implements Serializable {

    private String factor;
    private String offset;
    private String searchPattern;
    private String replaceString;
    private String maskStart;
    private String maskEnd;
    private String insertString;

}
