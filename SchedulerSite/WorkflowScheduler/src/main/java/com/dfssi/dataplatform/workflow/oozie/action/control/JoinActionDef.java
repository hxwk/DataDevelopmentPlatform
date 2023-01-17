package com.dfssi.dataplatform.workflow.oozie.action.control;

import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import com.dfssi.dataplatform.workflow.oozie.action.AbstractActionDef;
import org.dom4j.Element;

public class JoinActionDef extends AbstractActionDef {

    public JoinActionDef(OozieBuilder oozieBuilder) {
        super(oozieBuilder);
    }

    @Override
    public void buildXmlEl(Element parentEl) {

    }
}
