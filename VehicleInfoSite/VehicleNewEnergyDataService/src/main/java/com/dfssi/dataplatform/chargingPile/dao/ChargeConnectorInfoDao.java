package com.dfssi.dataplatform.chargingPile.dao;

import com.dfssi.dataplatform.chargingPile.entity.ChargeConnectorInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.TwoPowerEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/30 10:48
 */
@Repository
public interface ChargeConnectorInfoDao extends BaseDao<ChargeConnectorInfoEntity>{
    public void insertAllFromStation(List<ChargeConnectorInfoEntity> list,String EquipmentID);
    public void deleteAllFromStation(String id);
    public List<TwoPowerEntity> queryTwoPower();
    public Long totalNumberOfChargingTips();
}
