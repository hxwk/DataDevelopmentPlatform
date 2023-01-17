package com.dfssi.dataplatform.chargingPile.dao;

import com.dfssi.dataplatform.chargingPile.entity.ChargeEquipmentInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.ChargeOrderInfoEntity;
import com.dfssi.dataplatform.chargingPile.entity.TwoValueResultEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/31 10:40
 */
@Repository
public interface ChargeOrderInfoDao extends BaseDao<ChargeOrderInfoEntity>{
    public List<ChargeOrderInfoEntity> listOrderInfo();
    /** 充电电量相关统计*/
    public TwoValueResultEntity chargeElecMonth();
    public TwoValueResultEntity chargeElecYear();
    public long chargeElecTotal();
    public List<TwoValueResultEntity> chargeElecDay();
    public List<TwoValueResultEntity> chargeElecTwelveMonth();
    public List<TwoValueResultEntity> chargeElecTwelveDay();
    /** 充电金额相关统计*/
    public TwoValueResultEntity chargeAmountMonth();
    public TwoValueResultEntity chargeAmountYear();
    public long chargeAmountTotal();
    public List<TwoValueResultEntity> chargeAmountDay();
    public List<TwoValueResultEntity> chargeAmountTwelveMonth();
    public List<TwoValueResultEntity> chargeAmountTwelveDay();
    /** 充电次数相关统计*/
    public TwoValueResultEntity chargeFrequencyMonth();
    public TwoValueResultEntity chargeFrequencyYear();
    public long chargeFrequencyTotal();
    public List<TwoValueResultEntity> chargeFrequencyDay();
    public List<TwoValueResultEntity> chargeFrequencyTwelveMonth();
    public List<TwoValueResultEntity> chargeFrequencyTwelveDay();
    public List<TwoValueResultEntity> areaChargeFrequency();
}
