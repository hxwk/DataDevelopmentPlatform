package com.dfssi.dataplatform.analysis.common.constant;

public interface Constants {

    /**
     * log相关常量
     */
    String LOG_TAG_MODEL_ANALYSIS = "[Analysis Model IDE]";
    String LOG_TAG_MODEL_SERVICE = "[Service Model IDE]";
    String LOG_TAG_WORKFLOW = "[WorkFlow]";

    /**
     * 任务配置相关常量
     */


    /**
     * process相关常量
     */
    int SPARK_STEP_PREVIEW_NUMBER = 100;
    String SPARK_STEP_PREVIEW_DATABASENAME = "dev_analysis";
    String SPARK_STEP_PREVIEW_SAVEMODEL = "overwrite";

    /**
     * model xml相关常量
     */


    /**
     * oozie xml相关常量
     */
    String WORKFLOW_FILE_NAME = "workflow.xml";
    String COORDINATOR_FILE_NAME = "coordinator.xml";

    /**
     * spark XML 相关常量
     */
    String SPARK_TASK_DEF_FILE_NAME = "SparkTaskDef.xml";
    String SPARK_DEF_ELEMENT_TAG_ROOT = "spark-task-def";
    String STREAMING_DEF_ELEMENT_TAG_ROOT = "streaming-task-def";
    String EXTERNAL_DEF_ELEMENT_TAG_ROOT = "external-task-def";
    String SPARK_DEF_ELEMENT_TAG_STREAMING = "streaming";

    String SPARK_DEF_ELEMENT_TAG_INPUTS = "inputs";
    String SPARK_DEF_ELEMENT_TAG_INPUT = "input";
    String SPARK_DEF_ELEMENT_TAG_PARAMS = "params";
    String SPARK_DEF_ELEMENT_TAG_PARAM = "param";
    String SPARK_DEF_ELEMENT_TAG_PREPROCESS = "preprocess";
    String SPARK_DEF_ELEMENT_TAG_PROCESS = "process";
    String SPARK_DEF_ELEMENT_TAG_ALGORITHMS = "algorithms";
    String SPARK_DEF_ELEMENT_TAG_ALGORITHM = "algorithm";
    String SPARK_DEF_ELEMENT_TAG_OUTPUTS = "outputs";
    String SPARK_DEF_ELEMENT_TAG_OUTPUT = "output";
    String SPARK_DEF_ELEMENT_TAG_STEPTYPE = "stepType";
    String SPARK_DEF_ELEMENT_TAG_MODELTYPE = "modelType";

    String  SPARK_DEF_ATTR_TAG_ID = "id";
    String SPARK_DEF_ATTR_TAG_CLASSNAME = "className";
    String SPARK_DEF_ATTR_TAG_INPUTIDS = "inputIds";
    String SPARK_DEF_ATTR_TAG_NAME = "name";
    String SPARK_DEF_ATTR_TAG_VALUE = "value";
    String SPARK_DEF_ATTR_TAG_DURATION = "duration";
    String SPARK_DEF_ATTR_TAG_STEPS = "steps";
    String SPARK_DEF_ATTR_TAG_LINKS = "links";
    String SPARK_DEF_ATTR_TAG_MODELSTEPFROM = "modelStepFrom";
    String SPARK_DEF_ATTR_TAG_MODELSTEPTO = "modelStepTo";

    String SPARK_DEF__TYPE_INPUT = "Input";
    String SPARK_DEF__TYPE_PREPROCESS = "Preprocess";
    String SPARK_DEF__TYPE_ALGORITHM = "Algorithm";
    String SPARK_DEF__TYPE_OUTPUT = "Output";

}
