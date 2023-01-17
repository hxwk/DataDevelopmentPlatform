package com.dfssi.dataplatform.ide.cleantransform.mvc.entity;

import com.dfssi.dataplatform.cloud.common.entity.BaseVO;
import com.dfssi.dataplatform.ide.cleantransform.mvc.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hongs on 2018/5/10.
 */
@Data
public class CTEntity extends BaseVO {
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

//    public  String    getId() {
//        return id;
//    }
//    public void   setId(String id) {
//        this.id = id;
//    }
//    public String getField() {
//        return field;
//    }
//    public void   setField(String field) {
//        this.field = field;
//    }
//    public String getRuleName() {
//        return ruleName;
//    }
//    public void   setRuleName(String ruleName) {
//        this.ruleName = ruleName;
//    }
//    public String getFactor() {
//        return factor;
//    }
//    public void   setFactor(String factor) {
//        this.factor = factor;
//    }
//    public String getOffset() {
//        return offset;
//    }
//    public void   setOffset(String offset) {
//        this.offset = offset;
//    }
//    public String getPattern() {
//        return pattern;
//    }
//    public void   setPattern(String pattern) {
//        this.pattern = pattern;
//    }
//    public String getReplaceStr() {
//        return replaceStr;
//    }
//    public void   setReplaceStr(String replaceStr) {
//        this.replaceStr = replaceStr;
//    }
//    public String getStart() {
//        return start;
//    }
//    public void   setStart(String start) {
//        this.start = start;
//    }
//    public String getEnd() {
//        return end;
//    }
//    public void   setEnd(String end) {
//        this.end = end;
//    }
//    public String getInsertStr() {
//        return insertStr;
//    }
//    public void   setInsertStr(String insertStr) {
//        this.insertStr = insertStr;
//    }

    public static List<TaskCleanTransformMapperEntity> buildFromJson(List<String> listStr){
        List<TaskCleanTransformMapperEntity> listbean=new ArrayList<TaskCleanTransformMapperEntity>();
        for(String str:listStr){
            JsonObject ctmodelJsonObject = new JsonParser().parse(str).getAsJsonObject();
            CTEntity ctmodel = buildFromJson(ctmodelJsonObject);
            if (ctmodel.getRuleName().equals(NUMBERHANDLE)){
                //数字转换

                TaskCleanTransformMapperEntity taskcleantransforminfoentity=BuildNumCleanTransform(ctmodel);
                listbean.add(taskcleantransforminfoentity);
            }
            else if(ctmodel.getRuleName().equals(SEARCHREPLACE)){
               //文本转换

                TaskCleanTransformMapperEntity taskcleantransforminfoentity=BuildTextCleanTransform(ctmodel);
                listbean.add(taskcleantransforminfoentity);
            }
            else if(ctmodel.getRuleName().equals(MASKHANDLER)){
                //脱敏

                TaskCleanTransformMapperEntity taskcleantransforminfoentity=BuildMaskCleanTransform(ctmodel);
                listbean.add(taskcleantransforminfoentity);
            }
        }
        return listbean;
    }

    public static TaskCleanTransformMapperEntity buildFromJson(String str){
            JsonObject ctmodelJsonObject = new JsonParser().parse(str).getAsJsonObject();
        TaskCleanTransformMapperEntity taskcleantransforminfoentity=new TaskCleanTransformMapperEntity();
        CTEntity ctmodel = buildFromJson(ctmodelJsonObject);
        if (ctmodel.getRuleName().equals(NUMBERHANDLE)){
                //数字转换

                 taskcleantransforminfoentity=BuildNumCleanTransform(ctmodel);
            }else if(!JacksonUtils.isBlank(ctmodelJsonObject, SEARCHREPLACE)){
                //文本转换

                 taskcleantransforminfoentity=BuildTextCleanTransform(ctmodel);
            }else if(!JacksonUtils.isBlank(ctmodelJsonObject, MASKHANDLER)){
                //脱敏

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
    private static TaskCleanTransformMapperEntity BuildNumCleanTransform(CTEntity ctmodel){
        JsonObject numParam=new JsonObject();
        numParam.addProperty("factor",ctmodel.getFactor());
        numParam.addProperty("offset",ctmodel.getOffset());
        TaskCleanTransformMapperEntity taskcleantransforminfoentity=new TaskCleanTransformMapperEntity();
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
    private static TaskCleanTransformMapperEntity BuildTextCleanTransform(CTEntity ctmodel){
        JsonObject textParam=new JsonObject();
        textParam.addProperty("pattern",ctmodel.getPattern());
        textParam.addProperty("replaceStr",ctmodel.getReplaceStr());
        TaskCleanTransformMapperEntity taskcleantransforminfoentity=new TaskCleanTransformMapperEntity();
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
    private static TaskCleanTransformMapperEntity BuildMaskCleanTransform(CTEntity ctmodel){
        JsonObject maskParam=new JsonObject();
        maskParam.addProperty("start",ctmodel.getStart());
        maskParam.addProperty("end",ctmodel.getEnd());
        maskParam.addProperty("insertStr",ctmodel.getInsertStr());
        TaskCleanTransformMapperEntity taskcleantransforminfoentity=new TaskCleanTransformMapperEntity();
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
