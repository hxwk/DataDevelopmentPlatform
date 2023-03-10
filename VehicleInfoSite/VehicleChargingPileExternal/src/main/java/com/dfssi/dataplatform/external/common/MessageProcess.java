package com.dfssi.dataplatform.external.common;

import com.dfssi.dataplatform.external.chargingPile.entity.MessageBodyEntity;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/2 13:24
 */
public class MessageProcess {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String json;
    private MessageBodyEntity messageBody;
    private RestTemplate restTemplate;
    private  HttpServletRequest request;

    private int tokenCount=0;
    public MessageProcess() {

    }

    public MessageProcess(String json, HttpServletRequest request) {
        this.json = json;
        this.request=request;
    }
    public MessageProcess(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }
    public String executeCheck() {
        try {
            this.messageBody = JsonUtils.fromJson(json, new MessageBodyEntity().getClass());
        } catch (Exception e) {
            e.printStackTrace();
            return "4004";
        }
        String operatorID = messageBody.getOperatorID();
        String data = messageBody.getData();
        String timeStamp = messageBody.getTimeStamp();
        String seq = messageBody.getSeq();
        String sig = messageBody.getSig();

        if (operatorID == null || data == null || timeStamp == null || seq == null || sig == null) {
            return "4003";
        }
        if(request!=null){
//            Jedis jedis = new RedisPoolManager().getJedis();
//            String tokenRedis = "Bearer "+jedis.get("Charge:inside:nandou");
//            jedis.close();
            String tokenRedis ="Bearer "+PubGlobal.token;
            String tokenEntry = request.getHeader("Authorization");
            if(!tokenRedis.equals(tokenEntry)){
                return "4002";
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(operatorID).append(data).append(timeStamp).append(seq);
        String cc=builder.toString();
        String checkSig = HMacMD5.getHmacMd5Str(PubGlobal.secretMap.get("operatorID").get("SignSecret"), builder.toString());
        //??????????????????
        if (!checkSig.equals(sig)) {
            return "4001";
        }
        return "0";
    }

    /**
     * @author bin.Y
     * Description:????????????????????????
     * Date:  2018/6/2 14:11
     */
    public Object combParamsFail(String Ret) {
        JSONObject returnJson = new JSONObject();
        if (Ret.equals("4004")) {
            returnJson.put("Ret", "4004");
            returnJson.put("Msg", "?????????????????????????????????????????????????????????????????????");
        } else if (Ret.equals("4003")) {
            returnJson.put("Ret", "4003");
            returnJson.put("Msg", "POST??????????????????????????????????????????OperatorId???sig???TimeStamp???Data???Seq????????????");
        } else if (Ret.equals("4002")) {
            returnJson.put("Ret", "4002");
            returnJson.put("Msg", "Token??????");
        } else if (Ret.equals("4001")) {
            returnJson.put("Ret", "4001");
            returnJson.put("Msg", "????????????");
        } else if (Ret.equals("500")) {
            returnJson.put("Ret", "500");
            returnJson.put("Msg", "????????????");
        } else if (Ret.equals("-1")) {
            returnJson.put("Ret", "500");
            returnJson.put("Msg", "??????????????????????????????");
        }
        returnJson.put("Data", null);
        returnJson.put("Sig", null);
        return returnJson.toString();
    }

    /**
     * @author bin.Y
     * Description:??????????????????
     * Date:  2018/6/2 15:26
     */
    public Object combReturnMessage(String Ret, String Msg, Map<String, Object> Data,String OperatorID) throws Exception {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("Data", Data);
        JSONObject dataJson = JSONObject.fromObject(Data);
        JSONObject returnJson = new JSONObject();
        returnJson.put("Ret", Ret);
        returnJson.put("Msg", Msg);
        String dataTmp=AESOperator.getInstance().encrypt(dataJson.toString(),OperatorID);
        returnJson.put("Data", dataTmp);
        StringBuilder builder = new StringBuilder();
        builder.append(OperatorID).append(Msg).append(dataTmp);
        String Sig = HMacMD5.getHmacMd5Str(PubGlobal.secretMap.get(OperatorID).get("SignSecret"), builder.toString());
        returnJson.put("Sig", Sig);
        return returnJson.toString();
    }

    /**
     * @author bin.Y
     * Description:?????????????????????token
     * Date:  2018/6/4 9:40
     */
    public Map<String, String> getToken(String OperatorID, String OperatorSecret,String url) throws Exception {
        String params = "{\"OperatorID\":\"" + OperatorID + "\",\"OperatorSecret\":\"" + OperatorSecret + "\"}";//"Data":
        Map<String, Object> returnBody = sendRest(OperatorID, params, null, url);
        Map<String, String> map = new HashMap<String, String>();
        if ((Integer) returnBody.get("Ret") != 0) {
            map.put("Ret", returnBody.get("Ret") + "");
            map.put("Msg", returnBody.get("Msg").toString());
            return map;
        }
        String data = AESOperator.getInstance().decrypt(returnBody.get("Data").toString(),OperatorID);
        Map<String, Object> dataMap = JsonUtils.fromJson(data, new HashMap<String, Object>().getClass());
        if ( (double)dataMap.get("SuccStat")!=0.0 ) {
            map.put("Ret", dataMap.get("SuccStat").toString());
            map.put("Msg", dataMap.get("FailReason").toString());
            return map;
        }
        map.put("Ret", "0");
        map.put("token", dataMap.get("AccessToken").toString());
        Jedis jedis = new RedisPoolManager().getJedis();
        jedis.set("Charge:external:"+OperatorID+"token",dataMap.get("AccessToken").toString());
        jedis.close();
        return map;
    }

    /**
     * @author bin.Y
     * Description:????????????
     * Date:  2018/6/4 13:25
     */
    public Map<String, Object> sendRest(String OperatorID, String params, String token, String methodName) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String Data = AESOperator.getInstance().encrypt(params, OperatorID);
        String TimeStamp = sdf.format(new Date());
        String Seq = "0001";
//        Properties properties = loadProperties("rest.properties");
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", token == null ? "Bearer Tonken" : "Bearer " + token);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("OperatorID", OperatorID);
        jsonObj.put("Data", Data);
        jsonObj.put("TimeStamp", TimeStamp);
        jsonObj.put("Seq", Seq);
        StringBuilder builder = new StringBuilder();
        builder.append(OperatorID).append(Data).append(TimeStamp).append(Seq);
        String Sig = HMacMD5.getHmacMd5Str(PubGlobal.secretMap.get(OperatorID).get("SignSecret"), builder.toString());
        jsonObj.put("Sig", Sig);
        logger.info("??????["+methodName+"]???????????????"+jsonObj.toString());
        HttpEntity<String> requestEntity = new HttpEntity<String>(jsonObj.toString(), headers);
        Map<String, Object> object = restTemplate.postForObject(/*properties.getProperty("queryTokenUrl") + */methodName, requestEntity, LinkedHashMap.class);
        return object;
    }

    public Map<String, String> queryData(String OperatorID, String OperatorSecret, String params, String methodName,String url) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        if(tokenCount >=1 &&tokenCount <=3){
            Map<String, String> token = getToken(OperatorID, OperatorSecret,url+"query_token");
            if (!token.get("Ret").equals("0")) {
                map.put("Ret", token.get("Ret"));
                map.put("Msg", token.get("Msg"));
                return map;
            }
        }
        if(tokenCount==4){
            map.put("Ret","4002");
            map.put("Msg", "token??????3???????????????");
            return map;
        }
        Jedis jedis = new RedisPoolManager().getJedis();
        String tokenParam = jedis.get("Charge:external:"+OperatorID+"token");
        Map<String, Object> result = sendRest(OperatorID, params.toString(), tokenParam, url+methodName);

        if(result.get("Ret").toString().equals("4002")){
            tokenCount+=1;
            return queryData(OperatorID,OperatorSecret,params,methodName,url);
        }
        if (!result.get("Ret").toString().equals("0")) {
            map.put("Ret", result.get("Ret").toString());
            map.put("Msg", result.get("Msg").toString());
            return map;
        }
        String data = AESOperator.getInstance().decrypt(result.get("Data").toString(),OperatorID);
        map.put("Ret", "0");
        map.put("Data", data);
        return map;
    }

    /**
     * @author bin.Y
     * Description:
     * Date:  2018/5/31 13:26
     */
    public Properties loadProperties(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        InputStream stream = classloader.getResourceAsStream(path);
        try {
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

}
