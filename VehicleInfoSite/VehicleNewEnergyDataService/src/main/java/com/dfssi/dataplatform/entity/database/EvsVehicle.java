package com.dfssi.dataplatform.entity.database;

/**
 * Description:
 *  新能源车辆信息
 * @author LiXiaoCong
 * @version 2018/4/26 10:57
 */
public class EvsVehicle {

    private String id;
    private String lpn;
    private String vin;
    private String sim;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EvsVehicle{");
        sb.append("id='").append(id).append('\'');
        sb.append(", lpn='").append(lpn).append('\'');
        sb.append(", vin='").append(vin).append('\'');
        sb.append(", sim='").append(sim).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
