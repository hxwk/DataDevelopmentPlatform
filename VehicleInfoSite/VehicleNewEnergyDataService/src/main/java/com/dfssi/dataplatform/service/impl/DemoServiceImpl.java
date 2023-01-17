package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.model.DemoModel;
import com.dfssi.dataplatform.service.DemoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 10:23
 */
@Service
public class DemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Autowired
    private DemoModel demoModel;

    @Override
    public Map<String, Object> getVehicle(int pageNum, int pageSize) {

        Page<Map<String, Object>> page = PageHelper.startPage(pageNum, pageSize);
        demoModel.getVehicle();

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", page.getTotal());
        res.put("pageSize", page.size());
        res.put("records", page.getResult());

        return res;
    }

    @Override
    public Map<String, Object> getDetecteRules(int pageNum, int pageSize) {
        Page<Map<String, Object>> page = PageHelper.startPage(pageNum, pageSize);
        demoModel.getDetecteRules();

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", page.getTotal());
        res.put("pageSize", page.size());
        res.put("records", page.getResult());
        return res;
    }
}
