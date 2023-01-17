package com.dfssi.dataplatform.common.service;

import java.util.ArrayList;
import java.util.List;

public class CommonMenuCtr {
    private int id;
    private String name;
    private String showName;
    private String icon;
    private List children = new ArrayList();

    public CommonMenuCtr(int id, String name, String showName, String icon) {
        this.id = id;
        this.name = name;
        this.showName = showName;
        this.icon = icon;
    }

    public void addChild(Object child) {
        this.children.add(child);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }

    public CommonMenuCtr findByName(String name) {
        for (Object obj : children) {
            if (obj instanceof CommonMenuCtr && name.equalsIgnoreCase(((CommonMenuCtr) obj).getName())) {
                return (CommonMenuCtr) obj;
            }
        }

        return null;
    }
}
