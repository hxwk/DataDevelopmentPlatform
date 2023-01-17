package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.cache;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.bean.Vehicle;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.db.DBCommon;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.db.DBPoolDruidCommon;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Hannibal on 2018-02-03.
 */
public class VehicleCacheThread implements Runnable{

    private static Logger logger = Logger.getLogger(VehicleCacheThread.class);

    private boolean isActive=true;

    public void setIsActive(boolean isActive){
        isActive=isActive;
    }

    @Override
    public void run() {

        while (isActive) {
            Connection conn = null;
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                logger.info("开始缓存车辆数据");
                System.out.println("开始缓存车辆数据");
                Long dealBeginTime = System.currentTimeMillis();
                StringBuilder buf = new StringBuilder();
                buf.append("select sim, vid, vin, did from (SELECT s.SIM_NUMBER as sim, v.ID as vid, v.vin as VIN, v.did as DID ");
                buf.append("FROM SSI_SIM s LEFT JOIN SSI_VEHICLE v on s.ID = v.SIM)a where a.vid is not NULL and a.sim is not NULL");

                conn = DBPoolDruidCommon.getInstance().getConnection(Constants.VNND_DATASOURCE_ID);
//                Class.forName(Constants.DB_VNND_DRIVER);
//                conn = DriverManager.getConnection(Constants.DB_VNND_URL, Constants.DB_VNND_USRENAME, Constants.DB_VNND_PASSWORD);
                statement = conn.prepareStatement(buf.toString());

                result = statement.executeQuery();

                if (null != result) {
                    while (result.next()) {
                        try {
                            String sim = result.getString("sim");
                            String vid = result.getString("vid");
                            Vehicle vehicle = new Vehicle();
                            vehicle.setDid(result.getString("did"));
                            vehicle.setSim(sim);
                            vehicle.setVin(result.getString("vin"));
                            vehicle.setId(vid);

                            CacheEntities.vidSets.add(vid);
                            CacheEntities.sim2VehicleMap.put(sim, vehicle);
                        } catch (Exception e) {
                            logger.error(null, e);
                        }
                    }
                }

                logger.info("结束缓存车辆数据，耗时：" + (System.currentTimeMillis() - dealBeginTime) / 1000.0 + ".s ");
                logger.debug("CacheEntities.sim2VehicleMap = " + CacheEntities.sim2VehicleMap + " \n CacheEntities.vidSets = " + CacheEntities.vidSets);
            } catch (Exception e) {
                logger.error(null, e);
            } finally {
                try {
                    DBCommon.close(conn, statement, result);
                } catch (SQLException e) {
                    logger.error("关闭数据库连接发送错误");
                }

                try {
                    Thread.sleep(Constants.VEHCILE_CHAHE_THREAD_SLEEPTIME * 60 * 1000L);
                } catch (InterruptedException e) {
                    logger.error("缓存车辆数据线程睡眠失败");
                }
            }
        }
    }
}
