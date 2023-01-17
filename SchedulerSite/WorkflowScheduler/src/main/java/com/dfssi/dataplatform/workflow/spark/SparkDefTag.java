package com.dfssi.dataplatform.workflow.spark;

public class SparkDefTag {

    public static final String SPARK_DEF_ELEMENT_TAG_ROOT = "spark-task-def";
    public static final String STREAMING_DEF_ELEMENT_TAG_ROOT = "streaming-task-def";
    public static final String INTEGRATE_DEF_ELEMENT_TAG_ROOT = "integrate-task-def";
    public static final String EXTERNAL_DEF_ELEMENT_TAG_ROOT = "external-task-def";

    public static final String SPARK_DEF_ELEMENT_TAG_INITS = "inits";
    public static final String SPARK_DEF_ELEMENT_TAG_INIT = "init";
    public static final String SPARK_DEF_ELEMENT_TAG_INPUTS = "inputs";
    public static final String SPARK_DEF_ELEMENT_TAG_INPUT = "input";
    public static final String SPARK_DEF_ELEMENT_TAG_PARAMS = "params";
    public static final String SPARK_DEF_ELEMENT_TAG_PARAM = "param";
    public static final String SPARK_DEF_ELEMENT_TAG_PREPROCESS = "preprocess";
    public static final String SPARK_DEF_ELEMENT_TAG_PROCESS = "process";
    public static final String SPARK_DEF_ELEMENT_TAG_ALGORITHMS = "algorithms";
    public static final String SPARK_DEF_ELEMENT_TAG_ALGORITHM = "algorithm";
    public static final String SPARK_DEF_ELEMENT_TAG_OUTPUTS = "outputs";
    public static final String SPARK_DEF_ELEMENT_TAG_OUTPUT = "output";
    public static final String SPARK_DEF_ELEMENT_TAG_MAIN_CLASS = "mainClass";
    public static final String SPARK_DEF_ELEMENT_TAG_JAR_PATH = "jarPath";

    public static final String  SPARK_DEF_ATTR_TAG_ID = "id";
    public static final String SPARK_DEF_ATTR_TAG_TYPE = "type";
    public static final String SPARK_DEF_ATTR_TAG_INPUTIDS = "inputIds";
    public static final String SPARK_DEF_ATTR_TAG_NAME = "name";
    public static final String SPARK_DEF_ATTR_TAG_VALUE = "value";
    public static final String SPARK_DEF_ATTR_TAG_ACTION = "action";
    public static final String SPARK_DEF_ATTR_TAG_BATCHDURATIONSECOND = "batchDurationSecond";

    // buildType的五种类型定义 init、input、preprocess、algorithm、output
    public static final String SPARK_DEF_BUILD_TYPE_INIT = "Init";
    public static final String SPARK_DEF_BUILD_TYPE_INPUT = "DataSource";
    public static final String SPARK_DEF_BUILD_TYPE_PREPROCESS = "Preprocess";
    public static final String SPARK_DEF_BUILD_TYPE_ALGORITHM = "Algorithm";
    public static final String SPARK_DEF_BUILD_TYPE_OUTPUT = "Output";
}
