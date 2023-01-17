package com.dfssi.spark.config.xml;

import com.dfssi.resources.ConfigDetail;

import java.util.Arrays;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/3/1 13:22
 */
public class ConfigEntity extends ConfigDetail {

    private String id;
    private String type;
    private String[] inputIds;

    public ConfigEntity(String id, String type, String[] inputIds) {
        super();
        this.id = id;
        this.type = type;
        this.inputIds = inputIds;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String[] getInputIds() {
        return inputIds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfigEntity{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", inputIds=").append(Arrays.toString(inputIds));
        sb.append(", configMap=").append(configMap);
        sb.append('}');
        return sb.toString();
    }
}
