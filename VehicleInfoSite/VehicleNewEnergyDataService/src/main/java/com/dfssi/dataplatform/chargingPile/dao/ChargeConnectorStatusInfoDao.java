package com.dfssi.dataplatform.chargingPile.dao;

import com.dfssi.dataplatform.chargingPile.entity.ChargeConnectorStatusInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.ChargePileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/31 9:39
 */
@Repository
public interface ChargeConnectorStatusInfoDao extends BaseDao<ChargeConnectorStatusInfoEntity>{
    public void insertAllFromStation(List<ChargeConnectorStatusInfoEntity> list);
    public void deleteAllFromStation(List<ChargeConnectorStatusInfoEntity> list);
    public long queryConnectorNum(int status);
    public List<ChargePileEntity> getChargePileTotal();
}
