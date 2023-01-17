package com.dfssi.dataplatform.workflow.spark;

import com.dfssi.dataplatform.analysis.model.Model;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class PreprocessDef extends AbstractSparkDef {

    public void buildXmlElement(Element preprocessEl, String modeTypeId) {
        Element processEl = preprocessEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS);
        processEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ID, this.getId());
        processEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE, this.getType());
        processEl.addAttribute(SparkDefTag.SPARK_DEF_ELEMENT_TAG_MAIN_CLASS, this.getMainClass());
        // 第三方jar包任务
        if (Model.isExternalAnalysisModel(modeTypeId)) {
            processEl.addAttribute(SparkDefTag.SPARK_DEF_ELEMENT_TAG_MAIN_CLASS, StringUtils.defaultIfEmpty(this.mainClass, ""));
        } else {
            processEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS, this.getInputIdsStr());
        }
        Element paramsEl = processEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS);

        this.buildParamsXmlElement(paramsEl);
    }

}
