package com.dfssi.dataplatform.workflow.spark;

import org.dom4j.Element;

public class AlgorithmDef extends AbstractSparkDef {

    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void buildXmlElement(Element algorithmsEl) {
        Element algorithmEl = algorithmsEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM);
        algorithmEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ID, this.getId());
        algorithmEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE, this.getType());
        algorithmEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ACTION, this.getAction());
        algorithmEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS, this.getInputIdsStr());

        Element paramsEl = algorithmEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS);
        this.buildParamsXmlElement(paramsEl);
    }
}
