package com.dfssi.dataplatform.datasync.plugin.source.bean;

import com.dfssi.dataplatform.datasync.plugin.source.common.UtilTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DBHelper, Weather model class
 * @author jianKang
 * @date 2017/12/01
 */
public class Weather implements IFetcherObject{
    static final Logger logger = LoggerFactory.getLogger(Weather.class);
    private String weatherid;
    private String districtArea;
    private String currentDay;
    private String weather;
    private String windDirector;
    private String windLevel;
    private String in_db_time;

    public Weather() {
    }

    public Weather(Weather w) {
        this.districtArea = w.getDistrictArea();
        this.currentDay = w.getCurrentDay();
        this.weather = w.getWeather();
        this.windDirector = w.getWindDirector();
        this.windLevel = w.getWindLevel();
        this.in_db_time = UtilTools.getCurrentData();
    }

    public String getDistrictArea() {
        return districtArea;
    }

    public void setDistrictArea(String districtArea) {
        this.districtArea = districtArea;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWindDirector() {
        return windDirector;
    }

    public void setWindDirector(String windDirector) {
        this.windDirector = windDirector;
    }

    public String getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(String windLevel) {
        this.windLevel = windLevel;
    }

    public String getIn_db_time() {
        return in_db_time;
    }

    public void setIn_db_time(String in_db_time) {
        this.in_db_time = in_db_time;
    }

    @Override
    public String insertSQL(){
        String insertsql = String.format("insert into `weather` values ('%s','%s','%s','%s','%s','%s','%s');", UtilTools.generateUUID(),this.districtArea,this.currentDay,this.weather,this.windDirector,this.windLevel,this.in_db_time);
        logger.info("insert sql is "+ insertsql);
        return insertsql;
    }

    @Override
    public String deleteObjByParam(){
        return String.format("delete from `weather` where in_db_time='%s'",this.getIn_db_time());
    }


    @Override
    public String deleteSQL(){
        return String.format("delete from `weather` where in_db_time='%s'",this.in_db_time);
    }

    @Override
    public String selectAllSQL(){
        return String.format("select city,_89,_92,_95,_0,_current_oil_time,in_db_time from `weather`");
    }

    @Override
    public String selectByParamSQL(){
        return String.format("select city,_89,_92,_95,_0,_current_oil_time,in_db_time from `weather` where in_db_time='%s'",this.in_db_time);
    }

    @Override
    public String toString() {
        return "Weather{" +
                "weatherid='" + weatherid + '\'' +
                ", districtArea='" + districtArea + '\'' +
                ", currentDay='" + currentDay + '\'' +
                ", weather='" + weather + '\'' +
                ", windDirector='" + windDirector + '\'' +
                ", windLevel='" + windLevel + '\'' +
                ", in_db_time='" + in_db_time + '\'' +
                '}';
    }
}
