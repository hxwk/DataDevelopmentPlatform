package com.dfssi.dataplatform.ide.access.mvc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于构建接入任务新增修改左侧树形结构
 * Created by hongs on 2018/5/3.
 */
public class CommonMenuCtrEntity implements Serializable {

    private int code;
    private String name;
    private String showName;
    private List children = new ArrayList();

    public CommonMenuCtrEntity(int code, String name, String showName) {
        this.code = code;
        this.name = name;
        this.showName = showName;
    }

    public void addChild(Object child) {
        this.children.add(child);
    }

    public int getId() {
        return code;
    }

    public void setId(int id) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }

    public CommonMenuCtrEntity findByName(String name) {
        for (Object obj : children) {
            if (obj instanceof CommonMenuCtrEntity && name.equalsIgnoreCase(((CommonMenuCtrEntity) obj).getName())) {
                return (CommonMenuCtrEntity) obj;
            }
        }
        return null;
    }
}
