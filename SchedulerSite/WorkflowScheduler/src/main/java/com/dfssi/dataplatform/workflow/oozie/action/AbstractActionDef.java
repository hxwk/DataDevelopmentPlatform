package com.dfssi.dataplatform.workflow.oozie.action;

import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import org.dom4j.Element;

abstract public class AbstractActionDef {

    protected String name;
    protected OozieBuilder oozieBuilder;

    public AbstractActionDef(OozieBuilder oozieBuilder) {
        this.setOozieBuilder(oozieBuilder);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OozieBuilder getOozieBuilder() {
        return oozieBuilder;
    }

    public void setOozieBuilder(OozieBuilder oozieBuilder) {
        this.oozieBuilder = oozieBuilder;
    }

    abstract public void buildXmlEl(Element parentEl);

}
