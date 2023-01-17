package com.dfssi.dataplatform.workflow.spark;

import org.dom4j.Element;

public class OutputDef extends AbstractSparkDef {

    public void buildXmlElement(Element outputsEl) {
        Element processEl = outputsEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT);
        processEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ID, this.getId());
        processEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE, this.getType());
        processEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS, this.getInputIdsStr());
        Element paramsEl = processEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS);

        this.buildParamsXmlElement(paramsEl);
    }
}
