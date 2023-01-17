package com.dfssi.dataplatform.chargingPile.dao;

import com.dfssi.dataplatform.chargingPile.entity.ChargeOperatorInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.PassWordEntity;
import com.dfssi.dataplatform.chargingPile.entity.TopOperatorEntity;
import org.elasticsearch.action.search.SearchTask;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/2 14:24
 */
@Repository
public interface ChargeOperatorInfoDao extends BaseDao<ChargeOperatorInfoEntity> {
    public List<ChargeOperatorInfoEntity> seleteOperator(String OperatorID);
    public List<TopOperatorEntity> chargePileOperatorTopN(int topN);
    public void updateOperatorInfo(ChargeOperatorInfoEntity chargeOperatorInfoEntity);
    public PassWordEntity selectPassWord(String operatorID);
    public Integer totalNumberOfAccessOperators();
}
