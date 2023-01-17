package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.analyze;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.GeodeTool;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.RedisPoolManager;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Iterator;

/**
 * @author JianKang
 */
public abstract class BaseProtoHandler {

    private static Logger logger = LoggerFactory.getLogger(BaseProtoHandler.class);
    /**
     * 上传报文处理
     * @param upMsg 消息协议
     * @return 报文对象
     */
    public abstract String doUpMsg(ProtoMsg upMsg);

    /**
     * 根据vin获取添加车辆信息
     * @param upMsg
     */
    public VehicleDTO getVehicleInfo(ProtoMsg upMsg) {
        VehicleDTO vehicle = null;
        //根据vin获取车辆信息
        Region region = null;

        Object objList = null;
        try {
            //根据vin拿到车企信息
            //ClientRegionFactory rf = GeodeTool.getInstance().getRf();
            //region = rf.create(Constants.REGION_VEHICLEINFO);
//            region = GeodeTool.getRegeion(Constants.REGION_VEHICLEINFO);
            StringBuilder sqlBuf = new StringBuilder();
            sqlBuf.append("select * from /");
            sqlBuf.append(Constants.REGION_VEHICLEINFO);
            sqlBuf.append(" where vin = '");
            sqlBuf.append(upMsg.vin);
            sqlBuf.append("' and isValid = '1' limit 1");

            //logger.debug(" REGION_VEHICLEINFO region sql: " + sqlBuf.toString());
//            objList = region.query(sqlBuf.toString());
            objList = GeodeTool.getInstance().getQueryService().newQuery(sqlBuf.toString()).execute();

            if (objList instanceof ResultsBag) {
                Iterator iter = ((ResultsBag)objList).iterator();
                while (iter.hasNext()) {
                    vehicle = (VehicleDTO) iter.next();

                    break;
                }
            }
        } catch (Exception e) {
            logger.error("查询geode出错", e);
        } finally {
            if (null != region) {
                region.close();
            }
        }
        return vehicle;
    }

    public void updateVehicleStatus2Redis(String vin) {
        Jedis jedis = null;
        try {
            jedis = new RedisPoolManager().getJedis();
            jedis.set(Constants.GK_VEHICLE_STATE + vin, String.valueOf(System.currentTimeMillis()));
            jedis.expire(Constants.GK_VEHICLE_STATE + vin, Constants.VEHCILE_CACHE_TIME * 60);
        } catch (Exception e) {
            logger.error("在Redis中更新车辆状态失败", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
}
