package com.dfssi.dataplatform.external.chargingPile.dao;

import com.dfssi.dataplatform.external.chargingPile.entity.ChargeEquipmentInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/30 10:48
 */
@Repository
public interface ChargeEquipmentInfoDao  extends BaseDao<ChargeEquipmentInfoEntity>{
    public void insertAllFromStation(List<ChargeEquipmentInfoEntity> list, String StationID);
    public void deleteAllFromStation(String id);

}
