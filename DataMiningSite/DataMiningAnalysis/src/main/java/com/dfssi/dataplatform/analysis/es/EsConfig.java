package com.dfssi.dataplatform.analysis.es;

import com.dfssi.dataplatform.analysis.fuel.FuelConfig;
import com.dfssi.resources.ConfigDetail;
import com.dfssi.resources.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/2/6 14:30
 */
public class EsConfig extends ConfigDetail {
    private static String _CONFIG = "/es/raw-es";
    private transient Logger logger;

    public EsConfig() {
        super();
        this.logger = LoggerFactory.getLogger(FuelConfig.class);
        logger.info("开始配置读取及初始化...");

        try {
            Resources resources = new Resources(Resources.Env.NONE, _CONFIG);
            this.configMap.putAll(resources.getConfigMap());
        } catch (Exception e) {
            logger.error("读取es基础配置失败。", e);
        }
    }


}
