package com.dfssi.dataplatform.analysis.geo;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Description:
 *   城市
 * @author LiXiaoCong
 * @version 2018/5/26 11:22
 */
public class City extends Area{

    private Province province;

    private String citycode;
    private String tel;
    private Set<District> districtSet;

    public City(String name, String ename, String code) {
        super(name, ename, code);
        this.districtSet = Sets.newHashSet();
    }

    public void addDistrict(District district){
        this.districtSet.add(district);
        district.setCity(this);
    }

    public Set<District> getDistrictSet() {
        return Sets.newHashSet(districtSet);
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Province getProvince() {
        return province;
    }

    void setProvince(Province province) {
        this.province = province;
    }


}
