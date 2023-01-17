package com.dfssi.dataplatform.userhome.Entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import lombok.Data;

import java.util.List;

@Data
public class DicEntity extends BaseVO {
   private String id;
   private String dicType;
   private String name;
   private String description;
   private String orderNum;
}
