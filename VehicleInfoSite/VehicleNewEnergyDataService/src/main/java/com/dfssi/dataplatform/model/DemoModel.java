package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.annotation.DataSource;
import com.dfssi.dataplatform.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 10:37
 */
@Component
public class DemoModel {
    @Autowired
    private DemoMapper demoMapper;

    @DataSource(DataSource.MYSQL)
    public List<Map<String, Object>> getVehicle(){
        return demoMapper.findAll();
    }

    @DataSource(DataSource.GPDATA)
    public List<Map<String, Object>> getDetecteRules(){
        return demoMapper.listAll();
    }
}
