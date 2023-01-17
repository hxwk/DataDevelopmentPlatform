package com.dfssi.dataplatform.workflow.oozie.action.control;

import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import com.dfssi.dataplatform.workflow.oozie.action.AbstractActionDef;
import org.dom4j.Element;

public class KillActionDef extends AbstractActionDef {

    public KillActionDef(OozieBuilder oozieBuilder) {
        super(oozieBuilder);
    }

    @Override
    public void buildXmlEl(Element parentEl) {
        Element killEl = parentEl.addElement("kill");
        killEl.addAttribute("name", this.getName());
        killEl.addElement("message").setText("Action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");
    }
}
