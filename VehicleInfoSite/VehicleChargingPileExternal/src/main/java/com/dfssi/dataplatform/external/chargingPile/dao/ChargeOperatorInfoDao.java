package com.dfssi.dataplatform.external.chargingPile.dao;

import com.dfssi.dataplatform.external.chargingPile.entity.ChargeOperatorInfoEntity;
import com.dfssi.dataplatform.external.chargingPile.entity.TopOperatorEntity;
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
}
