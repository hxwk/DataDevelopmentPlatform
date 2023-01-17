package com.dfssi.dataplatform.workflow.builder.spark;

import com.dfssi.dataplatform.analysis.model.step.Step;
import com.dfssi.dataplatform.workflow.builder.AnalysisTaskBuilder;
import com.dfssi.dataplatform.workflow.spark.*;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/05/17 17:00
 */
public class SparkAllTypeBuilder extends AbstractSparkBuilder {

    public SparkAllTypeBuilder(AnalysisTaskBuilder analysisTaskBuilder, Step step) {
        super(analysisTaskBuilder, step);
    }

    @Override
    public void build(String buildType) {
        if (buildType.equals(SparkDefTag.SPARK_DEF_BUILD_TYPE_INIT)) {
            this.buildInitSimpleParams();
        } else if (buildType.equals(SparkDefTag.SPARK_DEF_BUILD_TYPE_INPUT)) {
            this.buildInputSimpleParams();
        } else if (buildType.equals(SparkDefTag.SPARK_DEF_BUILD_TYPE_PREPROCESS)) {
            this.buildPreprocessSimpleDef();
        } else if (buildType.equals(SparkDefTag.SPARK_DEF_BUILD_TYPE_ALGORITHM)) {
            this.buildAlogorithSimpleParams();
        } else if (buildType.equals(SparkDefTag.SPARK_DEF_BUILD_TYPE_OUTPUT)) {
            this.buildOutput();
        }
    }

    // build init def with simple param type.
    private void buildInitSimpleParams() {
        InitDef initDef = new InitDef();
        initDef.setId(step.getId());
        initDef.setType(step.getStepTypeId());

        buildParams(initDef, step);
        this.analysisTaskBuilder.getSparkTaskDef().setInitDef(initDef);
    }

    // build input def with simple param type.
    private void buildInputSimpleParams() {
        InputDef inputDef = new InputDef();
        inputDef.setId(step.getId());
        inputDef.setType(step.getStepTypeId());

        buildParams(inputDef, step);
        inputDef.getInputIds().addAll(buildInputIds());
        this.analysisTaskBuilder.getSparkTaskDef().getInputDefs().add(inputDef);
    }

    /**
     * build preprocess def with simple param type.
     */
    public void buildPreprocessSimpleDef() {
        PreprocessDef preprocessDef = new PreprocessDef();
        preprocessDef.setId(step.getId());
        preprocessDef.setType(step.getStepTypeId());
        preprocessDef.setMainClass(step.getMainClass());//by pengwk
        buildParams(preprocessDef, step);
        preprocessDef.getInputIds().addAll(buildInputIds());

        analysisTaskBuilder.getSparkTaskDef().getPreprocessDefs().add(preprocessDef);
    }

    // build algorith def with simple param type.
    private void buildAlogorithSimpleParams() {
        AlgorithmDef alogorithmDef = new AlgorithmDef();
        alogorithmDef.setId(step.getId());
        alogorithmDef.setType(step.getStepTypeId());

        buildParams(alogorithmDef, step);
        alogorithmDef.getInputIds().addAll(buildInputIds());

        this.analysisTaskBuilder.getSparkTaskDef().getAlgorithmDefs().add(alogorithmDef);
    }

    // build output def with simple param type.
    public void buildOutput() {
        OutputDef outputDef = new OutputDef();
        outputDef.setId(step.getId());
        outputDef.setType(step.getStepTypeId());

        buildParams(outputDef, step);
        outputDef.getInputIds().addAll(buildInputIds());

        this.analysisTaskBuilder.getSparkTaskDef().getOutputDefs().add(outputDef);
    }
}