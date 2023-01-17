package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.service.TestRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/4/13 11:23
 */
@RestController
public class HelloController {

    @Autowired
    TestRest testRest;
    @RequestMapping(value = "/hi",method = RequestMethod.GET )
    @ResponseBody
    public  HashMap<String,String> hi(@RequestParam("json") String json){
        System.out.print("enter");
        if(json.equals("2")){
            return testRest.hiService(json);
        }else{
            return null;
        }
    }
}
