package com.dfssi.dataplatform.external.chargingPile.service;

import com.dfssi.dataplatform.external.chargingPile.dao.*;
import com.dfssi.dataplatform.external.chargingPile.entity.*;
import com.dfssi.dataplatform.external.common.*;
import com.dfssi.dataplatform.external.model.TopicManger;
import com.google.gson.internal.LinkedTreeMap;
import net.sf.json.JSONObject;
//import org.apache.commons.io.IOUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/30 10:12
 */
@Service
public class ExternalOperatorService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    protected ChargeOperatorInfoDao ChargeOperatorInfoDao;
    @Autowired
    protected RestTemplate restTemplate;

    /**
     * @author bin.Y
     * Description:接收整个充电站的基本信息
     * Date:  2018/5/31 9:45
     */
    public Object insertNotificationStationInfo(HttpServletRequest request, String stationInfo) throws Exception {
        MessageProcess messageProcess = new MessageProcess(stationInfo, request);
        String Ret = messageProcess.executeCheck();
        if (Ret != "0") {
            return messageProcess.combParamsFail(Ret);
        }
        MessageBodyEntity messageBody = JsonUtils.fromJson(stationInfo, new MessageBodyEntity().getClass());
        String data = AESOperator.getInstance().decrypt(messageBody.getData(), messageBody.getOperatorID());
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            Map<String, Object> hashMap = JsonUtils.fromJson(data, new HashMap<String, Object>().getClass());
            JSONObject json = JSONObject.fromObject(hashMap.get("StationInfo"));
            ChargeStationInfoEntity chargeStationInfoEntity = JsonUtils.fromJson(json.toString(), new ChargeStationInfoEntity().getClass());

            if (null == chargeStationInfoEntity) {
                return messageProcess.combReturnMessage("500", "系统错误", params, messageBody.getOperatorID());
            }
            kafkaTemplate.send(TopicManger.stationInfoTopic, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            params.put("Status", 1);
            return messageProcess.combReturnMessage("500", "系统错误", params, messageBody.getOperatorID());
        }
        params.put("Status", 0);
        return messageProcess.combReturnMessage("0", "已接收", params, messageBody.getOperatorID());
    }

    /**
     * @author bin.Y
     * Description:接收接口的最新状态
     * Date:  2018/5/31 9:45
     */
    public Object notificationStationStatus(HttpServletRequest request, String connectorStatusInfo) throws Exception {

        MessageProcess messageProcess = new MessageProcess(connectorStatusInfo, request);
        String Ret = messageProcess.executeCheck();
        if (Ret != "0") {
            return messageProcess.combParamsFail(Ret);
        }
        MessageBodyEntity messageBody = JsonUtils.fromJson(connectorStatusInfo, new MessageBodyEntity().getClass());
        String data = AESOperator.getInstance().decrypt(messageBody.getData(), messageBody.getOperatorID());
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            Map<String, Object> hashMap = JsonUtils.fromJson(data, new HashMap<String, Object>().getClass());
            JSONObject json = JSONObject.fromObject(hashMap.get("ConnectorStatusInfo"));
            ChargeConnectorStatusInfoEntity connectorStatusInfoEntity = JsonUtils.fromJson(json.toString(), new ChargeConnectorStatusInfoEntity().getClass());
            if (null == connectorStatusInfoEntity) {
                return messageProcess.combReturnMessage("500", "系统错误", params, messageBody.getOperatorID());
            }
            kafkaTemplate.send(TopicManger.stationStatusTopic, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            params.put("Status", 1);
            return messageProcess.combReturnMessage("500", "系统错误", params, messageBody.getOperatorID());
        }
        params.put("Status", 0);
        return messageProcess.combReturnMessage("0", "已接收", params, messageBody.getOperatorID());
    }

    /**
     * @author bin.Y
     * Description:接收推送的订单信息
     * Date:  2018/5/31 10:41
     */
    public Object notificationOrderInfo(HttpServletRequest request, String orderInfo) throws Exception {
        MessageProcess messageProcess = new MessageProcess(orderInfo, request);
        String Ret = messageProcess.executeCheck();
        if (Ret != "0") {
            return messageProcess.combParamsFail(Ret);
        }
        MessageBodyEntity messageBody = JsonUtils.fromJson(orderInfo, new MessageBodyEntity().getClass());
        String data = AESOperator.getInstance().decrypt(messageBody.getData(), messageBody.getOperatorID());
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            Map<String, Object> hashMap = JsonUtils.fromJson(data, new HashMap<String, Object>().getClass());
            JSONObject json = JSONObject.fromObject(hashMap.get("OrderInfo"));
            ChargeOrderInfoEntity chargeOrderInfoEntity = JsonUtils.fromJson(json.toString(), new ChargeOrderInfoEntity().getClass());
            if (null == chargeOrderInfoEntity) {
                return messageProcess.combReturnMessage("500", "系统错误", params, messageBody.getOperatorID());
            }
            kafkaTemplate.send(TopicManger.orderInfoTopic, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            params.put("Status", 1);
            return messageProcess.combReturnMessage("500", "系统错误", params, messageBody.getOperatorID());
        }
        params.put("Status", 0);
        return messageProcess.combReturnMessage("0", "已接收", params, messageBody.getOperatorID());
    }

    /**
     * @author bin.Y
     * Description:查询充电站信息并入库
     * Date:  2018/5/31 13:10
     */
    public Map<String, String> queryStationsInfo(String LastQueryTime, String PageNo, String PageSize, String operatorId) throws Exception {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("LastQueryTime", LastQueryTime);
        jsonObj.put("PageNo", PageNo);
        jsonObj.put("PageSize", PageSize);
        Map<String, String> result = queryData(operatorId/*"731043872"*/, "query_stations_info", jsonObj.toString());
        logger.info("查询到充电站的数据:" + result);
        if (!result.get("Ret").equals("0")) {
            return result;
        }
        Map<String, Object> hashMap =new HashMap<String, Object>();
        int dataLength=0;
        try {
            hashMap = JsonUtils.fromJson(result.get("Data"), new HashMap<String, Object>().getClass());
            List<LinkedTreeMap<String, String>> stationInfos = (List<LinkedTreeMap<String, String>>) hashMap.get("StationInfos");
            dataLength=stationInfos.size();
            stationInfos.forEach(stationInfoMap -> {
                JSONObject stationInfo = JSONObject.fromObject(stationInfoMap);
                kafkaTemplate.send(TopicManger.stationInfoTopic, stationInfo.toString());
            });
        } catch (Exception e) {
            result.put("Ret", "1");
            result.put("Msg", e.toString());
            return result;
        }
        result.put("Ret", "0");
        result.put("Msg", "");
        result.put("Size", dataLength+"");
        return result;
    }

    /**
     * @author bin.Y
     * Description:设备接口状态查询
     * Date:  2018/5/31 14:13
     */
    public Object queryStationStatus(String[] stationInfo, String operatorId) throws Exception {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("StationIDs", stationInfo);
        Map<String, String> result = queryData(operatorId/*"731043872"*/, "query_station_status", jsonObj.toString());
        logger.info("查询到的接口数据是:" + result);
        if (!result.get("Ret").equals("0")) {
            return result;
        }
        Map<String, Object> hashMap = JsonUtils.fromJson(result.get("Data"), new HashMap<String, Object>().getClass());
        List<LinkedTreeMap<String, Object>> stationStatusInfos = (List<LinkedTreeMap<String, Object>>) hashMap.get("StationStatusInfos");
        stationStatusInfos.forEach(stationStatusMap -> {
            List<LinkedTreeMap<String, Object>> connectorStatusInfos = (List<LinkedTreeMap<String, Object>>) stationStatusMap.get("ConnectorStatusInfos");
            for (LinkedTreeMap<String, Object> info : connectorStatusInfos) {
                JSONObject json = JSONObject.fromObject(info);
                kafkaTemplate.send(TopicManger.stationStatusTopic, json.toString());
            }
        });
//        result.remove("Data");
        result.put("Ret", "0");
        result.put("Msg", "");
        return result;
    }


    public Object queryStationStats(String StationID, String StartTime, String EndTime) {
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        HttpHeaders headers = new HttpHeaders();
        postParameters.add("StationID", StationID);
        postParameters.add("StartTime", StartTime);
        postParameters.add("EndTime", EndTime);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(postParameters, headers);
        return "";
    }


    /**
     * @author bin.Y
     * Description:获取token
     * Date:  2018/6/2 13:19
     */
    public Object queryToken(String json) throws Exception {
        MessageProcess messageProcess = new MessageProcess(json, null);
        String Ret = messageProcess.executeCheck();
        if (Ret != "0") {
            return messageProcess.combParamsFail(Ret);
        }
        MessageBodyEntity messageBody = JsonUtils.fromJson(json, new MessageBodyEntity().getClass());
        String data = AESOperator.getInstance().decrypt(messageBody.getData(), messageBody.getOperatorID());
        Map<String, Object> hashMap = JsonUtils.fromJson(data, new HashMap<String, Object>().getClass());

        JSONObject jsonData = JSONObject.fromObject(data);
        String operatorId = jsonData.get("OperatorID").toString();
        String operatorSecret = jsonData.get("OperatorSecret").toString();
        List<ChargeOperatorInfoEntity> listOperator = ChargeOperatorInfoDao.seleteOperator(operatorId);
//        Jedis jedis = new RedisPoolManager().getJedis();
//        String tokenparam = jedis.get("Charge:inside:nandou");
//        jedis.close();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("OperatorID", operatorId);
        params.put("SuccStat", "0");
        params.put("AccessToken", PubGlobal.token /*tokenparam*/);
        params.put("TokenAvailableTime", 60 * 60 * 10 + "");
        params.put("FailReason", "0");
        if (listOperator.size() < 1) {
            params.put("SuccStat", "1");
            params.put("FailReason", "1");
            params.put("AccessToken", null);
            params.put("TokenAvailableTime", "0");
            return messageProcess.combReturnMessage("0", "无此运营商", params, messageBody.getOperatorID());
        }
        if (!operatorSecret.equals(PubGlobal.secretMap.get(operatorId).get("OperatorSecret"))) {
            params.put("SuccStat", "1");
            params.put("FailReason", "2");
            params.put("AccessToken", null);
            params.put("TokenAvailableTime", "0");
            return messageProcess.combReturnMessage("0", "密匙错误", params, messageBody.getOperatorID());
        }

        return messageProcess.combReturnMessage("0", "无", params, messageBody.getOperatorID());
    }


    /**
     * @author bin.Y
     * Description:获得运营商密匙，再查询数据
     * Date:  2018/6/6 13:19
     */
    public Map<String, String> queryData(String operatorId, String methodName, String params) throws Exception {
        MessageProcess process = new MessageProcess(restTemplate);
//        List<ChargeOperatorInfoEntity> listOperator = ChargeOperatorInfoDao.seleteOperator(operatorId);
        HashMap<String, String> stringStringHashMap = PubGlobal.secretMap.get(operatorId);
        Map<String, String> result = new HashMap<String, String>();
        if (stringStringHashMap.size() < 1) {
            result.put("Ret", "1");
            result.put("Msg", "无此运营商");
            return result;
        }
        String passWord = stringStringHashMap.get("OperatorSecret");
        String url = stringStringHashMap.get("url");
        result = process.queryData(operatorId, passWord, params, methodName, url);
        return result;
    }

}
