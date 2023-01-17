package com.dfssi.dataplatform.workflow.oozie.action.hadoop;

import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import com.dfssi.dataplatform.workflow.oozie.action.AbstractActionDef;

abstract public class HadoopBaseActionDef extends AbstractActionDef {

    public HadoopBaseActionDef(OozieBuilder oozieBuilder) {
        super(oozieBuilder);
    }
}
