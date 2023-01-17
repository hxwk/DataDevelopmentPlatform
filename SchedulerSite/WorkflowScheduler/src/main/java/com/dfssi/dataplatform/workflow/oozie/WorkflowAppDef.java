package com.dfssi.dataplatform.workflow.oozie;

import com.dfssi.dataplatform.workflow.oozie.action.AbstractActionDef;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class WorkflowAppDef {

    private String name;
    private List<AbstractActionDef> actions = new ArrayList<AbstractActionDef>();

    public WorkflowAppDef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAction(AbstractActionDef actionDef) {
        this.actions.add(actionDef);
    }

    public String toXml() {
        Document doc = DocumentHelper.createDocument();
        Element workflowAppEl = doc.addElement("workflow-app", "uri:oozie:workflow:0.5");

        workflowAppEl.addAttribute("name", this.getName());
        for (AbstractActionDef actionDef : actions) {
            actionDef.buildXmlEl(workflowAppEl);
        }

        return doc.getRootElement().asXML();
    }

}
