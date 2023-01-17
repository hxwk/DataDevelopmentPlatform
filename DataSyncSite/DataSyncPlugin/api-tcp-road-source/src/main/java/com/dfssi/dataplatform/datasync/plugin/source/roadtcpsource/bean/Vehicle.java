package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.bean;

public class Vehicle {

    private String id;

    private String did;

    private String sim;

    private String lpn;

    private Byte col;

    private String vin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public Byte getCol() {
        return col;
    }

    public void setCol(Byte col) {
        this.col = col;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", did='" + did + '\'' +
                ", sim='" + sim + '\'' +
                ", lpn='" + lpn + '\'' +
                ", col=" + col +
                ", vin='" + vin + '\'' +
                '}';
    }
}