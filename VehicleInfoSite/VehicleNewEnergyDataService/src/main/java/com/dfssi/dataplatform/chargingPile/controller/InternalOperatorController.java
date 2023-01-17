package com.dfssi.dataplatform.chargingPile.controller;

import com.dfssi.dataplatform.chargingPile.entity.*;
import com.dfssi.dataplatform.chargingPile.service.InternalOperatorService;
import com.dfssi.dataplatform.common.Exceptions;
import com.dfssi.dataplatform.common.ResponseObj;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Description:
 *      充电桩内部交互接口
 * @author JianjunWei
 * @version 2018/06/05 17:30
 */
@Api(tags = {"充电桩内部交互接口"})
@Controller
@RequestMapping(value = "/internal")
public class InternalOperatorController {

    protected Logger logger = LoggerFactory.getLogger(InternalOperatorController.class);
    private final static String LOG_INTERNAL_CHARGING_PILE_INTERFACE = "[Internal charging pile Interface]";

    @Autowired
    private InternalOperatorService internalOperatorService;

    @ApiOperation(value = "查询充电桩位置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "查询页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", dataType = "Integer", paramType = "query")
    })
    @RequestMapping(value = "/queryEquipmentInfo", method = RequestMethod.GET)
    @ResponseBody
    public Object queryEquipmentInfo(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query equipments info...");
            PageHelper.startPage(pageNo, pageSize);
            List<ChargeStationInfoEntity> chargeStationInfoEntities = internalOperatorService.queryEquipmentInfo();

            PageInfo<ChargeStationInfoEntity> pageInfo = new PageInfo<>(chargeStationInfoEntities);
            responseObj.setTotal(pageInfo.getTotal());
            responseObj.setData(chargeStationInfoEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query equipments info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query equipments info.\n", t);
        }
        return responseObj;
    }

    @ApiOperation(value = "查询充电电量、金额等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "查询页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", dataType = "Integer", paramType = "query")
    })
    @RequestMapping(value = "/queryOrderInfoList", method = RequestMethod.GET)
    @ResponseBody
    public Object queryOrderInfoList(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "list order info...");
            PageHelper.startPage(pageNo, pageSize);
            List<ChargeOrderInfoEntity> chargeOrderInfoList = internalOperatorService.queryOrderInfoList();

            PageInfo<ChargeOrderInfoEntity> pageInfo = new PageInfo<>(chargeOrderInfoList);
            responseObj.setTotal(pageInfo.getTotal());
            responseObj.setData(chargeOrderInfoList);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list order info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to list order info.\n", t);
        }
        return responseObj;
    }

    @ApiOperation(value = "查询工作桩、故障桩、待机桩信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "查询类型,0:离线桩，1：空闲桩，2：未充电桩，3：充电中桩，4：预约锁定桩，255：故障桩", dataType = "Integer", paramType = "query")
    })
    @RequestMapping(value = "/queryConnectorStatusInfo", method = RequestMethod.GET)
    @ResponseBody
    public Object queryConnectorStatusInfo(@RequestParam("status") Integer status) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query connector status info...");
            long chargePileNum = internalOperatorService.queryConnectorNum(status);

            responseObj.addKeyVal("充电桩个数", chargePileNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query connector status info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query connector status info.\n", t);
        }
        return responseObj;
    }

    @ApiOperation(value = "统计不同类型桩的总数,查询类型,0:离线桩，1：空闲桩，2：未充电桩，3：充电中桩，4：预约锁定桩，255：故障桩")
    @RequestMapping(value = "/getChargePileTotalNumber", method = RequestMethod.GET)
    @ResponseBody
    public Object getChargePileTotalNumber() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge pile total number ...");
            HashMap<Integer, Long> chargePileMap = internalOperatorService.getChargePile();

            responseObj.setData(chargePileMap);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge pile total number.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge pile total number.\n", t);
        }
        return responseObj;
    }

    @ApiOperation(value = "查询额定功率、实时功率、时间点")
    @RequestMapping(value = "/queryTwoPowerList", method = RequestMethod.GET)
    @ResponseBody
    public Object queryTwoPowerList() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query two power info...");
            List<TwoPowerEntity> twoPowerList = internalOperatorService.queryTwoPower();

            responseObj.setData(twoPowerList);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query two power info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query two power info.\n", t);
        }
        return responseObj;
    }

    @ApiOperation(value="根据站点类型，统计该类型的个数", notes = "站点类型:1：公共 50：个人 100：公交（专用） 101：环卫（专用） 102：物流（专用） 103：出租车（专用） 104：分时租赁（专用） 105：小区共享（专用） 106：单位（专用） 255：其他")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationType", value = "填写类型代号", dataType = "Integer", paramType = "query")
    })
    @RequestMapping(value = "/countStatusNumByStationType", method = RequestMethod.GET)
    @ResponseBody
    public Object countStationNumByStationType(@RequestParam("stationType") Integer stationType) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query station num by stationType...");
            long stationNum = internalOperatorService.countStationNumByStationType(stationType);

            responseObj.addKeyVal("站点个数", stationNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query station num by stationType.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query station num by stationType.\n", t);
        }
        return responseObj;
    }

    /**
     * 充电桩运营商接入桩top10
     */
    @ApiOperation(value="实际存在的运营商top10")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topN", value = "逆序排序，取前N个值,默认为值为10", dataType = "Integer", paramType = "query")
    })
    @RequestMapping(value = "/chargePileOperatorTopN", method = RequestMethod.GET)
    @ResponseBody
    public Object chargePileOperatorTopN(@RequestParam(value = "topN", defaultValue = "10") Integer topN) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge pile operator topN...");
            List<TopOperatorEntity> topOperatorList = internalOperatorService.chargePileOperatorTopN(topN);
            responseObj.setData(topOperatorList);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge pile operator topN.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge pile operator topN.\n", t);
        }
        return responseObj;
    }

    /**
     * 充电桩所在地区总数统计
     */
    @ApiOperation(value="充电桩所在地区总数统计")
    @RequestMapping(value = "/chargePileAreaCount", method = RequestMethod.GET)
    @ResponseBody
    public Object chargePileAreaCount() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge pile area count...");
            List<StationAreaEntity> stationAreaEntities = internalOperatorService.chargePileAreaCount();
            responseObj.setData(stationAreaEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge pile area count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge pile area count.\n", t);
        }
        return responseObj;
    }

    /** ---------------------------------充电电量统计---------------------------------------*/
    /**
     * 本月充电电量
     */
    @ApiOperation(value="本月充电电量")
    @RequestMapping(value = "/chargeElecMonth", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeElecMonth() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge electricity month count...");
            TwoValueResultEntity twoValueResultEntity = internalOperatorService.chargeElecMonth();
            responseObj.setData(twoValueResultEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge electricity month count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge electricity month count.\n", t);
        }
        return responseObj;
    }

    /**
     * 今年充电电量
     */
    @ApiOperation(value="今年充电电量")
    @RequestMapping(value = "/chargeElecYear", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeElecYear() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge electricity year count...");
            TwoValueResultEntity twoValueResultEntity = internalOperatorService.chargeElecYear();
            responseObj.setData(twoValueResultEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge electricity year count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge electricity year count.\n", t);
        }
        return responseObj;
    }

    /**
     * 累计充电电量
     */
    @ApiOperation(value="累计充电电量")
    @RequestMapping(value = "/chargeElecTotal", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeElecTotal() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge electricity total...");
            long totalNum = internalOperatorService.chargeElecTotal();
            responseObj.setData(totalNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge electricity total.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge electricity total.\n", t);
        }
        return responseObj;
    }


    /**
     * 今日充电电量、昨日充电电量{按小时划分}
     */
    @ApiOperation(value="今日充电电量、昨日充电电量")
    @RequestMapping(value = "/chargeElecDay", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeElecDay() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge electricity day...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeElecDay();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge electricity day.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge electricity day.\n", t);
        }
        return responseObj;
    }

    /**
     * 近12月充电电量{按月划分}(KWh)
     */
    @ApiOperation(value="近12月充电电量(KWh)")
    @RequestMapping(value = "/chargeElecTwelveMonth", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeElecTwelveMonth() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge electricity twelve month...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeElecTwelveMonth();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge electricity twelve month.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge electricity twelve month.\n", t);
        }
        return responseObj;
    }

    /**
     * 近12天充电电量{按天划分}(KWh)
     */
    @ApiOperation(value="近12天充电电量(KWh)")
    @RequestMapping(value = "/chargeElecTwelveDay", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeElecTwelveDay() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge electricity twelve day...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeElecTwelveDay();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge electricity twelve day.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge electricity twelve day.\n", t);
        }
        return responseObj;
    }

    /** ---------------------------------充电金额统计---------------------------------------*/
    /**
     * 本月充电金额
     */
    @ApiOperation(value="本月充电金额")
    @RequestMapping(value = "/chargeAmountMonth", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeAmountMonth() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge amount month count...");
            TwoValueResultEntity twoValueResultEntity = internalOperatorService.chargeAmountMonth();
            responseObj.setData(twoValueResultEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge amount month count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge amount month count.\n", t);
        }
        return responseObj;
    }

    /**
     * 今年充电金额
     */
    @ApiOperation(value="今年充电金额")
    @RequestMapping(value = "/chargeAmountYear", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeAmountYear() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge amount year count...");
            TwoValueResultEntity twoValueResultEntity = internalOperatorService.chargeAmountYear();
            responseObj.setData(twoValueResultEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge amount year count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge amount year count.\n", t);
        }
        return responseObj;
    }

    /**
     * 累计充电金额
     */
    @ApiOperation(value="累计充电金额")
    @RequestMapping(value = "/chargeAmountTotal", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeAmountTotal() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge amount total count...");
            long totalAmount = internalOperatorService.chargeAmountTotal();
            responseObj.setData(totalAmount);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge amount total count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge amount total count.\n", t);
        }
        return responseObj;
    }

    /**
     * 今日充电金额、昨日充电金额{按小时划分}
     */
    @ApiOperation(value="今日充电金额、昨日充电金额")
    @RequestMapping(value = "/chargeAmountDay", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeAmountDay() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge amount day count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeAmountDay();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge amount day count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge amount day count.\n", t);
        }
        return responseObj;
    }

    /**
     * 近12月充值金额{按月划分}(KWh)
     */
    @ApiOperation(value="近12月充值金额(KWh)")
    @RequestMapping(value = "/chargeAmountTwelveMonth", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeAmountTwelveMonth() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge amount twelve month count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeAmountTwelveMonth();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge amount twelve month count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge amount twelve month count.\n", t);
        }
        return responseObj;
    }

    /**
     * 近12天充值金额{按天划分}(KWh)
     */
    @ApiOperation(value="近12天充值金额(KWh)")
    @RequestMapping(value = "/chargeAmountTwelveDay", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeAmountTwelveDay() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge amount twelve day count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeAmountTwelveDay();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge amount twelve day count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge amount twelve day count.\n", t);
        }
        return responseObj;
    }

    /** ---------------------------------充电次数统计---------------------------------------*/
    /**
     * 本月充电次数
     */
    @ApiOperation(value="本月充电次数")
    @RequestMapping(value = "/chargeFrequencyMonth", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeFrequencyMonth() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge frequency month count...");
            TwoValueResultEntity twoValueResultEntity = internalOperatorService.chargeFrequencyMonth();
            responseObj.setData(twoValueResultEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge frequency month count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge frequency month count.\n", t);
        }
        return responseObj;
    }

    /**
     * 今年充电次数
     */
    @ApiOperation(value="今年充电次数")
    @RequestMapping(value = "/chargeFrequencyYear", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeFrequencyYear() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge frequency year count...");
            TwoValueResultEntity twoValueResultEntity = internalOperatorService.chargeFrequencyYear();
            responseObj.setData(twoValueResultEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge frequency year count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge frequency year count.\n", t);
        }
        return responseObj;
    }

    /**
     * 累计充电次数
     */
    @ApiOperation(value="累计充电次数")
    @RequestMapping(value = "/chargeFrequencyTotal", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeFrequencyTotal() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge frequency total count...");
            long totalNum = internalOperatorService.chargeFrequencyTotal();
            responseObj.setData(totalNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge frequency total count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge frequency total count.\n", t);
        }
        return responseObj;
    }


    /**
     * 今日充电次数、昨日充电次数{按小时划分}
     */
    @ApiOperation(value="今日充电次数、昨日充电次数")
    @RequestMapping(value = "/chargeFrequencyDay", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeFrequencyDay() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge frequency day count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeFrequencyDay();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge frequency day count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge frequency day count.\n", t);
        }
        return responseObj;
    }

    /**
     * 近12月充电次数{按月划分}(KWh)
     */
    @ApiOperation(value="近12月充电次数(KWh)")
    @RequestMapping(value = "/chargeFrequencyTwelveMonth", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeFrequencyTwelveMonth() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge frequency twelve month count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeFrequencyTwelveMonth();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge frequency twelve month count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge frequency twelve month count.\n", t);
        }
        return responseObj;
    }

    /**
     * 近12天充电次数{按天划分}(KWh)
     */
    @ApiOperation(value="近12天充电次数(KWh)")
    @RequestMapping(value = "/chargeFrequencyTwelveDay", method = RequestMethod.GET)
    @ResponseBody
    public Object chargeFrequencyTwelveDay() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charge frequency twelveDay count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargeFrequencyTwelveDay();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charge frequency twelveDay count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charge frequency twelveDay count.\n", t);
        }
        return responseObj;
    }

    /**
     * 区域充电次数分析
     */
    @ApiOperation(value="区域充电次数分析")
    @RequestMapping(value = "/areaChargeFrequency", method = RequestMethod.GET)
    @ResponseBody
    public Object areaChargeFrequency() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query area charge frequency count...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.areaChargeFrequency();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query area charge frequency count.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query area charge frequency count.\n", t);
        }
        return responseObj;
    }

    /**
     * 查询充电设备类型
     */
    @ApiOperation(value="查询充电设备类型,1：直流设备 2：交流设备 3：交直流一体设备")
    @RequestMapping(value = "/selectChargingDeviceType", method = RequestMethod.GET)
    @ResponseBody
    public Object selectChargingDeviceType() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charging device type...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.selectChargingDeviceType();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charging device type.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charging device type.\n", t);
        }
        return responseObj;
    }

    /**
     * 充电桩用户领域分布
     */
    @ApiOperation(value="充电桩用户领域分布，1：公共，50：个人，100：专用，255：其他")
    @RequestMapping(value = "/chargingPileUserDistribution", method = RequestMethod.GET)
    @ResponseBody
    public Object chargingPileUserDistribution() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query charging pile user distribution...");
            List<TwoValueResultEntity> twoValueResultEntities = internalOperatorService.chargingPileUserDistribution();
            responseObj.setData(twoValueResultEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query charging pile user distribution.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query charging pile user distribution.\n", t);
        }
        return responseObj;
    }

    /** ------------------------------充电桩综合接口------------------------------------------ */
    /**
     * 接入运营商总数
     */
    @ApiOperation(value="接入运营商总数，单位：家")
    @RequestMapping(value = "/totalNumberOfAccessOperators", method = RequestMethod.GET)
    @ResponseBody
    public Object totalNumberOfAccessOperators() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query total number of access operators...");
            Integer totalNum = internalOperatorService.totalNumberOfAccessOperators();
            responseObj.addKeyVal("accessOperators", totalNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query total number of access operators.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query total number of access operators.\n", t);
        }
        return responseObj;
    }

    /**
     * 充电站点总数
     */
    @ApiOperation(value="充电站点总数，单位：个")
    @RequestMapping(value = "/totalNumberOfChargingStations", method = RequestMethod.GET)
    @ResponseBody
    public Object totalNumberOfChargingStations() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query total number of charging stations...");
            long totalNum = internalOperatorService.totalNumberOfChargingStations();
            responseObj.addKeyVal("chargingStations", totalNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query total number of charging stations.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query total number of charging stations.\n", t);
        }
        return responseObj;
    }

    /**
     * 充电桩总数
     */
    @ApiOperation(value="充电桩总数，单位：个")
    @RequestMapping(value = "/totalNumberOfChargingPiles", method = RequestMethod.GET)
    @ResponseBody
    public Object totalNumberOfChargingPiles() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query total number of charging piles...");
            long totalNum = internalOperatorService.totalNumberOfChargingPiles();
            responseObj.addKeyVal("chargingPiles", totalNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query total number of charging piles.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query total number of charging piles.\n", t);
        }
        return responseObj;
    }

    /**
     * 充电枪头总数
     */
    @ApiOperation(value="充电枪头总数，单位：个")
    @RequestMapping(value = "/totalNumberOfChargingTips", method = RequestMethod.GET)
    @ResponseBody
    public Object totalNumberOfChargingTips() throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query total number of charging tips...");
            long totalNum = internalOperatorService.totalNumberOfChargingTips();
            responseObj.addKeyVal("chargingTips", totalNum);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query total number of charging tips.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query total number of charging tips.\n", t);
        }
        return responseObj;
    }

    /** ------------------------------运营商信息更新与查询------------------------------------ */
    /**
     * 更新运营商信息
     */
    @ApiOperation(value="更新运营商信息")
    @RequestMapping(value = "/updateOperatorInfo", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chargeOperatorInfoEntity", value = "运营商id和需要更新的运营商信息", required = true, dataType = "ChargeOperatorInfoEntity", paramType = "body")
    })
    @ResponseBody
    public Object updateOperatorInfo(@RequestBody ChargeOperatorInfoEntity chargeOperatorInfoEntity) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "update operator info ...");
            internalOperatorService.updateOperatorInfo(chargeOperatorInfoEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_UPDATE_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to update operator info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to update operator info.\n", t);
        }
        return responseObj;
    }

    /**
     * 新增运营商信息
     */
    @ApiOperation(value="新增运营商信息")
    @RequestMapping(value = "/insertOperatorInfo", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chargeOperatorInfoEntity", value = "新增运营商信息，运营商id必须传入", required = true, dataType = "ChargeOperatorInfoEntity", paramType = "body")
    })
    @ResponseBody
    public Object insertOperatorInfo(@RequestBody ChargeOperatorInfoEntity chargeOperatorInfoEntity) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "insert operator info ...");
            internalOperatorService.insertOperatorInfo(chargeOperatorInfoEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_ADD_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to insert operator info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to insert operator info.\n", t);
        }
        return responseObj;
    }

    /**
     * 根据运营商id查询运营商密钥
     */
    @ApiOperation(value="根据运营商id查询运营商密钥")
    @RequestMapping(value = "/selectPassWord", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operatorID", value = "运营商id", dataType = "String", paramType = "query")
    })
    @ResponseBody
    public Object selectPassWord(@RequestParam String operatorID) throws Exception {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "query pass word info ...");
            PassWordEntity passWordEntity = internalOperatorService.selectPassWord(operatorID);
            responseObj.setData(passWordEntity);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to query pass word info.", Exceptions.getStackTraceAsString(t));
            logger.error(LOG_INTERNAL_CHARGING_PILE_INTERFACE + "Failed to query pass word info.\n", t);
        }
        return responseObj;
    }
}