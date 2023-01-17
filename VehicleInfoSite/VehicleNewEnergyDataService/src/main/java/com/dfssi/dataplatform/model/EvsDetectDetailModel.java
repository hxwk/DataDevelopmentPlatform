package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.annotation.DataSource;
import com.dfssi.dataplatform.entity.database.EvsDetectDetail;
import com.dfssi.dataplatform.mapper.EvsDetectDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 11:05
 */
@Component
public class EvsDetectDetailModel {

    @Autowired
    private EvsDetectDetailMapper evsDetectDetailMapper;

    @DataSource
    public List<EvsDetectDetail> queryAll(){
        return evsDetectDetailMapper.queryAll();
    }

    @DataSource
    public List<Map<String, Object>> allBaseDetectDetail(){
        return evsDetectDetailMapper.allBaseDetectDetail();
    }

}
