package com.dfssi.dataplatform.abs.service;

import com.dfssi.dataplatform.abs.mapper.LatestKafkaRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author Weijj
 * @version 2018/9/17 9:04
 */
@Service
public class LatestDataService {

    @Autowired
    private LatestKafkaRecordDao latestKafkaRecordDao;

    public Object latest(List<String> vids) {
        return latestKafkaRecordDao.getLatestRecord(vids);
    }
}
