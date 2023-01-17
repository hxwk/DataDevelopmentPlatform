package com.dfssi.dataplatform.service;

import com.dfssi.dataplatform.entity.database.EvsDetectDetail;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *     greenplum 车辆数据质量检测规则信息查询
 * @author LiXiaoCong
 * @version 2018/4/26 11:09
 */
public interface EvsDetectDetailService {
    List<EvsDetectDetail> queryAll();

    List<Map<String, Object>> allBaseDetectDetail();
}
