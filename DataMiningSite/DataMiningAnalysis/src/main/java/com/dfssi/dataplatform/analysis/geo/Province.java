package com.dfssi.dataplatform.analysis.geo;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Description:
 *  省份
 * @author LiXiaoCong
 * @version 2018/5/26 11:21
 */
public class Province extends Area{

    private Set<City> citySet;

    public Province(String name, String ename, String code) {
        super(name, ename, code);
        this.citySet = Sets.newHashSet();
    }

    public void addCity(City city){
        this.citySet.add(city);
        city.setProvince(this);
    }

    public Set<City> getCitySet() {
        return Sets.newHashSet(citySet);
    }

}
