package com.dfssi.dataplatform.ide.security.model;


import com.dfssi.dataplatform.ide.security.entity.SecurityAuth;
import com.dfssi.dataplatform.ide.security.mapper.SecurityAuthMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Description:
 *   表 security_auth的增删改查
 * @author LiXiaoCong
 * @version 2018/1/4 8:51
 */
@Service
public class SecurityAuthModels {

    private final Logger logger = LoggerFactory.getLogger(SecurityAuthModels.class);

    @Autowired
    private SecurityAuthMapper securityAuthMapper;

    public  Map<String, Object> findAll(int pageNum,
                                        int pageSize,
                                        String orderField,
                                        String orderType){

        Page<SecurityAuth> page = PageHelper.startPage(pageNum, pageSize);
        securityAuthMapper.findAll(orderField, orderType);

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", page.getTotal());
        res.put("pageSize", page.size());
        res.put("records", page.getResult());

        return res;
    }

    public  Map<String, Object> findAuthByCondition(String authname,
                                                    String authtype,
                                                    Long starttime,
                                                    Long endtime,
                                                    int pageNum,
                                                    int pageSize,
                                                    String orderField,
                                                    String orderType){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String st = null;
        if(starttime != null){
            st = simpleDateFormat.format(new Date(starttime));
        }

        String et = null;
        if(endtime != null){
            et = simpleDateFormat.format(new Date(endtime));
        }

        Page<SecurityAuth> page = PageHelper.startPage(pageNum, pageSize);
        securityAuthMapper.findByCondition(authname, authtype, st, et, orderField, orderType);

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", page.getTotal());
        res.put("pageSize", page.size());
        res.put("records", page.getResult());

        return res;
    }

    public void updateById(int id,
                           String authname,
                           String authdesc,
                           String authtype,
                           String authuser,
                           String authpassword,
                           String editor){
        securityAuthMapper.updateWithId(id, authname, authdesc, authtype, authuser, authpassword, editor);
    }

    public void add(String authname,
                    String authdesc,
                    String authtype,
                    String authuser,
                    String authpassword,
                    String creator){
        if(authuser == null)authuser = creator;
        securityAuthMapper.insert(authname, authdesc, authtype, authuser, authpassword, creator);
    }

    public void deleteById(int id){
        securityAuthMapper.delete(id);
    }

}
