package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.entity.database.EvsDetectDetail;
import com.dfssi.dataplatform.model.EvsDetectDetailModel;
import com.dfssi.dataplatform.service.EvsDetectDetailService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 11:10
 */
@Service
public class EvsDetectDetailServiceImpl implements EvsDetectDetailService {
    private final Logger logger = LoggerFactory.getLogger(EvsDetectDetailServiceImpl.class);

    @Autowired
    private EvsDetectDetailModel evsDetectDetailModel;

    @Override
    public List<EvsDetectDetail> queryAll() {
        List<EvsDetectDetail> evsDetectDetails = evsDetectDetailModel.queryAll();
        if(evsDetectDetails == null)evsDetectDetails = Lists.newArrayList();
        return evsDetectDetails;
    }

    @Override
    public List<Map<String, Object>> allBaseDetectDetail() {
        return evsDetectDetailModel.allBaseDetectDetail();
    }
}
