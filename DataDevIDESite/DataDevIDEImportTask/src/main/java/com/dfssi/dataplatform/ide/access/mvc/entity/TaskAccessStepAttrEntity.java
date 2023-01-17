package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import com.dfssi.dataplatform.ide.access.mvc.utils.JacksonUtils;
import com.google.gson.JsonObject;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskAccessStepAttrEntity extends BaseVO {

    private String id;

    private String stepId;

    private String attrItem;

    private String attrValue;

    private String attrDesc;

    private String taskId;



    public static List<TaskAccessStepAttrEntity> buildFromJson(List<JsonObject> objects){
        List<TaskAccessStepAttrEntity> listbean=new ArrayList<TaskAccessStepAttrEntity>();
        for(JsonObject object:objects){
            TaskAccessStepAttrEntity taskaccessstepattrentity = buildFromJson(object);
            listbean.add(taskaccessstepattrentity);
        }
        return listbean;
    }

    private static TaskAccessStepAttrEntity buildFromJson(JsonObject modelJsonObject){

        TaskAccessStepAttrEntity taskaccessstepattrentity = new TaskAccessStepAttrEntity();

        taskaccessstepattrentity.setId(JacksonUtils.getAsString(modelJsonObject, "id"));
        taskaccessstepattrentity.setStepId(JacksonUtils.getAsString(modelJsonObject, "stepId"));
        taskaccessstepattrentity.setAttrItem(JacksonUtils.getAsString(modelJsonObject, "attrItem"));
        taskaccessstepattrentity.setAttrValue(JacksonUtils.getAsString(modelJsonObject, "attrValue"));
        taskaccessstepattrentity.setAttrDesc(JacksonUtils.getAsString(modelJsonObject, "attrDesc"));
        taskaccessstepattrentity.setIsDeleted("0");
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=df.format(day);
        taskaccessstepattrentity.setCreateDate(s);
        taskaccessstepattrentity.setCreateUser("hs");
        taskaccessstepattrentity.setUpdateDate(s);
        taskaccessstepattrentity.setUpdateUser("hs");
        taskaccessstepattrentity.setTaskId(JacksonUtils.getAsString(modelJsonObject, "taskId"));
        return taskaccessstepattrentity;
    }
}
