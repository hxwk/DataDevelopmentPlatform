package com.dfssi.dataplatform.workflow.spark;

import org.dom4j.Element;

public class InputDef extends AbstractSparkDef {

    public void buildXmlElement(Element inputsEl) {
        Element inputEl = inputsEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT);
        inputEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ID, this.getId());
        inputEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE, this.getType());
        inputEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS, this.getInputIdsStr());
        Element paramsEl = inputEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS);

        this.buildParamsXmlElement(paramsEl);
    }
}
