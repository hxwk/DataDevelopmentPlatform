package com.dfssi.spark.config.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/3/1 13:20
 */
public class XmlConfig implements Serializable{

    private String id;
    private String name;

    private List<ConfigEntity> inputsConfig;
    private List<ConfigEntity> processsConfig;
    private List<ConfigEntity> outputsConfig;

    public XmlConfig(String id, String name) {
        this.id = id;
        this.name = name;

        this.inputsConfig = new ArrayList<>();
        this.processsConfig = new ArrayList<>();
        this.outputsConfig = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ConfigEntity> getInputsConfig() {
        return inputsConfig;
    }

    public void setInputsConfig(List<ConfigEntity> inputsConfig) {
        if(inputsConfig != null)this.inputsConfig.addAll(inputsConfig);
    }

    public List<ConfigEntity> getProcesssConfig() {
        return processsConfig;
    }

    public void setProcesssConfig(List<ConfigEntity> processsConfig) {
        if(processsConfig != null)this.processsConfig.addAll(processsConfig);
    }

    public List<ConfigEntity> getOutputsConfig() {
        return outputsConfig;
    }

    public void setOutputsConfig(List<ConfigEntity> outputsConfig) {
        if(outputsConfig != null)this.outputsConfig.addAll(outputsConfig);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("XmlConfig{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", inputsConfig=").append(inputsConfig);
        sb.append(", processsConfig=").append(processsConfig);
        sb.append(", outputsConfig=").append(outputsConfig);
        sb.append('}');
        return sb.toString();
    }
}
