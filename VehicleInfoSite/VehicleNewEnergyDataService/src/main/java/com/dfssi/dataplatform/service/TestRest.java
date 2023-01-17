package com.dfssi.dataplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/4/13 11:16
 */
@Service
public class TestRest {
    @Autowired
    RestTemplate restTemplate;

    public  HashMap<String,String> hiService(String json) {
        System.out.print("enter-2");
        String str = "{\"platfrom\":[{\"platfromLoginOut\":[{\"checkType\":\"01\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"}],\"singleCarCheck\":[{\"carVin\":\"vinxxx\",\"checkType\":\"02\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vinxxx\",\"checkType\":\"03\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vinxxx\",\"checkType\":\"04\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vinxxx\",\"checkType\":\"05\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vinxxx\",\"checkType\":\"06\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vinxxx\",\"checkType\":\"07\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vinxxx\",\"checkType\":\"08\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"}],\"manyCarCheck\":[{\"carVin\":\"vin41,vin51\",\"checkType\":\"09\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"},{\"carVin\":\"vin42,vin52\",\"checkType\":\"10\",\"beginTime\":\"2018-04-12 11:39:22\",\"endTime\":\"2018-04-12 11:39:22\"}],\"carcompanyInfo\":[{\"carcompanyId\":\"id1\",\"bespeakTime\":\"2018-04-12 11:39:22\",\"dutyPeople\":\"eason\",\"dutyPhone\":\"13966778899\",\"dutyEmail\":\"123@139.com\"}]}]}\n";
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", UUID.randomUUID().toString());
        postParameters.add("json", str);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, headers);
        HashMap<String,String> map = restTemplate.postForObject("http://localhost:8092/conformance/checkInfoReceive", requestEntity, HashMap.class);
        return map;
    }
}
