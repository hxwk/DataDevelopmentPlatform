package com.dfssi.dataplatform.datasync.plugin.source.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jian on 2017/11/28.
 */
public class CrawlerTest {
    static final Logger logger = LoggerFactory.getLogger(CrawlerTest.class);
    public static void main(String[] args) {
        Boolean osType = System.getProperty("os.name").toLowerCase().contains("windows");
        logger.info("current os type is "+System.getProperty("os.name").toLowerCase());
        CrawlerController crawlerCtl = new CrawlerController();
        crawlerCtl.startTask();


    }
}
