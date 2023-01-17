package com.dfssi.dataplatform.workflow.oozie.action.hadoop;

import com.dfssi.dataplatform.analysis.entity.AnalysisStepAttrEntity;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import com.dfssi.dataplatform.workflow.spark.SparkDefTag;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import java.util.List;

public class SparkActionDef extends HadoopBaseActionDef {

    private String sparkAppName;

    public SparkActionDef(OozieBuilder oozieBuilder) {
        super(oozieBuilder);
    }

    public String getSparkAppName() {
        return sparkAppName;
    }

    public void setSparkAppName(String sparkAppName) {
        this.sparkAppName = sparkAppName;
    }

    @Override
    public void buildXmlEl(Element parentEl) {
        Element actionEl = parentEl.addElement("action");
        actionEl.addAttribute("name", this.getName());
        Element sparkEl = actionEl.addElement("spark", "uri:oozie:spark-action:0.1");
        sparkEl.addElement("job-tracker").setText("${jobTracker}");
        sparkEl.addElement("name-node").setText("${nameNode}");
        Element prepareEl = sparkEl.addElement("prepare");
        prepareEl.addElement("delete").addAttribute("path", "${nameNode}/${sparkRootPath}/app/${appRelativePath}/tmp");
        prepareEl.addElement("delete").addAttribute("path",
                "${nameNode}/${sparkRootPath}/app/${appRelativePath}/output");
        sparkEl.addElement("master").setText("${master}");
        sparkEl.addElement("name").setText(this.getSparkAppName());
        if (Model.isOfflineAnalysisModel(this.getOozieBuilder().getAnalysisTaskBuilder().getModel().getModelTypeId())) {
            this.buildOfflineXmlEl(sparkEl);
        } else if (Model.isStreamingAnalysisModel(this.getOozieBuilder().getAnalysisTaskBuilder().getModel()
                .getModelTypeId())) {
            this.buildStreamingXmlEl(sparkEl);
        } else if (Model.isIntegrateAnalysisModel(this.getOozieBuilder().getAnalysisTaskBuilder().getModel()
                .getModelTypeId())) {
            this.buildIntegrateXmlEl(sparkEl);
        } else if (Model.isExternalAnalysisModel(this.getOozieBuilder().getAnalysisTaskBuilder().getModel()
                .getModelTypeId())) {
            this.buildExternalXmlEl(sparkEl);
        }
        sparkEl.addElement("arg").setText("${nameNode}");
        sparkEl.addElement("arg").setText("${sparkRootPath}/app/${appRelativePath}");

        actionEl.addElement("ok").addAttribute("to", OozieBuilder.ACTION_NAME_END);
        actionEl.addElement("error").addAttribute("to", OozieBuilder.ACTION_NAME_KILL);
    }

    private void buildOfflineXmlEl(Element sparkEl) {
        sparkEl.addElement("class").setText("${offlineSparkActionClass}");
        sparkEl.addElement("jar").setText("${nameNode}/${sparkRootPath}/lib/" + "${sparkActionjar}");
        sparkEl.addElement("spark-opts").setText(createSparkOpts(oozieBuilder.getAnalysisTaskBuilder()
                .getModel().getSparkOpts(), "${offlineSparkOpts}"));
    }

    private void buildStreamingXmlEl(Element sparkEl) {
        sparkEl.addElement("class").setText("${streamingSparkActionClass}");
        sparkEl.addElement("jar").setText("${nameNode}/${sparkRootPath}/lib/" + "${sparkActionjar}");
        sparkEl.addElement("spark-opts").setText(createSparkOpts(oozieBuilder.getAnalysisTaskBuilder()
                .getModel().getSparkOpts(), "${streamingSparkOpts}"));
    }

    private void buildIntegrateXmlEl(Element sparkEl) {
        sparkEl.addElement("class").setText("${integrateSparkActionClass}");
        sparkEl.addElement("jar").setText("${nameNode}/${sparkRootPath}/lib/" + "${sparkActionjar}");
        sparkEl.addElement("spark-opts").setText(createSparkOpts(oozieBuilder.getAnalysisTaskBuilder()
                .getModel().getSparkOpts(), "${streamingSparkOpts}"));
    }

    private void buildExternalXmlEl(Element sparkEl) {
        sparkEl.addElement("class").setText("${externalSparkActionClass}");
        sparkEl.addElement("jar").setText("${nameNode}/${sparkRootPath}/lib/" + "${sparkActionjar}");
        sparkEl.addElement("spark-opts").setText(createSparkOpts(oozieBuilder.getAnalysisTaskBuilder()
                .getModel().getSparkOpts(), "${streamingSparkOpts}"));
    }
//    private void buildExternalXmlEl(Element sparkEl) {
//        sparkEl.addElement("class").setText("${externalSparkActionClass}");
//        String jarPath = null;
//        List<AnalysisStepAttrEntity> attrs = this.getOozieBuilder().getAnalysisTaskBuilder().getModel().toStepEntities().get(0).getAttrs();
//        for (AnalysisStepAttrEntity attr : attrs) {
//            if (attr.getCode().equalsIgnoreCase(SparkDefTag.SPARK_DEF_ELEMENT_TAG_JAR_PATH)) {
//                jarPath = attr.getValueStr();
//            }
//        }
//        sparkEl.addElement("jar").setText("${nameNode}/" + jarPath);
//        sparkEl.addElement("spark-opts").setText(StringUtils.defaultIfEmpty(oozieBuilder.getAnalysisTaskBuilder()
//                .getModel().getSparkOpts(), "${streamingSparkOpts}"));
//    }

    //根据配置的资源参数 和 系统内置的固定参数 合成任务参数
    private String createSparkOpts(String resoucesOpts, String defaultOpts){
        if (StringUtils.isBlank(resoucesOpts)) {
            return defaultOpts;
        }
        return String.format("%s ${sparkOpts}", resoucesOpts);
    }
}
