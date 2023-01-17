package com.dfssi.hive;

import com.dfssi.resources.ConfigDetail;
import com.dfssi.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parquet.Preconditions;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/9 15:06
 */
public class HiveConf extends ConfigDetail {
    private static final String _CONFIG = "/hive/hive-base";
    private final Logger logger = LogManager.getLogger(HiveConf.class);

    private boolean initHive2OnStart;
    private HiveConnection hiveConnection;

    public HiveConf(Map<String, String> config){
        super();
        try {
            Resources resources = new Resources(Resources.Env.NONE, _CONFIG);
            this.configMap.putAll(resources.getConfigMap());
        } catch (Exception e) {
            logger.error("读取hive基础配置失败。", e);
        }

        if(config != null)
            this.configMap.putAll(config);

        init();
    }

    private void init(){
        this.initHive2OnStart = getConfigItemBoolean("hive2.init.on.start", true);
        logger.info(String.format("hive2.init.on.start = %s", initHive2OnStart));

        boolean autoCreate = getConfigItemBoolean("hive2.auto.create.database", true);
        logger.info(String.format("hive2.auto.create.database = %s", autoCreate));

        String hostPorts = getConfigItemValue("hive2.host.port");
        Preconditions.checkNotNull(hostPorts, "hive2.host.port不能为空。");
        logger.info(String.format("hive2.host.port = %s", hostPorts));

        String database = getConfigItemValue("hive2.database", "default");
        logger.info(String.format("hive2.database = %s", database));

        String user = getConfigItemValue("hive2.user", "hive");
        logger.info(String.format("hive2.user = %s", user));

        String password = getConfigItemValue("hive2.password", "");
        logger.info(String.format("hive2.password = %s", password));

        String diver = getConfigItemValue("hive2.diver", "org.apache.hive.jdbc.HiveDriver");
        logger.info(String.format("hive2.diver = %s", diver));

        this.hiveConnection = new HiveConnection(hostPorts, database, user, password, diver, autoCreate);
        if(initHive2OnStart){
            try {
                hiveConnection.init();
            } catch (Exception e) {
                logger.error("初始化hive连接失败。", e);
            }
        }
    }

    public boolean isInitHive2OnStart() {
        return initHive2OnStart;
    }

    public HiveConnection getHiveConnection() {
        return hiveConnection;
    }
}
