package com.dfssi.dataplatform.workflow.builder;

import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.analysis.model.step.Step;
import com.dfssi.dataplatform.workflow.builder.oozie.OozieBuilder;
import com.dfssi.dataplatform.workflow.builder.spark.SparkAllTypeBuilder;
import com.dfssi.dataplatform.workflow.oozie.CoordinatorAppDef;
import com.dfssi.dataplatform.workflow.oozie.WorkflowAppDef;
import com.dfssi.dataplatform.workflow.spark.SparkTaskDef;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Build 3 contents:
 * job.properties
 * workflow.xml
 * OfflineTaskDef.xml
 * coordinator.xml
 */
abstract public class AnalysisTaskBuilder {

    private Model model;

    private SparkTaskDef sparkTaskDef = null;
    private OozieBuilder oozieBuilder = null;

    public void clear() {
        this.oozieBuilder = null;
        this.sparkTaskDef = null;
    }

    public AnalysisTaskBuilder(Model model) {
        this.setModel(model);
    }

    public void build() throws Exception {
        if (!validModel(model)) throw new Exception("Model is not a DAG graph.");

        this.clear();

        this.buildOozieBuilder();
        this.buildSparkTaskDef();
    }

    private void buildOozieBuilder() {
        oozieBuilder = new OozieBuilder(this);
        oozieBuilder.buildOozieDef();
    }

    private void buildSparkTaskDef() {
        sparkTaskDef = new SparkTaskDef(this.model);
        sparkTaskDef.setId(model.getId());
        sparkTaskDef.setName(model.getName());

        List<Step> topOrder = getTopOrder(model);
        for (Step step : topOrder) {
//            SparkStepBuilderFactory.getInstance().getBuilder(this, step).build();
//            SparkStepBuilderFactory.getBuilder(step.getStepTypeId(),this, step).build();
            new SparkAllTypeBuilder(this, step).build(step.getBuildType());
        }
    }

    public static boolean validModel(Model model) {
        /* check circle */
        return checkDag(model);
    }

    protected static boolean checkDag(Model model) {
        List<Step> topOrder = getTopOrder(model);

        return topOrder.size() == model.getSteps().size();
    }

    protected static List<Step> getTopOrder(Model model) {
        List<Step> result = new ArrayList<Step>();
        Queue<Step> que = new LinkedList<Step>();

        /* save old steps */
        List<Step> initSteps = new ArrayList<Step>(model.getSteps());

        for (Step step : initSteps) {
            if (step.getInputSteps().size() == 0) que.offer(step);
        }

        while (!que.isEmpty()) {
            Step currentStep = que.poll();
            result.add(currentStep);
            initSteps.remove(currentStep);
            for (Step step : initSteps) {
                step.getInputSteps().remove(currentStep);
                step.getOutputSteps().remove(currentStep);
                if (step.getInputSteps().size() == 0 && !que.contains(step)) {
                    que.offer(step);
                }
            }
        }

        for (Step step : model.getSteps()) {
            step.getInputSteps().clear();
            step.getOutputSteps().clear();
        }

        return result;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public WorkflowAppDef getWorkflowAppDef() {
        return this.oozieBuilder.getWorkflowAppDef();
    }

    public CoordinatorAppDef getCoordinatorAppDef() {
        return this.oozieBuilder.getCoordinatorAppDef();
    }

    public SparkTaskDef getSparkTaskDef() {
        return sparkTaskDef;
    }
}
