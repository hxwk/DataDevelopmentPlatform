package com.dfssi.dataplatform.datasync.plugin.source.crawler;

import com.dfssi.dataplatform.datasync.plugin.source.common.BaseCrawlerRuleParam;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * crawller controller, start crawler task
 * controller.start crawler start multi threading to crawler the website to fetch content
 * @author jianKang
 * @date 2017/11/8
 */
public class CrawlerController {
    static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    public void startTask(){
        logger.info("Load parameters and initial ,please waiting...");
        BaseCrawlerRuleParam baseCrawlerRuleParam = BaseCrawlerRuleParam.getInstanceConf();
        String crawlStorageFolder = baseCrawlerRuleParam.getCrawlStorageFolder();
        int numberOfCrawlers = baseCrawlerRuleParam.getNumberOfCrawlers();
        int connectionTimeout = baseCrawlerRuleParam.getConnectionTimeout();
        String userAgentString = baseCrawlerRuleParam.getUserAgentString();
        int politenessDelay = baseCrawlerRuleParam.getPolitenessDelay();
        boolean resumableCrawling = baseCrawlerRuleParam.isResumableCrawling();
        String proxyHost = baseCrawlerRuleParam.getProxyHost();
        int proxyPort = baseCrawlerRuleParam.getProxyPort();

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        //config.setConnectionTimeout(connectionTimeout);
        config.setUserAgentString(userAgentString);
        //config.setPolitenessDelay(politenessDelay);
        //config.setProxyHost(proxyHost);
        //config.setProxyPort(proxyPort);

        logger.info("config load over! config information such as connectionTimeout "+connectionTimeout);
        logger.info("userAgentString "+userAgentString);
        logger.info("PolitenessDelay "+politenessDelay);
        logger.info("ResumableCrawling "+resumableCrawling);
        logger.info("proxyHost "+proxyHost);
        logger.info("proxyPort "+proxyPort);

        /**
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            logger.error("CrawlController crawler controller exception, please modify it......");
        }

        /**
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        for(String seed: baseCrawlerRuleParam.getSeedSite()){
            controller.addSeed(seed);
            logger.info("seed site is: "+seed);
        }
        /**
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        logger.info("start web crawler ,start time {0}......");
        controller.start(WebCrawlerVisit.class, numberOfCrawlers);
        WebCrawlerVisit s = new WebCrawlerVisit();
        logger.info("当日最新信息：");
        for(String content : s.crawlerContent()) {
            logger.info("crawler content is :" +content);
        }
        logger.info("crawler fetch content over!");
    }
}
