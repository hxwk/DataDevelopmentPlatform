package com.dfssi.dataplatform.workflow.oozie;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CoordinatorAppDef {

    private String name;
    private String frequency;
    private String timezone = "GMT+0800";
    private String start;
    private String end;

    public CoordinatorAppDef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String toXml() {
        Document doc = DocumentHelper.createDocument();
        Element coordinatorAppEl = doc.addElement("coordinator-app", "uri:oozie:coordinator:0.1");
        //调度规律以及时区交由用户配置
        coordinatorAppEl.addAttribute("name", this.getName());
        coordinatorAppEl.addAttribute("frequency", this.getFrequency());
        coordinatorAppEl.addAttribute("timezone", this.getTimezone());
        coordinatorAppEl.addAttribute("start", this.getStart());
        coordinatorAppEl.addAttribute("end", this.getEnd());

        //固定的任务配置参数
        Element actioEl = coordinatorAppEl.addElement("action");
        Element workflowEl = actioEl.addElement("workflow");
        workflowEl.addElement("app-path").setText("${nameNode}${sparkRootPath}/app/${appRelativePath}");
        Element configurationEl = workflowEl.addElement("configuration");
        this.addProperty(configurationEl, "jobTracker", "${jobTracker}");
        this.addProperty(configurationEl, "nameNode", "${nameNode}");
        this.addProperty(configurationEl, "sparkRootPath", "${sparkRootPath}");
        this.addProperty(configurationEl, "appRelativePath", "${appRelativePath}");
        this.addProperty(configurationEl, "master", "${master}");
        this.addProperty(configurationEl, "queueName", "${queueName}");
        this.addProperty(configurationEl, "offlineSparkActionClass", "${offlineSparkActionClass}");
        this.addProperty(configurationEl, "sparkActionjar", "${sparkActionjar}");
        this.addProperty(configurationEl, "offlineSparkOpts", "${offlineSparkOpts}");
        this.addProperty(configurationEl, "user.name", "${userName}");
        this.addProperty(configurationEl, "mapreduce.job.user.name", "${mrUserName}");
        this.addProperty(configurationEl, "oozie.use.system.libpath", "${useSystemLibpath}");

        return doc.getRootElement().asXML();
    }

    private void addProperty(Element propCtlEl, String name, String value) {
        Element nameNodePropEl = propCtlEl.addElement("property");
        nameNodePropEl.addElement("name").setText(name);
        nameNodePropEl.addElement("value").setText(value);
    }
}
