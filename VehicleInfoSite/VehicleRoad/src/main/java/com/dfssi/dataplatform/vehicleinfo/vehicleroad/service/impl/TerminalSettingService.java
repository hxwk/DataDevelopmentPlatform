package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.ITerminalSettingService;
/*import com.yaxon.vn.nd.tbp.si.Req_E101;
import com.yaxon.vn.nd.tbp.si.Req_E102;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanghs on 2018/9/12.
 */
@Service
public class TerminalSettingService implements ITerminalSettingService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate =new RestTemplate();

    @Value("http://192.168.80.31:8081")
    private String accessServiceUrl;

    @Override
    public List<DataStatisticsEntity> queryparam(TerminalParamQuery terminalParamQuery) {
        //转发到master--master再转发到client--client下发指令给终端
        //结果原路返回


        return null;
    }


    //根据TerminalParamQuery入参类获得向master转发的Req_8103
    @Override
    public Req_8103 getReq_8103(TerminalSettingEntity terminalSettingEntity){
        Req_8103 req_8103= new Req_8103();
        try {
            req_8103.setId("jts.8103");
            req_8103.setSim(terminalSettingEntity.getSim());
            req_8103.setVid(terminalSettingEntity.getVid());
            List<Item> terminalSettingList = terminalSettingEntity.getParamItems();
            List<ParamItem> paramItems = new ArrayList<ParamItem>();
            for (int i = 0 ; i < terminalSettingList.size() ; i++){
                Item item = terminalSettingList.get(i);
                ParamItem e = new ParamItem(Integer.parseInt(item.getParamId(), 16),item.getParamVal());
                System.out.println(item.getParamId());
                System.out.println(item.getParamVal());
                paramItems.add(e);
            }
            req_8103.setParamItems(paramItems);
        } catch (NumberFormatException e) {
            logger.error("入参校验失败："+terminalSettingEntity.toString(),e);
            e.printStackTrace();
        }
        return  req_8103;
    };

    //根据TerminalParamQuery入参类获得向master转发的Req_8104
    @Override
    public Req_8104 getReq_8104(TerminalParamQuery terminalParamQuery){
        Req_8104 req_8104= new Req_8104();
        req_8104.setSim(terminalParamQuery.getSim());
        req_8104.setVid(terminalParamQuery.getVid());
        return  req_8104;
    };

    //根据TerminalParamQuery入参类获得向master转发的 Req_8106
    @Override
    public Req_8106 getReq_8106(TerminalParamQuery terminalParamQuery) throws  Exception{
        Req_8106 req_8106= new Req_8106();
        req_8106.setId("jts.8106");
        req_8106.setSim(terminalParamQuery.getSim());
        String times = String.valueOf(System.currentTimeMillis());
        req_8106.setTimestamp(times);
        req_8106.setVid(terminalParamQuery.getVid());
        String[] paramIdStr = terminalParamQuery.getQueryType().split(",");
        int[] paramIds = new int[paramIdStr.length];
        for (int i = 0;i<paramIdStr.length;i++){
            paramIds[i] = Integer.parseInt(paramIdStr[i],16);
            //paramIds[i] = Integer.parseInt(paramIdStr[i]);
        }
        req_8106.setParamIds(paramIds);
        return  req_8106;
    };
}
