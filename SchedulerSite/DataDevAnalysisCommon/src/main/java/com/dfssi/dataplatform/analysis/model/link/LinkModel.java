package com.dfssi.dataplatform.analysis.model.link;

import com.dfssi.dataplatform.analysis.entity.AnalysisLinkEntity;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.analysis.model.step.Step;
import com.dfssi.dataplatform.analysis.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class LinkModel {

    private String id;
    private LinkPointAttr modelStepFrom;
    private LinkPointAttr modelStepTo;
    @JsonIgnore
    private Model model;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkPointAttr getModelStepFrom() {
        return modelStepFrom;
    }

    public void setModelStepFrom(LinkPointAttr modelStepFrom) {
        this.modelStepFrom = modelStepFrom;
    }

    public LinkPointAttr getModelStepTo() {
        return modelStepTo;
    }

    public void setModelStepTo(LinkPointAttr modelStepTo) {
        this.modelStepTo = modelStepTo;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public AnalysisLinkEntity toLinkEntity() {
        AnalysisLinkEntity linkEntity = new AnalysisLinkEntity();
        linkEntity.setId(this.getId());
        linkEntity.setModelId(this.getModel().getId());
        linkEntity.setModelStepFromId(this.getModelStepFrom().getId());
        linkEntity.setModelStepFromPos(this.getModelStepFrom().getPos());
        linkEntity.setModelStepToId(this.getModelStepTo().getId());
        linkEntity.setModelStepToPos(this.getModelStepTo().getPos());

        return linkEntity;
    }

    public static LinkModel buildFromLinkEntity(Model model, AnalysisLinkEntity linkEntity) {
        LinkModel linkModel = new LinkModel();
        linkModel.setId(linkEntity.getId());
        linkModel.setModelStepFrom(new LinkPointAttr(linkEntity.getModelStepFromId(), linkEntity.getModelStepFromPos
                ()));
        linkModel.setModelStepTo(new LinkPointAttr(linkEntity.getModelStepToId(), linkEntity.getModelStepToPos()));
        linkModel.setModel(model);

        return linkModel;
    }

    public static LinkModel buildFromJson(Model model, JsonObject linkJsonObj) {
        LinkModel linkModel = new LinkModel();
        if (!JacksonUtils.isBlank(linkJsonObj, "id")) {
            linkModel.setId(linkJsonObj.get("id").getAsString());
        } else {
            linkModel.setId(Model.generateId());
        }
        if (!JacksonUtils.isBlank(linkJsonObj, "modelStepFrom")) {
            JsonObject fromJson = linkJsonObj.get("modelStepFrom").getAsJsonObject();
            linkModel.setModelStepFrom(new LinkPointAttr(fromJson.get("id").getAsString(), fromJson.get("pos")
                    .getAsString()));
        }
        if (!JacksonUtils.isBlank(linkJsonObj, "modelStepTo")) {
            JsonObject toJson = linkJsonObj.get("modelStepTo").getAsJsonObject();
            linkModel.setModelStepTo(new LinkPointAttr(toJson.get("id").getAsString(), toJson.get("pos").getAsString
                    ()));
        }
        linkModel.setModel(model);

        return linkModel;
    }

    public static class LinkPointAttr {
        private String id;
        private String pos;

        public LinkPointAttr() {
        }

        public LinkPointAttr(String id, String pos) {
            this.setId(id);
            this.setPos(pos);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }
    }
}
