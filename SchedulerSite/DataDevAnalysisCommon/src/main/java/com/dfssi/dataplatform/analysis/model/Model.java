package com.dfssi.dataplatform.analysis.model;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.model.step.Step;
import com.dfssi.dataplatform.analysis.model.link.LinkModel;
import com.dfssi.dataplatform.analysis.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Model {

    private static volatile long index = 0;

    @JsonIgnore
    private static final String ANALYSIS_MODEL_TYPE_ID_OFFLINE = "OfflineAnalysis";
    @JsonIgnore
    private static final String ANALYSIS_MODEL_TYPE_ID_STREAMING = "StreamingAnalysis";
    @JsonIgnore
    private static final String ANALYSIS_MODEL_TYPE_ID_INTEGRATE = "IntegrateAnalysis";
    @JsonIgnore
    private static final String ANALYSIS_MODEL_TYPE_ID_EXTERNAL = "ExternalAnalysis";

    private String id;
    private String name;
    private String modelTypeId;
    private String description;
    private String sparkOpts;

    //coordinator周期任务配值
    private String cronExp;
    private String timezone;
    private String coordStart;
    private String coordEnd;

    //流式任务 批次间隔时间
    private String batchDurationSecond;

    private List<Step> steps = new ArrayList<Step>();
    private List<LinkModel> links = new ArrayList<LinkModel>();

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

    public String getModelTypeId() {
        return modelTypeId;
    }

    public void setModelTypeId(String modelTypeId) {
        this.modelTypeId = modelTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getSparkOpts() {
        return sparkOpts;
    }

    public void setSparkOpts(String sparkOpts) {
        this.sparkOpts = sparkOpts;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCoordStart() {
        return coordStart;
    }

    public void setCoordStart(String coordStart) {
        this.coordStart = coordStart;
    }

    public String getCoordEnd() {
        return coordEnd;
    }

    public void setCoordEnd(String coordEnd) {
        this.coordEnd = coordEnd;
    }

    public String getBatchDurationSecond() {
        return batchDurationSecond;
    }

    public void setBatchDurationSecond(String batchDurationSecond) {
        this.batchDurationSecond = batchDurationSecond;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<LinkModel> getLinks() {
        return links;
    }

    public static boolean isOfflineAnalysisModel(String modelTypeId) {
        return ANALYSIS_MODEL_TYPE_ID_OFFLINE.equalsIgnoreCase(modelTypeId);
    }

    public static boolean isStreamingAnalysisModel(String modelTypeId) {
        return ANALYSIS_MODEL_TYPE_ID_STREAMING.equalsIgnoreCase(modelTypeId);
    }

    public static boolean isIntegrateAnalysisModel(String modelTypeId) {
        return ANALYSIS_MODEL_TYPE_ID_INTEGRATE.equalsIgnoreCase(modelTypeId);
    }
    public static boolean isExternalAnalysisModel(String modelTypeId) {
        return ANALYSIS_MODEL_TYPE_ID_EXTERNAL.equalsIgnoreCase(modelTypeId);
    }

    public AnalysisModelEntity toModelEntity() {
        AnalysisModelEntity modelEntity = new AnalysisModelEntity();

        modelEntity.setId(this.getId());
        modelEntity.setName(this.getName());
        AnalysisModelTypeEntity modelTypeEntity = new AnalysisModelTypeEntity();
        modelTypeEntity.setId(this.getModelTypeId());
        modelEntity.setModelType(modelTypeEntity);
        modelEntity.setDescription(this.getDescription());
        modelEntity.setCronExp(this.getCronExp());
        modelEntity.setSparkOpts(this.getSparkOpts());
        modelEntity.setTimezone(this.getTimezone());
        modelEntity.setCoordStart(this.getCoordStart());
        modelEntity.setCoordEnd(this.getCoordEnd());
        modelEntity.setBatchDurationSecond(this.getBatchDurationSecond());

        return modelEntity;
    }

    public List<AnalysisStepEntity> toStepEntities() {
        List<AnalysisStepEntity> stepEntities = new ArrayList<AnalysisStepEntity>();
        for (Step tempStep : steps) {
            stepEntities.add(tempStep.toStepEntities());
        }

        return stepEntities;
    }

    public List<AnalysisStepAttrEntity> toAttrEntities() {
        List<AnalysisStepAttrEntity> attrEntities = new ArrayList<AnalysisStepAttrEntity>();
        for (Step abstractStep : steps) {
            attrEntities.addAll(abstractStep.toAttrEntities());
        }

        return attrEntities;
    }

    public List<AnalysisLinkEntity> toLinkEntities() {
        List<AnalysisLinkEntity> linkEntities = new ArrayList<AnalysisLinkEntity>();
        for (LinkModel link : links) {
            linkEntities.add(link.toLinkEntity());
        }
        return linkEntities;
    }

    public static Model buildFromModelEntity(AnalysisModelEntity modelEntity, List<AnalysisLinkEntity> linkEntities,
                                             List<AnalysisStepEntity> stepEntities) {
        Model model = buildFromModelEntity(modelEntity);
        for (AnalysisLinkEntity tempLinkEntity : linkEntities) {
            model.getLinks().add(LinkModel.buildFromLinkEntity(model, tempLinkEntity));
        }
        for (AnalysisStepEntity tempStepEntity : stepEntities) {
            model.getSteps().add(Step.buildFromStepEntity(model, tempStepEntity));
        }

        return model;
    }

    private static Model buildFromModelEntity(AnalysisModelEntity modelEntity) {
        Model model = new Model();
        model.setId(modelEntity.getId());
        model.setModelTypeId(modelEntity.getModelType().getId());
        model.setName(modelEntity.getName());
        model.setDescription(modelEntity.getDescription());
        model.setCronExp(modelEntity.getCronExp());
        model.setSparkOpts(modelEntity.getSparkOpts());
        model.setTimezone(modelEntity.getTimezone());
        model.setCoordStart(modelEntity.getCoordStart());
        model.setCoordEnd(modelEntity.getCoordEnd());
        model.setBatchDurationSecond(modelEntity.getBatchDurationSecond());

        return model;
    }

    public static Model buildFromJson(String jsonStr) {
        JsonObject modelJsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        Model model = buildFromJson(modelJsonObject);
        if (!JacksonUtils.isBlank(modelJsonObject, "links")) {
            JsonArray linksJsonArray = modelJsonObject.get("links").getAsJsonArray();
            for (JsonElement linkJsonEl : linksJsonArray) {
                JsonObject linkJsonObj = linkJsonEl.getAsJsonObject();
                model.getLinks().add(LinkModel.buildFromJson(model, linkJsonObj));
            }
        }
        if (!JacksonUtils.isBlank(modelJsonObject, "steps")) {
            JsonArray stepsJsonArray = modelJsonObject.get("steps").getAsJsonArray();
            for (JsonElement stepJsonEl : stepsJsonArray) {
                JsonObject stepJsonObj = stepJsonEl.getAsJsonObject();
                model.getSteps().add(Step.buildFromJson(model, stepJsonObj));
            }
        }

        return model;
    }

    private static Model buildFromJson(JsonObject modelJsonObject) {
        Model model = new Model();
        model.setId(JacksonUtils.getAsString(modelJsonObject, "id"));
        model.setModelTypeId(JacksonUtils.getAsString(modelJsonObject, "modelTypeId"));
        model.setName(JacksonUtils.getAsString(modelJsonObject, "name"));
        model.setDescription(JacksonUtils.getAsString(modelJsonObject, "description"));
        model.setCronExp(JacksonUtils.getAsString(modelJsonObject, "cronExp"));
        model.setSparkOpts(JacksonUtils.getAsString(modelJsonObject, "sparkOpts"));
        model.setTimezone(JacksonUtils.getAsString(modelJsonObject, "timezone"));
        model.setCoordStart(JacksonUtils.getAsString(modelJsonObject, "coordStart"));
        model.setCoordEnd(JacksonUtils.getAsString(modelJsonObject, "coordEnd"));
        model.setBatchDurationSecond(JacksonUtils.getAsString(modelJsonObject, "batchDurationSecond"));

        return model;
    }

    public Step findStepById(String stepId) {
        for (Step step : steps) {
            if (step.getId().equals(stepId)) return step;
        }

        return null;
    }

    public static String generateId() {
        long index = nextIndex();
        String indexStr = StringUtils.right("000000" + index, 6);

        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + indexStr;
    }

    public static long nextIndex() {
        if (index == 999999) {
            index = 0;
            return index;
        } else {
            return index++;
        }
    }
}
