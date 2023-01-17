package com.dfssi.dataplatform.workflow.spark;

import com.dfssi.dataplatform.analysis.model.Model;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class SparkTaskDef extends AbstractSparkDef {

    private Model model;
    private String name;

    private InitDef initDef;
    private List<InputDef> inputDefs = new ArrayList<InputDef>();
    private List<PreprocessDef> preprocessDefs = new ArrayList<PreprocessDef>();
    private List<AlgorithmDef> algorithmDefs = new ArrayList<AlgorithmDef>();
    private List<OutputDef> outputDefs = new ArrayList<OutputDef>();

    public SparkTaskDef(Model model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String toXml() {
        Document doc = DocumentHelper.createDocument();
        Element sparkTaskDefEl = doc.addElement(this.getRootTagByType());
        sparkTaskDefEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_ID, this.getId());
        sparkTaskDefEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_NAME, this.getName());

        //流任务则设置 批次时间
        if (Model.isStreamingAnalysisModel(this.model.getModelTypeId())) {
            sparkTaskDefEl.addAttribute(SparkDefTag.SPARK_DEF_ATTR_TAG_BATCHDURATIONSECOND,
                    this.model.getBatchDurationSecond());
        }

        this.generateInitEl(sparkTaskDefEl);
        this.generateInputEl(sparkTaskDefEl);
        this.generatePreprocessEl(sparkTaskDefEl);
        this.generateAlgorithmsEl(sparkTaskDefEl);
        this.generateOutputsEl(sparkTaskDefEl);

        return doc.getRootElement().asXML();
    }

    private String getRootTagByType() {
        if (Model.isOfflineAnalysisModel(this.model.getModelTypeId())) {
            return SparkDefTag.SPARK_DEF_ELEMENT_TAG_ROOT;
        } else if (Model.isStreamingAnalysisModel(this.model.getModelTypeId())) {
            return SparkDefTag.STREAMING_DEF_ELEMENT_TAG_ROOT;
        } else if (Model.isIntegrateAnalysisModel(this.model.getModelTypeId())) {
            return SparkDefTag.INTEGRATE_DEF_ELEMENT_TAG_ROOT;
        } else {
            return SparkDefTag.EXTERNAL_DEF_ELEMENT_TAG_ROOT;
        }
    }

    private void generateInitEl(Element sparkTaskDefEl) {
        if (initDef == null) return;
        Element initsEl = sparkTaskDefEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_INITS);
        initDef.buildXmlElement(initsEl);
    }

    private void generateInputEl(Element sparkTaskDefEl) {
        if (inputDefs.size() <= 0) return;

        Element inputsEl = sparkTaskDefEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUTS);
        for (InputDef inputDef : inputDefs) {
            inputDef.buildXmlElement(inputsEl);
        }
    }

    private void generatePreprocessEl(Element sparkTaskDefEl) {
        if (preprocessDefs.size() <= 0) return;

        Element preprocessEl = sparkTaskDefEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS);
        for (PreprocessDef preprocessDef : preprocessDefs) {
            // 根据modeTypeId判断，是否是第三方jar包任务，生成不同xml文件
            preprocessDef.buildXmlElement(preprocessEl, this.model.getModelTypeId());
        }
    }

    private void generateAlgorithmsEl(Element sparkTaskDefEl) {
        if (algorithmDefs.size() <= 0) return;

        Element preprocessEl = sparkTaskDefEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHMS);
        for (AlgorithmDef algorithmDef : algorithmDefs) {
            algorithmDef.buildXmlElement(preprocessEl);
        }
    }

    private void generateOutputsEl(Element sparkTaskDefEl) {
        if (outputDefs.size() <= 0) return;

        Element outputEl = sparkTaskDefEl.addElement(SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUTS);
        for (OutputDef outputDef : outputDefs) {
            outputDef.buildXmlElement(outputEl);
        }
    }

    public void setInitDef(InitDef initDef) {
        this.initDef = initDef;
    }

    public List<InputDef> getInputDefs() {
        return inputDefs;
    }

    public List<PreprocessDef> getPreprocessDefs() {
        return preprocessDefs;
    }

    public List<AlgorithmDef> getAlgorithmDefs() {
        return algorithmDefs;
    }

    public List<OutputDef> getOutputDefs() {
        return outputDefs;
    }
}