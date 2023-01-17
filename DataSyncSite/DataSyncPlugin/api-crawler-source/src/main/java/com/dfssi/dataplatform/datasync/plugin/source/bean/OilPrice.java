package com.dfssi.dataplatform.datasync.plugin.source.bean;

import com.dfssi.dataplatform.datasync.plugin.source.common.UtilTools;

/**
 * oil price model
 * @author jianKang
 * @date 2017/11/29
 */
public class OilPrice implements IFetcherObject{
    private String city;
    private String _89;
    private String _92;
    private String _95;
    private String _0;
    private String _current_oil_time;
    private String in_db_time;

    public OilPrice(OilPrice oilPrice) {
        this.city = oilPrice.getCity();
        this._89 = oilPrice.get_89();
        this._92 = oilPrice.get_92();
        this._95 = oilPrice.get_95();
        this._0 = oilPrice.get_0();
        this._current_oil_time=oilPrice.get_current_oil_time();
        in_db_time = UtilTools.getCurrentData();
    }

    public OilPrice() {
    }

    public OilPrice(String in_db_time){
        this.in_db_time = in_db_time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String get_89() {
        return _89;
    }

    public void set_89(String _89) {
        this._89 = _89;
    }

    public String get_92() {
        return _92;
    }

    public void set_92(String _92) {
        this._92 = _92;
    }

    public String get_95() {
        return _95;
    }

    public void set_95(String _95) {
        this._95 = _95;
    }

    public String get_0() {
        return _0;
    }

    public void set_0(String _0) {
        this._0 = _0;
    }

    public String getIn_db_time() {
        return in_db_time;
    }

    public void setIn_db_time(String in_db_time) {
        this.in_db_time = in_db_time;
    }

    public String get_current_oil_time() {
        return _current_oil_time;
    }

    public void set_current_oil_time(String _current_oil_time) {
        this._current_oil_time = _current_oil_time;
    }

    @Override
    public String insertSQL(){
        return String.format("insert into `oilprice` values ('%s','%s','%s','%s','%s','%s','%s','%s');", UtilTools.generateUUID(),this.city,this._89,this._92,this._95,this._0,this._current_oil_time,UtilTools.getCurrentData());
    }

    @Override
    public String deleteSQL(){
        return String.format("delete from `oilprice` where in_db_time=%s",this.in_db_time);
    }

    @Override
    public String deleteObjByParam(){
        return String.format("delete from `oilprice` where in_db_time=%s",this.in_db_time);
    }

    @Override
    public String selectAllSQL(){
        return String.format("select city,_89,_92,_95,_0,_current_oil_time,in_db_time from `oilprice`");
    }

    @Override
    public String selectByParamSQL(){
        return String.format("select city,_89,_92,_95,_0,_current_oil_time,in_db_time from `oilprice` where in_db_time=%s",this.in_db_time);
    }

    @Override
    public String toString() {
        return "OilPrice{" +
                "city='" + city + '\'' +
                ", 89# 油价'" + _89 + '\'' +
                ", 92# 油价'" + _92 + '\'' +
                ", 95# 油价'" + _95 + '\'' +
                ", 0# 柴油油价'" + _0 + '\'' +
                ", 当前油价更新时间='" + _current_oil_time + '\'' +
                ", 入库时间='" + in_db_time + '\'' +
                '}';
    }
}
