package com.dfssi.dataplatform.chargingPile.service;

import com.dfssi.dataplatform.chargingPile.dao.*;
import com.dfssi.dataplatform.chargingPile.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * Description:
 *  充电桩内部接口服务类
 * @author JianjunWei
 * @version 2018/06/05 17:32
 */

@Service
public class InternalOperatorService {

    @Autowired
    private ChargeEquipmentInfoDao chargeEquipmentInfoDao;
    @Autowired
    private ChargeOrderInfoDao chargeOrderInfoDao;
    @Autowired
    private ChargeConnectorStatusInfoDao chargeConnectorStatusInfoDao;
    @Autowired
    private ChargeConnectorInfoDao chargeConnectorInfoDao;
    @Autowired
    private ChargeStationInfoDao chargeStationInfoDao;
    @Autowired
    private ChargeOperatorInfoDao chargeOperatorInfoDao;

    /**
     * 查询充电设备信息
     */
    public List<ChargeStationInfoEntity> queryEquipmentInfo() {
        List<ChargeStationInfoEntity> chargeStationInfoEntities = chargeStationInfoDao.listEquipmentInfo();
        return chargeStationInfoEntities;
    }

    /**
     * 查询充电电量、金额等订单信息
     */
    public List<ChargeOrderInfoEntity> queryOrderInfoList() {
        return chargeOrderInfoDao.listOrderInfo();
    }

    /**
     * 查询工作桩、待机桩、故障桩、预约锁定充电桩个数
     */
    public long queryConnectorNum(int status) {
        return chargeConnectorStatusInfoDao.queryConnectorNum(status);
    }

    /**
     * 统计不同类型充电桩的个数
     */
    public HashMap<Integer, Long> getChargePile() {
        List<ChargePileEntity> chargePileTotal = chargeConnectorStatusInfoDao.getChargePileTotal();
        HashMap<Integer, Long> chargePileMap = new HashMap<Integer, Long>();
        for (ChargePileEntity chargePile : chargePileTotal) {
            chargePileMap.put(chargePile.getStatus(), chargePile.getTotal());
        }
        return chargePileMap;
    }

    /**
     *  查询额定功率、实时功率
     */
    public List<TwoPowerEntity> queryTwoPower() {
        return chargeConnectorInfoDao.queryTwoPower();
    }

    /**
     * 根据充电站类型查询充电桩个数
     */
    public long countStationNumByStationType(int stationType) {
        return chargeStationInfoDao.countStationNumByStationType(stationType);
    }

    /**
     * 查询充电桩运营商的TopN('N'：默认为10)
     */
    public List<TopOperatorEntity> chargePileOperatorTopN(int topN) {
        return chargeOperatorInfoDao.chargePileOperatorTopN(topN);
    }

    /**
     * 充电桩所在地区总数统计
     */
    public List<StationAreaEntity> chargePileAreaCount() {
        return chargeStationInfoDao.chargePileAreaCount();
    }

    /** ---------------------------------充电电量统计---------------------------------------*/
    /**
     * 本月充电电量
     */
    public TwoValueResultEntity chargeElecMonth() {
        return chargeOrderInfoDao.chargeElecMonth();
    }

    /**
     * 今年充电电量
     */
    public TwoValueResultEntity chargeElecYear() {
        return chargeOrderInfoDao.chargeElecYear();
    }

    /**
     * 累计充电电量
     */
    public long chargeElecTotal() {
        return chargeOrderInfoDao.chargeElecTotal();
    }

    /**
     * 今日充电电量、昨日充电电量{按小时划分}
     */
    public List<TwoValueResultEntity> chargeElecDay() {
        return chargeOrderInfoDao.chargeElecDay();
    }

    /**
     * 近12月充电量{按月划分}(KWh)
     */
    public List<TwoValueResultEntity> chargeElecTwelveMonth() {
        return chargeOrderInfoDao.chargeElecTwelveMonth();
    }

    /**
     * 近12天充电量{按天划分}(KWh)
     */
    public List<TwoValueResultEntity> chargeElecTwelveDay() {
        return chargeOrderInfoDao.chargeElecTwelveDay();
    }

    /** ---------------------------------充电金额统计---------------------------------------*/
    /**
     * 本月充电金额
     */
    public TwoValueResultEntity chargeAmountMonth() {
        return chargeOrderInfoDao.chargeAmountMonth();
    }

    /**
     * 今年充电金额
     */
    public TwoValueResultEntity chargeAmountYear() {
        return chargeOrderInfoDao.chargeAmountYear();
    }

    /**
     * 累计充电金额
     */
    public long chargeAmountTotal() {
        return chargeOrderInfoDao.chargeAmountTotal();
    }

    /**
     * 今日充电金额、昨日充电金额{按小时划分}
     */
    public List<TwoValueResultEntity> chargeAmountDay() {
        return chargeOrderInfoDao.chargeAmountDay();
    }

    /**
     * 近12月充金额{按月划分}(KWh)
     */
    public List<TwoValueResultEntity> chargeAmountTwelveMonth() {
        return chargeOrderInfoDao.chargeAmountTwelveMonth();
    }

    /**
     * 近12天充金额{按天划分}(KWh)
     */
    public List<TwoValueResultEntity> chargeAmountTwelveDay() {
        return chargeOrderInfoDao.chargeAmountTwelveDay();
    }

    /** ---------------------------------充电次数统计---------------------------------------*/
    /**
     * 本月充电次数
     */
    public TwoValueResultEntity chargeFrequencyMonth() {
        return chargeOrderInfoDao.chargeFrequencyMonth();
    }

    /**
     * 今年充电次数
     */
    public TwoValueResultEntity chargeFrequencyYear() {
        return chargeOrderInfoDao.chargeFrequencyYear();
    }

    /**
     * 累计充电次数
     */
    public long chargeFrequencyTotal() {
        return chargeOrderInfoDao.chargeFrequencyTotal();
    }

    /**
     * 今日充电金额、昨日充电次数按小时划分}
     */
    public List<TwoValueResultEntity> chargeFrequencyDay() {
        return chargeOrderInfoDao.chargeFrequencyDay();
    }

    /**
     * 近12月充次数{按月划分}(KWh)
     */
    public List<TwoValueResultEntity> chargeFrequencyTwelveMonth() {
        return chargeOrderInfoDao.chargeFrequencyTwelveMonth();
    }

    /**
     * 近12天充次数{按天划分}(KWh)
     */
    public List<TwoValueResultEntity> chargeFrequencyTwelveDay() {
        return chargeOrderInfoDao.chargeFrequencyTwelveDay();
    }

    /**
     * 区域充电次数分析
     */
    public List<TwoValueResultEntity> areaChargeFrequency() {
        return chargeOrderInfoDao.areaChargeFrequency();
    }

    /**
     * 查询充电设备类型
     */
    public List<TwoValueResultEntity> selectChargingDeviceType() {
        return chargeEquipmentInfoDao.selectChargingDeviceType();
    }

    /**
     * 充电桩用户领域分布
     */
    public List<TwoValueResultEntity> chargingPileUserDistribution() {
        return chargeStationInfoDao.chargingPileUserDistribution();
    }

    /** ------------------------------充电桩综合接口------------------------------------------ */
    /**
     * 接入运营商总数
     */
    public Integer totalNumberOfAccessOperators() {
        return chargeOperatorInfoDao.totalNumberOfAccessOperators();
    }

    /**
     * 充电站点总数
     */
    public Long totalNumberOfChargingStations() {
        return chargeStationInfoDao.totalNumberOfChargingStations();
    }

    /**
     * 充电桩总数
     */
    public Long totalNumberOfChargingPiles() {
        return chargeEquipmentInfoDao.totalNumberOfChargingPiles();
    }

    /**
     * 充电枪头总数
     */
    public Long totalNumberOfChargingTips() {
        return chargeConnectorInfoDao.totalNumberOfChargingTips();
    }

    /** ------------------------------运营商信息更新与查询------------------------------------ */
    /**
     * 更新运营商信息
     */
    public void updateOperatorInfo(ChargeOperatorInfoEntity chargeOperatorInfoEntity) {
        chargeOperatorInfoDao.updateOperatorInfo(chargeOperatorInfoEntity);
    }

    /**
     * 新增运营商信息
     */
    public void insertOperatorInfo(ChargeOperatorInfoEntity chargeOperatorInfoEntity) {
        chargeOperatorInfoDao.insert(chargeOperatorInfoEntity);
    }

    /**
     * 根据运营商id查询运营商密钥
     */
    public PassWordEntity selectPassWord(String operatorID) {
        return chargeOperatorInfoDao.selectPassWord(operatorID);
    }
}