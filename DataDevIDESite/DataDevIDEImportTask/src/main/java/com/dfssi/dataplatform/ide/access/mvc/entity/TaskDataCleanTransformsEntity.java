package com.dfssi.dataplatform.ide.access.mvc.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data
public class TaskDataCleanTransformsEntity implements Serializable {

   private String type;

   private String cleanTransId;

   private List<String> columns;

   private Map<String,Map<String, HandleTypeEntity>> cleanTranRule;

   private Map<String,String> config;

}
