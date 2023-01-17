package com.dfssi.dataplatform.ide.access.mvc.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by hongs on 2018/5/29.
 */
@Data
public class TaskDataDestinationsEntity implements Serializable {

    private String destId;

    private String type;

    //private Map<String,List<Integer>> mapping;

    private Map<String,String> config;


}
