package com.dfssi.dataplatform.workflow.spark;

import org.dom4j.Element;

public class InitDef extends AbstractSparkDef {

    public void buildXmlElement(Element initsEl) {
        Element initEl = initsEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_INIT);
        initEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ID, this.getId());
        initEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE, this.getType());
        //initEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS, this.getInputIdsStr());
        Element paramsEl = initEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS);

        this.buildParamsXmlElement(paramsEl);
    }
}
