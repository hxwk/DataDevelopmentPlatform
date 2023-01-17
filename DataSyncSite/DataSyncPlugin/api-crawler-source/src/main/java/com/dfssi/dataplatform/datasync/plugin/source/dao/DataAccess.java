package com.dfssi.dataplatform.datasync.plugin.source.dao;

import com.dfssi.dataplatform.datasync.plugin.source.bean.OilPrice;
import com.dfssi.dataplatform.datasync.plugin.source.bean.Weather;
import com.dfssi.dataplatform.datasync.plugin.source.common.BaseCrawlerRuleParam;
import com.dfssi.dataplatform.datasync.plugin.source.common.CrawlerTopic;
import com.dfssi.dataplatform.datasync.plugin.source.common.UtilTools;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Parsing a string for properties to import to db
 * @author jianKang
 * @date 2017/11/29
 */
public class DataAccess {
    static final Logger logger = LoggerFactory.getLogger(DataAccess.class);
    Weather weather ;
    String values;
    static InputStream is;
    static String districtArea;
    private static String emptyDelimiter= StringUtils.SPACE;
    List<Weather> weatherinfos;
    List<OilPrice> oilPrices;

    private static final int step=4;

    public DataAccess() {

    }

    private void parseDataFromOilTxt() {
        oilPrices = Lists.newArrayList();
        int steps = 7;
        OilPrice oilPrice;
        is = DataAccess.class.getClassLoader().getResourceAsStream("ds-oil-2017120365863.properties");
        try {
            values = IOUtils.toString(is,"UTF-8");
                    String[] oilPriceArr = values.split(emptyDelimiter);
                    int listLength= values.split(emptyDelimiter).length;
                    int i=0,j=0;
                    while(i<listLength&&(i+steps<listLength)){
                        oilPrice = new OilPrice();
                        oilPrice.setCity(oilPriceArr[i]);
                        j++;
                        oilPrice.set_89(oilPriceArr[++i]);
                        j++;
                        oilPrice.set_92(oilPriceArr[++i]);
                        j++;
                        oilPrice.set_95(oilPriceArr[++i]);
                        j++;
                        oilPrice.set_0(oilPriceArr[++i]);
                        j++;
                        String current_oilPrice_date = oilPriceArr[++i];
                        j++;
                        String current_oilPrice_time = oilPriceArr[++i];
                        j++;
                        oilPrice.set_current_oil_time(current_oilPrice_date+" "+current_oilPrice_time);
                        if(j%steps==0) {
                            ++i;
                            oilPrices.add(oilPrice);
                        }
            }
        } catch (IOException e) {
            logger.error("parse string error,please check.",e.getMessage());
        }
    }

    /**
     * parse data from filter weather contents
     * @param contents
     */
    private void parseWeaDataFromTxt(String contents){
        weatherinfos = Lists.newArrayList();
        try {
            for(String name:contents.split("\r\n")){
                logger.info("current district is :"+name);
                if(name.split(emptyDelimiter).length==1){
                    districtArea = name;
                }else if(name.split(emptyDelimiter).length>1){
                    String[] weatheres = name.split(emptyDelimiter);
                    int listLength= name.split(emptyDelimiter).length;
                    int i=0,j=0;
                    while(i<listLength&&(i+step<listLength)){
                        weather = new Weather();
                        weather.setDistrictArea(districtArea);
                        weather.setCurrentDay(weatheres[i]);
                        j++;
                        weather.setWeather(weatheres[++i]);
                        j++;
                        weather.setWindDirector(weatheres[++i]);
                        j++;
                        weather.setWindLevel(weatheres[++i]);
                        j++;
                        weather.setIn_db_time(UtilTools.getCurrentData());
                        if(j%step==0) {
                            ++i;
                            weatherinfos.add(weather);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parse string error,please check.",e.getMessage());
        }
    }

    /**
     * get all weathers or get weather info by params
     * @return List<Weather> Weathers
     */
    public List<Weather> getWeathers(){
        List<Weather> weathers = Lists.newArrayList();

        return weathers;
    }

    public void readFileByLine(InputStream is) throws IOException {
        InputStreamReader isr=new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        logger.info(br.readLine());
        while ((line=br.readLine())!=null) {
            logger.info(line);
        }
        br.close();
        isr.close();
    }

    private static void insertWeatherByTxt(String contents){
        DataAccess da = new DataAccess();
        da.parseWeaDataFromTxt(contents);
        Weather w ;
        DBHelper dbHelper = new DBHelper();

        for(Weather weather : da.weatherinfos){
            w = new Weather(weather);
            String insert = w.insertSQL();
            dbHelper.insertAndDelete(insert);
        }
    }

    /**
     * insert into oil price to db
     */
    private static void insertOilPriceByTxt(String contents){
        DataAccess da = new DataAccess();
        da.parseDataFromOilTxt();
        OilPrice op ;
        DBHelper dbHelper = new DBHelper();
        for(OilPrice oilPrice: da.oilPrices){
            op = new OilPrice(oilPrice);
            String insert = op.insertSQL();
            dbHelper.insertAndDelete(insert);
        }
    }

    public void startTask(){
        //String sep = File.separator;
        UtilTools uts = new UtilTools();
        BaseCrawlerRuleParam bcrp = BaseCrawlerRuleParam.getInstanceConf();
        String crawlStorageFolder = bcrp.getCrawlStorageFolder();
        String inDbDatafile = bcrp.getInDbDatafile();
        String fileName = crawlStorageFolder+inDbDatafile;
        logger.info("insert db file path and name is "+fileName);
        if(inDbDatafile.toUpperCase().contains(CrawlerTopic.OIL.name())){
            String oilContents = uts.getFilterOilContent(fileName);
            insertOilPriceByTxt(oilContents);
        }else if(inDbDatafile.toUpperCase().contains(CrawlerTopic.WEA.name())){
            String contents = uts.getFilterWeaContent(fileName);
            insertWeatherByTxt(contents);
        }
    }

    public static void main(String[] args) {
        DataAccess da = new DataAccess();
        da.startTask();
    }
}
