package com.dfssi.dataplatform.chargingPile.dao;

import com.dfssi.dataplatform.chargingPile.entity.ChargeEquipmentInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.ChargeStationInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.StationAreaEntity;
import com.dfssi.dataplatform.chargingPile.entity.TwoValueResultEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/30 10:47
 */
@Repository
public interface ChargeStationInfoDao extends BaseDao<ChargeStationInfoEntity>{
    public long countStationNumByStationType(int stationType);
    public List<StationAreaEntity> chargePileAreaCount();
    public List<ChargeStationInfoEntity> listEquipmentInfo();
    public List<TwoValueResultEntity> chargingPileUserDistribution();
    public Long totalNumberOfChargingStations();
}