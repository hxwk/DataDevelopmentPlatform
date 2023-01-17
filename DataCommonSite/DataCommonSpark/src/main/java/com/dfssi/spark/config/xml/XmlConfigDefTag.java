package com.dfssi.spark.config.xml;

import java.io.Serializable;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/3/1 13:39
 */
public class XmlConfigDefTag implements Serializable {

    private XmlConfigDefTag(){}

    public static final String ELEMENT_TAG_DEFAULT_ROOT = "spark-task-def";

    public static final String ELEMENT_TAG_INPUTS = "inputs";
    public static final String ELEMENT_TAG_INPUT = "input";
    public static final String ELEMENT_TAG_PARAMS = "params";
    public static final String ELEMENT_TAG_PARAM = "param";

    public static final String ELEMENT_TAG_DROP = "drop";
    public static final String ELEMENT_TAG_FILTER = "filter";
    public static final String ELEMENT_TAG_FILL = "fill";

    public static final String ELEMENT_TAG_PREPROCESS = "preprocess";
    public static final String ELEMENT_TAG_PROCESS = "process";
    public static final String ELEMENT_TAG_ALGORITHMS = "algorithms";
    public static final String ELEMENT_TAG_ALGORITHM = "algorithm";
    public static final String ELEMENT_TAG_OUTPUTS = "outputs";
    public static final String ELEMENT_TAG_OUTPUT = "output";

    public static final String ATTR_TAG_ID = "id";
    public static final String ATTR_TAG_TYPE = "type";
    public static final String ATTR_TAG_INPUTIDS = "inputIds";
    public static final String ATTR_TAG_NAME = "name";
    public static final String ATTR_TAG_COLNAME = "colName";
    public static final String ATTR_TAG_VALUE = "value";
    public static final String ATTR_TAG_TOTYPE = "toType";
    public static final String ATTR_TAG_EXP = "exp";
    public static final String ATTR_TAG_TOVALUE = "toValue";
    public static final String ATTR_TAG_OPERATOR = "operator";
}
