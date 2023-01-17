package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.bean;

public class Sim {


    private String id;

    private String sim_number;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSim_number() {
        return sim_number;
    }

    public void setSim_number(String sim_number) {
        this.sim_number = sim_number;
    }

    @Override
    public String toString() {
        return "Sim{" +
                "id='" + id + '\'' +
                ", sim_number='" + sim_number + '\'' +
                '}';
    }
}