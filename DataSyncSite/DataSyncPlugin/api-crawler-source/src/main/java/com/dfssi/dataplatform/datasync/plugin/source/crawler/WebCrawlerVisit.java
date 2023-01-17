package com.dfssi.dataplatform.datasync.plugin.source.crawler;

import com.dfssi.dataplatform.datasync.plugin.source.common.BaseCrawlerRuleParam;
import com.dfssi.dataplatform.datasync.plugin.source.common.RandomSequenceUtil;
import com.google.common.collect.Lists;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.regex.Pattern;

/**
 * configure information about crawler
 * @author jianKang
 * @date 2017/11/8
 */
public class WebCrawlerVisit extends WebCrawler {
    /**
     * Regular matching the specified suffix file
     */
    private static final Logger logger = LoggerFactory.getLogger(WebCrawlerVisit.class);
    private static BaseCrawlerRuleParam configure = BaseCrawlerRuleParam.getInstanceConf();
    private final static Pattern FILTERS = Pattern
            .compile(configure.getFilter());

    private final static String URL_PREFIX = configure.getUrlPrefix();
    private final static Pattern URL_PARAMS_PATTERN = Pattern
            .compile(configure.getUrlParamPattern());

    private final static int instantParamLength = 2;

    private static List<String> crawlerInformations = Lists.newArrayList();

    private static String crawlerInformation = null;

    private static String storedFile = StringUtils.EMPTY;

    private static String lineSeparator="\r\n";

    private static String themeSites = null;

    private String storedPathPrefix = "/home/kangj/flume1.7/tmp/";

    private static String fileExtend =".properties";
    /**
     * such as oilRule.properties ,3 characters is oil
     */
    private final static int splitConfFileNum = 3;

    public WebCrawlerVisit(){
        this.setStoredPathPrefix(configure.getCrawlStorageFolder());
        String fileNamePath = splitThemeName(configure.getConfigurationPath());
        this.setStoredFile(fileNamePath);
        logger.info("save it to: "+fileNamePath);
        this.setThemeSite(configure.getConfigurationPath());
    }

    public static List<String> getCrawlerInformations() {
        return crawlerInformations;
    }

    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息
     * 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        /**
         * 正则匹配，过滤掉我们不需要的后缀文件
         */
        if (FILTERS.matcher(href).matches() || !href.startsWith(URL_PREFIX)) {
            return false;
        }

        String[] params = href.split("\\?");
        if (params.length < instantParamLength) {
            return false;
        }

        if (!URL_PARAMS_PATTERN.matcher(params[1]).matches()) {
            return false;
        }
        return true;
    }

    /**
     * 当我们爬到我们需要的页面，这个方法会被调用，我们可以尽情的处理这个页面
     * page参数封装了所有页面信息
     */
    @Override
    public void visit(Page page) {
        StringBuilder collections = new StringBuilder();
        String url = page.getWebURL().getURL();
        logger.info("current crawler site is: "+url);
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Document doc = Jsoup.parse(html);

            if(!StringUtils.equals(StringUtils.EMPTY,configure.getBodyCssSelector())){
                List<Element> elementsBody = doc.select(configure.getBodyCssSelector());
                for (Element es:elementsBody){
                    collections.append(es.text());
                    collections.append(configure.getFieldDelimite());
                    try {
                        writeTxtFile(es.text(),new File(storedFile));
                        writeTxtFile(lineSeparator,new File(storedFile));
                    } catch (Exception e) {
                        logger.error("write text file error, please check it",e.getMessage());
                    }
                }
                logger.info("crawler info(body) has been stored to local file... ");
            }
            if(!StringUtils.equals(StringUtils.EMPTY,configure.getElementsSelector())) {
                List<Element> elements = doc.select(configure.getElementsSelector());
                for (Element es : elements) {
                    collections.append(es.text());
                    collections.append(configure.getRowDelimite());
                    try {
                        writeTxtFile(es.text(),new File(storedFile));
                        writeTxtFile(lineSeparator,new File(storedFile));
                    } catch (Exception e) {
                        logger.error("write to text file error, please check.", e.getMessage());

                    }
                }
                logger.info("crawler info(elements) has been stored to local file... ");
            }
            else{
                crawlerInformation = null;
            }
            crawlerInformation = collections.toString();
            crawlerInformations.add(crawlerInformation);
        }
    }

    public void setStoredPathPrefix(String storedPathPrefix) {
        this.storedPathPrefix = storedPathPrefix;
    }

    public void setThemeSite(String themeSite) {
        themeSites = themeSite;
    }

    /**
     * get the content by crawler
     * @return web content list
     */
    public List<String> crawlerContent() {
        return getCrawlerInformations() ;
    }

    public void setStoredFile(String storedFileName) {
        storedFile = storedFileName;
    }

    /**
     * sub name from params files name (datastream-oil-20171123001.txt)
     * @param themeConf
     * @return themeFileName (datastream-oil-20171123001.txt)
     */
    private String splitThemeName(String themeConf){
        String prefixThename = themeConf.split("/")[themeConf.split("/").length-1].substring(0,splitConfFileNum);
        return storedPathPrefix + prefixThename+"-"+ RandomSequenceUtil.getRandomFileName()+fileExtend;
    }

    /**
     * if filename is not exists, create file of filename
     * @param filename
     * @return
     */
    public static  boolean createFile(File filename){
        boolean flag = false;
        try{
            if(!filename.exists()){
                filename.createNewFile();
                flag = true;
            }
            flag = true;
        } catch (IOException e) {
            logger.error("IO error",e.getMessage());
        }
        return flag;
    }

    /**
     * write text to file from web stream
     * @param content
     * @param fileName
     * @return success is true , or false
     * @throws Exception
     */
    public static boolean writeTxtFile(String content,File fileName)throws Exception{
        RandomAccessFile mm = null;
        boolean flag=false;
        FileOutputStream o;
        if(createFile(fileName)) {
            try {
                o = new FileOutputStream(fileName, true);
                o.write(content.getBytes("UTF-8"));
                o.close();
                flag = true;
            } catch (Exception e) {
                logger.error("create file or write file error, please modify it......");
            } finally {
                if (mm != null) {
                    mm.close();
                }
            }
        }
        return flag;
    }
}
