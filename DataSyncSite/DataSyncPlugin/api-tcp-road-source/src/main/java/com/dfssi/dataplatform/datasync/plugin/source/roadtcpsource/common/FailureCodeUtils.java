package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.FailureCodeDTO;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @ClassName FailureCodeUtils
 * @Description TODO
 * @Author chenf
 * @Date 2018/10/9
 * @Versiion 1.0
 **/
public class FailureCodeUtils {
    private static final Logger logger = LoggerFactory.getLogger(FailureCodeUtils.class);
    /**
     * 查询故障码信息
     * @param queryStr
     * @return
     */
    private static FailureCodeDTO getFailureCode(String queryStr){
        logger.debug(" failureCodeInfo region sql: " + queryStr);
        FailureCodeDTO fcdto = null;
        Region region = null;
        try {
            region = GeodeTool.getRegeion(Constants.REGION_FAILURECODEINFO);
            Object objList = region.query(queryStr);
            if (objList instanceof ResultsBag) {
                Iterator iter = ((ResultsBag)objList).iterator();
                while (iter.hasNext()) {
                    fcdto = (FailureCodeDTO) iter.next();
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("查询geode出错", e);
        }
        return fcdto;
    }

    /**
     *根据 spn fmi sa查询故障码信息
     * @param spn
     * @param fmi
     * @param sa
     * @return
     */
    public static FailureCodeDTO getFailureCode(String spn,String fmi,String sa ) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_FAILURECODEINFO);
        sqlBuf.append(" where spn = '");
        sqlBuf.append(spn);
        sqlBuf.append("' and fmi = '");
        sqlBuf.append(fmi);
        sqlBuf.append("' and sa = '");
        sqlBuf.append(sa);
        sqlBuf.append("'  limit 1");
        FailureCodeDTO vehicle = getFailureCode(sqlBuf.toString());
        return vehicle;
    }


    public static void main(String[] args) {
        FailureCodeDTO fcdto = getFailureCode("3363","18","0x00");
        System.out.println(JSON.toJSONString(fcdto));
    }


}
