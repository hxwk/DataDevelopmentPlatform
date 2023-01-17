package com.dfssi.dataplatform.workflow.builder.oozie;

import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.workflow.builder.AnalysisTaskBuilder;
import com.dfssi.dataplatform.workflow.oozie.CoordinatorAppDef;
import com.dfssi.dataplatform.workflow.oozie.WorkflowAppDef;
import com.dfssi.dataplatform.workflow.oozie.action.control.EndActionDef;
import com.dfssi.dataplatform.workflow.oozie.action.control.KillActionDef;
import com.dfssi.dataplatform.workflow.oozie.action.control.StartActionDef;
import com.dfssi.dataplatform.workflow.oozie.action.hadoop.SparkActionDef;

public class OozieBuilder {

    private static final String SPARK_ACTION_NAME = "analysis-task-action";
    public static final String ACTION_NAME_KILL = "fail";
    public static final String ACTION_NAME_END = "end";

    private AnalysisTaskBuilder analysisTaskBuilder;

    private WorkflowAppDef workflowAppDef;
    private CoordinatorAppDef coordinatorAppDef;

    public OozieBuilder(AnalysisTaskBuilder analysisTaskBuilder) {
        this.analysisTaskBuilder = analysisTaskBuilder;
    }

    public void buildOozieDef() {
        buildWorkflowAppDef();
        if(this.analysisTaskBuilder.getModel().getCronExp() != null)
            buildCoordinatorDef();
    }

    public AnalysisTaskBuilder getAnalysisTaskBuilder() {
        return analysisTaskBuilder;
    }

    public WorkflowAppDef getWorkflowAppDef() {
        return workflowAppDef;
    }

    private void buildWorkflowAppDef() {
        workflowAppDef = new WorkflowAppDef(this.analysisTaskBuilder.getModel().getName());

        this.buildStartAction();
        this.buildSparkAction();
        this.buildKillAction();
        this.buildEndAction();
    }

    private void buildStartAction() {
        StartActionDef startActionDef = new StartActionDef(this);
        startActionDef.setTo(SPARK_ACTION_NAME);

        this.workflowAppDef.addAction(startActionDef);
    }

    private void buildSparkAction() {
        SparkActionDef sparkActionDef = new SparkActionDef(this);
        sparkActionDef.setName(SPARK_ACTION_NAME);
        sparkActionDef.setSparkAppName(this.analysisTaskBuilder.getModel().getName());

        this.workflowAppDef.addAction(sparkActionDef);
    }

    private void buildKillAction() {
        KillActionDef killActionDef = new KillActionDef(this);
        killActionDef.setName(ACTION_NAME_KILL);

        this.workflowAppDef.addAction(killActionDef);
    }

    private void buildEndAction() {
        EndActionDef endActionDef = new EndActionDef(this);
        endActionDef.setName(ACTION_NAME_END);

        this.workflowAppDef.addAction(endActionDef);
    }

    public CoordinatorAppDef getCoordinatorAppDef() {
        return coordinatorAppDef;
    }

    private void buildCoordinatorDef() {
        Model model = this.analysisTaskBuilder.getModel();
        coordinatorAppDef = new CoordinatorAppDef(model.getName());
        coordinatorAppDef.setFrequency(model.getCronExp());

        if(model.getTimezone() != null)
            coordinatorAppDef.setTimezone(model.getTimezone());

        coordinatorAppDef.setStart(model.getCoordStart());
        coordinatorAppDef.setEnd(model.getCoordEnd());

    }

}
