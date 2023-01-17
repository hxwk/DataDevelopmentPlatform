package com.dfssi.dataplatform.analysis.workflow.service;

import com.dfssi.dataplatform.analysis.common.util.JacksonUtils;
import com.google.gson.JsonObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/3 11:23
 */
public class CoordinatorXml {
    private static final Logger logger = LoggerFactory.getLogger(CoordinatorXml.class);

    private static final String COORDINATOR_LABEL = "coord";

    private String name;
    private Document document;

    public CoordinatorXml(String name, Document document){
        this.name = name;
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public boolean noEmpty(){
        return document != null;
    }

    public static CoordinatorXml newCoordinatorXml(JsonObject jsonObject){
        CoordinatorXml xml = null;
        if(jsonObject != null){
            xml = getCoordinatorXmlFromJsonObj(jsonObject);
        }
        return xml;
    }

    private static CoordinatorXml getCoordinatorXmlFromJsonObj(JsonObject jsonObject) {
        CoordinatorXml xml = null;
        Document doc;
        try {
            String modelType = JacksonUtils.getAsString(jsonObject, "modelType");
            if (modelType.contains(COORDINATOR_LABEL)) {

                doc = DocumentHelper.createDocument();
                Element coordinatorAppEl = doc.addElement("coordinator-app", "uri:oozie:coordinator:0.1");
                String name = JacksonUtils.getAsString(jsonObject, "name");

                coordinatorAppEl.addAttribute("name", name);
                coordinatorAppEl.addAttribute("frequency", JacksonUtils.getAsString(jsonObject, "frequency"));
                coordinatorAppEl.addAttribute("timezone", JacksonUtils.getAsString(jsonObject, "timezone"));
                coordinatorAppEl.addAttribute("start", JacksonUtils.getAsString(jsonObject, "start"));
                coordinatorAppEl.addAttribute("end", JacksonUtils.getAsString(jsonObject, "end"));

                Element actioEl = coordinatorAppEl.addElement("action");
                Element workflowEl = actioEl.addElement("workflow");
                workflowEl.addElement("app-path").setText("${nameNode}${sparkRootPath}/app/${appRelativePath}");

                Element configurationEl = workflowEl.addElement("configuration");
                addProperty(configurationEl, "jobTracker", "${jobTracker}");
                addProperty(configurationEl, "nameNode", "${nameNode}");
                addProperty(configurationEl, "queueName", "${queueName}");

                xml = new CoordinatorXml(name, doc);
            }

        } catch (Exception e) {
            logger.error(null, e);
        }

        return xml;
    }

    private static void addProperty(Element propCtlEl, String name, String value) {
        Element nameNodePropEl = propCtlEl.addElement("property");
        nameNodePropEl.addElement("name").setText(name);
        nameNodePropEl.addElement("value").setText(value);
    }

    @Override
    public String toString() {
        return document == null ? null : document.getRootElement().asXML();
    }

}
