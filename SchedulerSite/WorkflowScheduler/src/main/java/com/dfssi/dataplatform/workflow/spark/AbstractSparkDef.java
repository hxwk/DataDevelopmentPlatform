package com.dfssi.dataplatform.workflow.spark;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractSparkDef {

    protected String id;
    protected String type;
    protected String mainClass;
    protected List<Param> params = new ArrayList<Param>();
    protected List<RowParams> rowParams = new ArrayList<RowParams>();
    protected List<String> inputIds = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Param> getParams() {
        return params;
    }

    public void addParam(Param param) {
        this.params.add(param);
    }

    public void addParam(String name, String value) {
        Param param = new Param(name, value);
        this.params.add(param);
    }

    public void addParamOnlyValue(String value) {
        Param param = new Param(value);
        this.params.add(param);
    }

    public void addParamPair(String attrName, String attrValue) {
        Param param = new Param();
        param.addPair(attrName, attrValue);
        this.params.add(param);
    }

    public List<RowParams> getRowParams() {
        return rowParams;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setRowParams(List<RowParams> rowParams) {
        this.rowParams = rowParams;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<String> getInputIds() {
        return inputIds;
    }

    public void setInputIds(List<String> inputIds) {
        this.inputIds = inputIds;
    }

    public String getInputIdsStr() {
        return StringUtils.join(inputIds, ",");
    }

    public void buildParamsXmlElement(Element paramsEl) {
        if (params.size() > 0) {
            this.buildSimpleParamsEl(paramsEl);
        }
        if (rowParams.size() > 0) {
            this.buildTableParamsEl(paramsEl);
        }
    }

    private void buildSimpleParamsEl(Element paramsEl) {
        for (Param param : params) {
            param.buildXmlEl(paramsEl);
        }
    }


    private void buildTableParamsEl(Element paramsEl) {
    }

    public static class Param {

        private List<String> attrNames = new ArrayList<String>();
        private List<String> attrValues = new ArrayList<String>();

        private String elTag = SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM;
        private List<Param> subParams = new ArrayList<Param>();

        public Param() {
        }

        /**
         * build name and value param.
         *
         * @param name
         * @param value
         */
        public Param(String name, String value) {
            this.attrNames.add(SparkDefTag.SPARK_DEF_ATTR_TAG_NAME);
            this.attrValues.add(name);
            this.attrNames.add(SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE);
            this.attrValues.add(value);
        }

        /**
         * only build name
         * @param value
         */
        public Param(String value) {
            this.attrNames.add(SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE);
            this.attrValues.add(value);
        }

        public void addPair(String attrName, String attrValue) {
            this.attrNames.add(attrName);
            this.attrValues.add(attrValue);
        }

        public void buildXmlEl(Element paramsEl) {
            Element paramEl = paramsEl.addElement(this.getElTag());
            for (int i = 0; i < attrNames.size(); i++) {
                paramEl.addAttribute(attrNames.get(i), attrValues.get(i));
            }
            for (Param subParam : this.subParams) {
                subParam.buildXmlEl(paramEl);
            }
        }

        public boolean isBlankParam() {
            return attrNames.size() <= 0 && attrValues.size() <= 0;
        }

        public String getElTag() {
            return elTag;
        }

        public void setElTag(String elTag) {
            this.elTag = elTag;
        }

        public List<Param> getSubParams() {
            return subParams;
        }
    }

    public static class RowParams {
        private List<Param> params = new ArrayList<Param>();

        public RowParams() {
        }

        public void addParam(String name, String value) {
            this.params.add(new Param(name, value));
        }
    }
}
