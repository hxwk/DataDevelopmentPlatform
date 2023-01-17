package com.dfssi.dataplatform.analysis.geo;

/**
 * Description:
 *   区域
 * @author LiXiaoCong
 * @version 2018/5/26 11:23
 */
public class District extends Area{

    private City city;

    public District(String name, String ename, String code) {
        super(name, ename, code);
    }

    void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }


}
