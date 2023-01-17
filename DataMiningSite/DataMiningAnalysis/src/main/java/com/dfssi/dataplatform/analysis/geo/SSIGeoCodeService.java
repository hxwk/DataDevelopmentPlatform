package com.dfssi.dataplatform.analysis.geo;

import com.dfssi.common.json.Jsons;
import com.dfssi.common.net.Https;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/26 10:51
 */
public class SSIGeoCodeService implements Serializable {

    private transient Logger logger;

    private String geoServerUrl;

    public SSIGeoCodeService(String geoServerUrl){
        this.geoServerUrl = geoServerUrl;
    }

    public District rgeoCode(Object longitude,
                         Object latitude) {

        String url = createRGeoCodeUrl(longitude, latitude);
        try {
            String s = Https.get(url);
            s = s.substring(s.indexOf("]=") + 2);

            Map<String, Object> map = Jsons.toMap(s);
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
            Map<String, Object> res = list.get(0);

            Map<String, String> provinceMap = (Map<String, String>) res.get("province");
            Province province = new Province(provinceMap.get("name"), provinceMap.get("ename"), provinceMap.get("code"));

            Map<String, String> cityMap = (Map<String, String>) res.get("city");
            City city = new City(cityMap.get("name"), cityMap.get("ename"), cityMap.get("code"));
            city.setCitycode(cityMap.get("citycode"));
            city.setTel(cityMap.get("tel"));

            Map<String, String> districtMap = (Map<String, String>) res.get("district");
            District district = new District(districtMap.get("name"), districtMap.get("ename"), districtMap.get("code"));

            city.addDistrict(district);
            province.addCity(city);

            return district;
        } catch (Exception e) {
            getLogger().error(String.format("[%s,%s]获取行政区划失败。", longitude, latitude), e);
        }

        return null;
    }

    private String createRGeoCodeUrl(Object longitude,
                                     Object latitude){
        return String.format("%s&resType=json&encode=utf-8&range=0&roadnum=0&crossnum=0&poinum=0&retvalue=0&retvalue=0&region=%s,%s",
                geoServerUrl, longitude, latitude);
    }

    private Logger getLogger(){
        if(logger == null){
            logger = LoggerFactory.getLogger(SSIGeoCodeService.class);
        }
        return logger;
    }

    public static void main(String[] args) {
        SSIGeoCodeService ssiGeoCodeService
                = new SSIGeoCodeService("http://192.168.1.220:8081/rgeocode/simple?key=59902a0ea0cf2b1864b3a1f75664b9406df3316e934385f53d1bd0c1b8493e44d0dfd4c8e88a04bb&sid=7001&rid=97942");

        District district = ssiGeoCodeService.rgeoCode(0, 0);
        System.out.println(district);

        City city = district.getCity();
        System.out.println(city);
        System.out.println(city.getProvince());
    }

}
