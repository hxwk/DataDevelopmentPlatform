package com.dfssi.dataplatform.analysis.workflow.service;

import com.dfssi.dataplatform.analysis.common.util.JacksonUtils;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/3 10:52
 */
public class WorkflowXml {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowXml.class);

    private String name;
    private Document document;

    public WorkflowXml(String name, Document document){
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public boolean noEmpty(){
        return document != null;
    }

    public static WorkflowXml newWorkflowXml(JsonObject jsonObject){
        WorkflowXml xml = null;
        if(jsonObject != null){
            xml = getWorkflowXmlFromJsonObj(jsonObject);
        }
        return xml;
    }

    private static WorkflowXml getWorkflowXmlFromJsonObj(JsonObject jsonObject) {
        WorkflowXml xml = null;
        Document doc;
        try {
            doc = DocumentHelper.createDocument();
            Element workflowAppEl = doc.addElement("workflow-app", "uri:oozie:workflow:0.5");
            String name = JacksonUtils.getAsString(jsonObject, "name");
            workflowAppEl.addAttribute("name", name);

            workflowAppEl.addElement("start").addAttribute("to", "analysis-task-action");

            Element actionEl = workflowAppEl.addElement("action");
            actionEl.addAttribute("name", "analysis-task-action");

            Element sparkEl = actionEl.addElement("spark", "uri:oozie:spark-action:0.1");
            sparkEl.addElement("job-tracker").setText("${jobTracker}");
            sparkEl.addElement("name-node").setText("${nameNode}");

            Element prepareEl = sparkEl.addElement("prepare");
            prepareEl.addElement("delete").addAttribute("path", "${nameNode}/${sparkRootPath}/app/${appRelativePath}/tmp");
            prepareEl.addElement("delete").addAttribute("path", "${nameNode}/${sparkRootPath}/app/${appRelativePath}/output");

            sparkEl.addElement("master").setText("${master}");
            sparkEl.addElement("name").setText(name);

            String modelType = JacksonUtils.getAsString(jsonObject, "modelType");
            switch (modelType.toLowerCase()){
                case "offlineanalysis":
                case "offlinecoordanalysis" :
                    sparkEl.addElement("class").setText("${offlineSparkActionClass}");
                    break;
                case "streaminganalysis" :
                    sparkEl.addElement("class").setText("${streamingSparkActionClass}");
                    break;
                case "externalanalysis":
                case "externalcoordanalysis":
                    sparkEl.addElement("class").setText("${externalSparkActionClass}");
                    break;
                default:
                    throw new IllegalArgumentException(String.format("不识别的modelType：%s", modelType));
            }

            sparkEl.addElement("jar").setText("${nameNode}/${sparkRootPath}/lib/" + "${sparkActionjar}");

            String sparkOpts = JacksonUtils.getAsString(jsonObject, "sparkOpts");
            if (StringUtils.isBlank(sparkOpts)) {
                sparkEl.addElement("spark-opts").setText("${defaultSparkOpts}");
            } else {
                sparkEl.addElement("spark-opts").setText(String.format("%s ${sparkOpts}", sparkOpts));
            }

            sparkEl.addElement("arg").setText("${nameNode}");
            sparkEl.addElement("arg").setText("${sparkRootPath}/app/${appRelativePath}");

            actionEl.addElement("ok").addAttribute("to", "end");
            actionEl.addElement("error").addAttribute("to", "fail");

            Element killEl = workflowAppEl.addElement("kill").addAttribute("name", "fail");
            killEl.addElement("message").setText("Action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");

            workflowAppEl.addElement("end").addAttribute("name", "end");

            xml = new WorkflowXml(name, doc);
        } catch (Exception e) {
            logger.error(null, e);
        }

        return xml;
    }

    @Override
    public String toString() {
        return document == null ? null : document.getRootElement().asXML();
    }

}
