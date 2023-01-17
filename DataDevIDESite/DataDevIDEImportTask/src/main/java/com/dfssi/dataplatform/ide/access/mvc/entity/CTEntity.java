package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.ide.access.mvc.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hongs on 2018/5/10.
 */
@Data
public class CTEntity implements Serializable {
    //数字转换
    @JsonIgnore
    private static final String NUMBERHANDLE = "numberhandle";

    //文本转换
    @JsonIgnore
    private static final String SEARCHREPLACE = "searchreplace";

    //脱敏处理
    @JsonIgnore
    private static final String MASKHANDLER = "maskhandler";

    private String id;
   //字段
    private String field;
   //规则
    private String ruleName;
   //系数
    private String factor;
    //偏移量
    private String offset;
    //模式
    private String pattern;
    //替换字符串
   private String replaceStr;
    //起始
    private String start;
    //结束
    private String end;
    //插入字符串
    private String insertStr;

    public static List<TaskCleanTransformInfoEntity> buildFromJson(List<String> listStr){
        List<TaskCleanTransformInfoEntity> listbean=new ArrayList<TaskCleanTransformInfoEntity>();
        for(String str:listStr){
            JsonObject ctmodelJsonObject = new JsonParser().parse(str).getAsJsonObject();
            CTEntity ctmodel = buildFromJson(ctmodelJsonObject);
            if (ctmodel.getRuleName().equals(NUMBERHANDLE)){
                //数字转换

                TaskCleanTransformInfoEntity taskcleantransforminfoentity=BuildNumCleanTransform(ctmodel);
                listbean.add(taskcleantransforminfoentity);
            }
            else if(ctmodel.getRuleName().equals(SEARCHREPLACE)){
               //文本转换

                TaskCleanTransformInfoEntity taskcleantransforminfoentity=BuildTextCleanTransform(ctmodel);
                listbean.add(taskcleantransforminfoentity);
            }
            else if(ctmodel.getRuleName().equals(MASKHANDLER)){
                //脱敏

                TaskCleanTransformInfoEntity taskcleantransforminfoentity=BuildMaskCleanTransform(ctmodel);
                listbean.add(taskcleantransforminfoentity);
            }
        }
        return listbean;
    }

    public static TaskCleanTransformInfoEntity buildFromJson(String str){
            JsonObject ctmodelJsonObject = new JsonParser().parse(str).getAsJsonObject();
        TaskCleanTransformInfoEntity taskcleantransforminfoentity=new TaskCleanTransformInfoEntity();
            if ("numberhandle".equals(NUMBERHANDLE)){
                //数字转换
                CTEntity ctmodel = buildFromJson(ctmodelJsonObject);

                 taskcleantransforminfoentity=BuildNumCleanTransform(ctmodel);
            }else if(!JacksonUtils.isBlank(ctmodelJsonObject, SEARCHREPLACE)){
                //文本转换
                CTEntity ctmodel = buildFromJson(ctmodelJsonObject);

                 taskcleantransforminfoentity=BuildTextCleanTransform(ctmodel);
            }else if(!JacksonUtils.isBlank(ctmodelJsonObject, MASKHANDLER)){
                //脱敏
                CTEntity ctmodel = buildFromJson(ctmodelJsonObject);

                 taskcleantransforminfoentity=BuildMaskCleanTransform(ctmodel);
            }

        return taskcleantransforminfoentity;
    }

    private static CTEntity buildFromJson(JsonObject modelJsonObject){
        CTEntity ctmodel = new CTEntity();
        ctmodel.setId(JacksonUtils.getAsString(modelJsonObject, "id"));
        ctmodel.setField(JacksonUtils.getAsString(modelJsonObject, "field"));
        ctmodel.setRuleName(JacksonUtils.getAsString(modelJsonObject, "ruleName"));
        ctmodel.setFactor(JacksonUtils.getAsString(modelJsonObject, "factor"));
        ctmodel.setOffset(JacksonUtils.getAsString(modelJsonObject, "offset"));
        ctmodel.setPattern(JacksonUtils.getAsString(modelJsonObject, "pattern"));
        ctmodel.setReplaceStr(JacksonUtils.getAsString(modelJsonObject, "replaceStr"));
        ctmodel.setStart(JacksonUtils.getAsString(modelJsonObject, "start"));
        ctmodel.setEnd(JacksonUtils.getAsString(modelJsonObject, "end"));
        ctmodel.setInsertStr(JacksonUtils.getAsString(modelJsonObject, "insertStr"));

        return ctmodel;
    }

//数字转换bean
    private static TaskCleanTransformInfoEntity BuildNumCleanTransform(CTEntity ctmodel){
        JsonObject numParam=new JsonObject();
        numParam.addProperty("factor",ctmodel.getFactor());
        numParam.addProperty("offset",ctmodel.getOffset());
        TaskCleanTransformInfoEntity taskcleantransforminfoentity=new TaskCleanTransformInfoEntity();
        taskcleantransforminfoentity.setId(ctmodel.getId());
        taskcleantransforminfoentity.setField(ctmodel.getField());
        taskcleantransforminfoentity.setRuleName(ctmodel.getRuleName());
        taskcleantransforminfoentity.setParam(numParam.toString());
        taskcleantransforminfoentity.setDescr("数字转换");
        taskcleantransforminfoentity.setIsDeleted("0");
        Date d = new Date();
        String s=d.toString();
        taskcleantransforminfoentity.setCreateDate(s);
        taskcleantransforminfoentity.setCreateUser("hs");
        taskcleantransforminfoentity.setUpdateDate(s);
        taskcleantransforminfoentity.setUpdateUser("hs");
           return  taskcleantransforminfoentity;
    }

    //文本转换bean
    private static TaskCleanTransformInfoEntity BuildTextCleanTransform(CTEntity ctmodel){
        JsonObject textParam=new JsonObject();
        textParam.addProperty("pattern",ctmodel.getPattern());
        textParam.addProperty("replaceStr",ctmodel.getReplaceStr());
        TaskCleanTransformInfoEntity taskcleantransforminfoentity=new TaskCleanTransformInfoEntity();
        taskcleantransforminfoentity.setId(ctmodel.getId());
        taskcleantransforminfoentity.setField(ctmodel.getField());
        taskcleantransforminfoentity.setRuleName(ctmodel.getRuleName());
        taskcleantransforminfoentity.setParam(textParam.toString());
        taskcleantransforminfoentity.setDescr("文本转换");
        taskcleantransforminfoentity.setIsDeleted("0");
        Date d = new Date();
        String s=d.toString();
        taskcleantransforminfoentity.setCreateDate(s);
        taskcleantransforminfoentity.setCreateUser("hs");
        taskcleantransforminfoentity.setUpdateDate(s);
        taskcleantransforminfoentity.setUpdateUser("hs");
        return  taskcleantransforminfoentity;
    }

//脱敏转换bean
    private static TaskCleanTransformInfoEntity BuildMaskCleanTransform(CTEntity ctmodel){
        JsonObject maskParam=new JsonObject();
        maskParam.addProperty("start",ctmodel.getStart());
        maskParam.addProperty("end",ctmodel.getEnd());
        maskParam.addProperty("insertStr",ctmodel.getInsertStr());
        TaskCleanTransformInfoEntity taskcleantransforminfoentity=new TaskCleanTransformInfoEntity();
        taskcleantransforminfoentity.setId(ctmodel.getId());
        taskcleantransforminfoentity.setField(ctmodel.getField());
        taskcleantransforminfoentity.setRuleName(ctmodel.getRuleName());
        taskcleantransforminfoentity.setParam(maskParam.toString());
        taskcleantransforminfoentity.setDescr("数字转换");
        taskcleantransforminfoentity.setIsDeleted("0");
        Date d = new Date();
        String s=d.toString();
        taskcleantransforminfoentity.setCreateDate(s);
        taskcleantransforminfoentity.setCreateUser("hs");
        taskcleantransforminfoentity.setUpdateDate(s);
        taskcleantransforminfoentity.setUpdateUser("hs");
        return  taskcleantransforminfoentity;
    }
}
