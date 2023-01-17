package com.dfssi.dataplatform.workflow.builder.spark;

import com.dfssi.dataplatform.analysis.model.link.LinkModel;
import com.dfssi.dataplatform.analysis.model.step.Step;
import com.dfssi.dataplatform.analysis.model.step.StepAttr;
import com.dfssi.dataplatform.workflow.builder.AnalysisTaskBuilder;
import com.dfssi.dataplatform.workflow.spark.AbstractSparkDef;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

abstract public class AbstractSparkBuilder {

    protected AnalysisTaskBuilder analysisTaskBuilder;
    protected Step step;

    public AbstractSparkBuilder(AnalysisTaskBuilder analysisTaskBuilder, Step step) {
        this.setAnalysisTaskBuilder(analysisTaskBuilder);
        this.setStep(step);
    }

    abstract public void build(String buildType);

    public AnalysisTaskBuilder getAnalysisTaskBuilder() {
        return analysisTaskBuilder;
    }

    public void setAnalysisTaskBuilder(AnalysisTaskBuilder analysisTaskBuilder) {
        this.analysisTaskBuilder = analysisTaskBuilder;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    protected void buildParams(AbstractSparkDef sparkDef, Step step) {
        List<StepAttr> stepAttrs = step.getParams();
        for (StepAttr stepAttr : stepAttrs) {
            Iterator<Map.Entry<String, Object>> it = stepAttr.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                if (entry.getValue() instanceof StepAttr.AttrValue) {
                    if (entry.getKey().equals(StepAttr.AttrValue.FIELD_TAG_VALUESTR)) {
                        sparkDef.addParamOnlyValue(((StepAttr.AttrValue) entry.getValue()).getNotEmptyValue());
                    } else {
                        sparkDef.addParam(entry.getKey(), ((StepAttr.AttrValue) entry.getValue())
                                .getNotEmptyValue());
                    }
                }
            }
        }
    }

    protected List<String> buildInputIds() {
        List<String> inputIds = new ArrayList<String>();
        List<LinkModel> links = analysisTaskBuilder.getModel().getLinks();
        for (LinkModel linkModel : links) {
            if (step.getId().equalsIgnoreCase(linkModel.getModelStepTo().getId())) {
                inputIds.add(linkModel.getModelStepFrom().getId());
            }
        }

        return inputIds;
    }
}
