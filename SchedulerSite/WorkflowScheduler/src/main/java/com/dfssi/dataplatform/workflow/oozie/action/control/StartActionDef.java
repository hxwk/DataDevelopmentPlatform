package com.dfssi.dataplatform.workflow.oozie.action.control;

import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import com.dfssi.dataplatform.workflow.oozie.action.AbstractActionDef;
import org.dom4j.Element;

public class StartActionDef extends AbstractActionDef {

    private String to;

    public StartActionDef(OozieBuilder oozieBuilder) {
        super(oozieBuilder);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public void buildXmlEl(Element parentEl) {
        Element startEl = parentEl.addElement("start");
        startEl.addAttribute("to", this.getTo());
    }
}
