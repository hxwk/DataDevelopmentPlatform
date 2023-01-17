package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by cxq on 2017/12/4.
 */
public class PropertiUtil {
    private static Properties p;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiUtil.class);

    static {
        Properties defaults = new Properties();
        defaults.setProperty("netty.so_backlog","128");
        defaults.setProperty("max_loss_conn_time","12");

        p = new Properties(defaults);
        try {
            String conf = System.getProperty("conf");
            System.out.println("conf = " + conf);
            p.load(new FileReader(conf));
        } catch (IOException e) {
            LOGGER.error("PropertiUtil加载配置文件失败 :{}",e);
        }
    }

    public static final String getStr(String key){
        String property =null;
        if (p != null) {
            property = p.getProperty(key);
        }
        return property;
    }

    public static final int getInt(String key){
        int property =-1;
        if (p != null) {
            property = Integer.parseInt( p.getProperty(key));
        }
        return property;
    }


}
