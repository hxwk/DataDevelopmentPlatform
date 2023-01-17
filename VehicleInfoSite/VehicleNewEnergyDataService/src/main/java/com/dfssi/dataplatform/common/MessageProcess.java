package com.dfssi.dataplatform.common;

import com.dfssi.dataplatform.chargingPile.entity.MessageBodyEntity;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
            Jedis jedis = new RedisPoolManager().getJedis();
            String tokenRedis = jedis.get("Charge:inside:nandou");
            String tokenEntry = request.getHeader("Authorization");
            if(!tokenRedis.equals(tokenEntry)){
                return "4002";
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(operatorID).append(data).append(timeStamp).append(seq);
        String checkSig = HMacMD5.getHmacMd5Str(PubGlobal.SignSecret, builder.toString());
        //判断签名错误
        if (!checkSig.equals(sig)) {
            return "4001";
        }
        return "0";
    }

    /**
     * @author bin.Y
     * Description:组装请求失败报文
     * Date:  2018/6/2 14:11
     */
    public Object combParamsFail(String Ret) {
        JSONObject returnJson = new JSONObject();
        if (Ret.equals("4004")) {
            returnJson.put("Ret", "4004");
            returnJson.put("Msg", "请求的业务参数不合法，各接口定义自己的必须参数");
        } else if (Ret.equals("4003")) {
            returnJson.put("Ret", "4003");
            returnJson.put("Msg", "POST参数不合法，缺少必须的示例：OperatorId、sig、TimeStamp、Data、Seq五个参数");
        } else if (Ret.equals("4002")) {
            returnJson.put("Ret", "4002");
            returnJson.put("Msg", "Token错误");
        } else if (Ret.equals("4001")) {
            returnJson.put("Ret", "4001");
            returnJson.put("Msg", "签名错误");
        } else if (Ret.equals("500")) {
            returnJson.put("Ret", "500");
            returnJson.put("Msg", "系统错误");
        } else if (Ret.equals("-1")) {
            returnJson.put("Ret", "500");
            returnJson.put("Msg", "系统繁忙，请稍后再试");
        }
        returnJson.put("Data", null);
        returnJson.put("Sig", null);
        return returnJson.toString();
    }

    /**
     * @author bin.Y
     * Description:组装返回报文
     * Date:  2018/6/2 15:26
     */
    public Object combReturnMessage(String Ret, String Msg, Map<String, Object> Data,String OperatorID) throws Exception {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("Data", Data);
        JSONObject dataJson = JSONObject.fromObject(Data);
        JSONObject returnJson = new JSONObject();
        returnJson.put("Ret", Ret);
        returnJson.put("Msg", Msg);
        String dataTmp=AESOperator.getInstance().encrypt(dataJson.toString(), PubGlobal.DataSecret, PubGlobal.DataSecretIV);
        returnJson.put("Data", dataTmp);
        StringBuilder builder = new StringBuilder();
        builder.append(OperatorID).append(Msg).append(dataTmp);
        String Sig = HMacMD5.getHmacMd5Str(PubGlobal.SignSecret, builder.toString());
        returnJson.put("Sig", Sig);
        return returnJson.toString();
    }

    /**
     * @author bin.Y
     * Description:发送前需先获取token
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
        String data = AESOperator.getInstance().decrypt(returnBody.get("Data").toString());
        Map<String, Object> dataMap = JsonUtils.fromJson(data, new HashMap<String, Object>().getClass());
        if ((double) dataMap.get("SuccStat") != 0.0) {
            map.put("Ret", dataMap.get("SuccStat").toString());
            map.put("Msg", dataMap.get("failReason").toString());
            return map;
        }
        map.put("Ret", "0");
        map.put("token", dataMap.get("accessToken").toString());
        Jedis jedis = new RedisPoolManager().getJedis();
        jedis.set("Charge:external:"+OperatorID+"token",dataMap.get("accessToken").toString());
        jedis.close();
        return map;
    }

    /**
     * @author bin.Y
     * Description:发送请求
     * Date:  2018/6/4 13:25
     */
    public Map<String, Object> sendRest(String OperatorID, String params, String token, String methodName) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String Data = AESOperator.getInstance().encrypt(params, PubGlobal.DataSecret, PubGlobal.DataSecretIV);
        String TimeStamp = sdf.format(new Date());
        String Seq = "0001";
        Properties properties = loadProperties("rest.properties");
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
        String Sig = HMacMD5.getHmacMd5Str(PubGlobal.SignSecret, builder.toString());
        jsonObj.put("Sig", Sig);
        System.out.println("接口["+methodName+"]下发报文："+jsonObj.toString());
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
            map.put("Msg", "token连续3次认证失败");
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
        String data = AESOperator.getInstance().decrypt(result.get("Data").toString());
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
