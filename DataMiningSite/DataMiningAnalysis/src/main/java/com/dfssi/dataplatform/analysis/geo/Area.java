package com.dfssi.dataplatform.analysis.geo;

import java.io.Serializable;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/26 11:25
 */
public class Area implements Serializable {

    protected String name;
    protected String ename;
    protected String code;

    public Area(String name, String ename, String code) {
        this.name = name;
        this.ename = ename;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getEname() {
        return ename;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Area{");
        sb.append("name='").append(name).append('\'');
        sb.append(", ename='").append(ename).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
