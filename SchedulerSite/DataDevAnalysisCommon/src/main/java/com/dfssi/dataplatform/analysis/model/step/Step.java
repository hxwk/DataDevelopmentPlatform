package com.dfssi.dataplatform.analysis.model.step;

import com.dfssi.dataplatform.analysis.entity.AnalysisStepAttrEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisStepEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisStepTypeEntity;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.analysis.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class Step {

    private static volatile long index = 0;

    @JsonIgnore
    protected Model model;

    private String id;
    private String name;
    private String stepTypeId;
    private String buildType;
    private String mainClass;
    private long guiX;
    private long guiY;
    private List<StepAttr> params = new ArrayList<StepAttr>();

    @JsonIgnore
    private List<Step> inputSteps = new ArrayList<Step>();
    @JsonIgnore
    private List<Step> outputSteps = new ArrayList<Step>();
    @JsonIgnore
    private AnalysisStepTypeEntity stepType;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStepTypeId() {
        return stepTypeId;
    }

    public void setStepTypeId(String stepTypeId) {
        this.stepTypeId = stepTypeId;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public long getGuiX() {
        return guiX;
    }

    public void setGuiX(long guiX) {
        this.guiX = guiX;
    }

    public long getGuiY() {
        return guiY;
    }

    public void setGuiY(long guiY) {
        this.guiY = guiY;
    }

    public List<StepAttr> getParams() {
        return params;
    }

    public void setParams(List<StepAttr> params) {
        this.params = params;
    }

    public AnalysisStepTypeEntity getStepType() {
        return stepType;
    }

    public void setStepType(AnalysisStepTypeEntity stepType) {
        this.stepType = stepType;
    }

    public AnalysisStepEntity toStepEntities() {
        AnalysisStepEntity stepEntity = new AnalysisStepEntity();
        stepEntity.setId(this.getId());
        stepEntity.setModelId(this.getModel().getId());
        stepEntity.setName(this.getName());
        stepEntity.setStepType(new AnalysisStepTypeEntity(this.getStepTypeId()));
        stepEntity.setBuildType(this.buildType);
        stepEntity.setMainClass(this.mainClass);
        stepEntity.setGuiX(this.getGuiX());
        stepEntity.setGuiY(this.guiY);

        return stepEntity;
    }

    public List<AnalysisStepAttrEntity> toAttrEntities() {
        List<AnalysisStepAttrEntity> attrEntities = new ArrayList<AnalysisStepAttrEntity>();
        for (int index = 0; index < params.size(); index++) {
            attrEntities.addAll(params.get(index).toAttrEntity(index));
        }

        return attrEntities;
    }

    public static Step buildFromStepEntity(Model model, AnalysisStepEntity stepEntity) {
        Step step = new Step();
        step.setModel(model);
        step.setId(stepEntity.getId());
        step.setGuiX(stepEntity.getGuiX());
        step.setGuiY(stepEntity.getGuiY());
        step.setName(stepEntity.getName());
        step.setStepTypeId(stepEntity.getStepType().getId());
        step.setStepType(stepEntity.getStepType());
        step.setBuildType(stepEntity.getBuildType());
        step.setMainClass(stepEntity.getMainClass());
        step.getParams().addAll(buildFromStepAttrEntity(step, stepEntity));

        return step;
    }

    private static Collection<StepAttr> buildFromStepAttrEntity(Step step, AnalysisStepEntity stepEntity) {
        Map<Object, StepAttr> stepAttrMap = new HashMap<Object, StepAttr>();
        List<AnalysisStepAttrEntity> attrEntities = stepEntity.getAttrs();
        for (AnalysisStepAttrEntity attrEntity : attrEntities) {
            int nRow = attrEntity.getnRow();
            StepAttr stepAttr = stepAttrMap.get(nRow);
            if (stepAttr == null) {
                stepAttr = new StepAttr();
                stepAttr.setStep(step);
                stepAttrMap.put(nRow, stepAttr);
            }
            stepAttr.put(attrEntity.getCode(), new StepAttr.AttrValue(attrEntity.getValueStr(), attrEntity
                    .getValueNum()));
        }

        return stepAttrMap.values();
    }

    public static Step buildFromJson(Model model, JsonObject stepJsonObj) {
        Step step = new Step();
        step.setModel(model);
        step.setId(stepJsonObj.get("id").getAsString());
        step.setGuiX(stepJsonObj.get("guiX").getAsLong());
        step.setGuiY(stepJsonObj.get("guiY").getAsLong());
        step.setName(stepJsonObj.get("name").getAsString());
        step.setStepTypeId(stepJsonObj.get("stepTypeId").getAsString());
        step.setBuildType(stepJsonObj.get("buildType").getAsString());
        step.setMainClass(stepJsonObj.get("mainClass").getAsString());
        step.getParams().addAll(buildStepAttrFromJson(step, stepJsonObj));

        return step;
    }

    private static Collection<StepAttr> buildStepAttrFromJson(Step step, JsonObject stepJsonObj) {
        Map<Object, StepAttr> stepAttrMap = new HashMap<Object, StepAttr>();
        if (JacksonUtils.isBlank(stepJsonObj, "params")) {
            return stepAttrMap.values();
        }

        JsonArray attrJsonArray = stepJsonObj.get("params").getAsJsonArray();
        int nRow = 0;
        for (JsonElement attrJsonEl : attrJsonArray) {
            JsonObject attrJsonObj = attrJsonEl.getAsJsonObject();
            StepAttr stepAttr = null;
            //if exist attr, go on.
            if (attrJsonObj.size() > 0) {
                stepAttr = stepAttrMap.get(nRow);
                if (stepAttr == null) {
                    stepAttr = new StepAttr();
                    stepAttr.setStep(step);
                    stepAttrMap.put(nRow, stepAttr);
                }
            } else {
                continue;
            }

            Iterator<Map.Entry<String, JsonElement>> it = attrJsonObj.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, JsonElement> entry = it.next();
                JsonObject valJsonObject = null;
                if (entry.getKey().equals(StepAttr.AttrValue.FIELD_TAG_VALUESTR)) {
                    stepAttr.put(entry.getKey(), new StepAttr.AttrValue(entry.getValue().toString(), null));
                }else {
                    valJsonObject = entry.getValue().getAsJsonObject();
                    if (valJsonObject.get(StepAttr.AttrValue.FIELD_TAG_VALUENUM) != null) {
                        stepAttr.put(entry.getKey(), new StepAttr.AttrValue(null, valJsonObject.get(StepAttr.AttrValue
                                .FIELD_TAG_VALUENUM).getAsBigDecimal()));
                    } else {
                        stepAttr.put(entry.getKey(), new StepAttr.AttrValue(valJsonObject.get(StepAttr.AttrValue
                                .FIELD_TAG_VALUESTR).getAsString(), null));
                    }
                }
            }

            nRow++;
        }

        return stepAttrMap.values();
    }

    public List<Step> getInputSteps() {
        return inputSteps;
    }

    public List<Step> getOutputSteps() {
        return outputSteps;
    }


}
