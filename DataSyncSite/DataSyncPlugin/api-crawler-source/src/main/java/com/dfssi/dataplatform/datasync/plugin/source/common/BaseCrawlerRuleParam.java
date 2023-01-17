package com.dfssi.dataplatform.datasync.plugin.source.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
/**
 * base configure rule parameters on website ,
 * 如果需要后缀为参数，需要同时配置ParamSuffix，同时添加 urlParamSelector，若只从
 * 单一网站抓取，直接将 urlParamSelector 置为空即可。
 * @author jianKang
 * @date 2017/11/8
 */
public class BaseCrawlerRuleParam {
    static final Logger logger = LoggerFactory.getLogger(BaseCrawlerRuleParam.class);
    /**
     * configuration path
     * oilRule.properties 实时油价信息
     * envRule.properties 环境大气信息
     */
    private static String configurationPath=BaseCrawlerRuleParam.class.getClassLoader().getResource("weaRule.properties").getPath();
    private InputStream configurationStream = BaseCrawlerRuleParam.class.getClassLoader().getResourceAsStream("weaRule.properties");
    /**
     * crawlStorageFolder: save data of crawler to storage folder
     */
    private String crawlStorageFolder = "D:\\test\\data";
    /**
     * crawler number
     */
    private int numberOfCrawlers = 3;
    /**
     *     URL prefix
     */
    private String urlPrefix = "http://youjia.chemcp.com";
    /**
     * URL PARAM PATTERN
     */
    private String urlParamPattern ="carbrand=brand-\\d+(&index=\\d+)?";
    /**
     * seed site
     */
    private List<String> seedSite = Lists.newArrayList();
    /**
     * body css Selector
     */
    private String bodyCssSelector = "table[width=100%][border=0][cellpadding=4][cellspacing=1][bgcolor=#B6CCE4]";
    /**
     * element elementsSelector
     */
    private String elementsSelector = " [bgcolor=#FFFFFF]";
    /**
     * web filter regular
     */
    private String filter = ".*(\\.(css|js|bmp|gif|jpe?g|ico"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$";
    /**
     * CSV save path
     */
    private String csvPath = "d:\\test\\data.csv";

    /**
     * field separator (such as ' ',',','|','\t','\001' etc.)
     */
    private String fieldDelimite = "^";
    /**
     * row separator (such as ' ',',','|','\t','\001' etc.)
     */
    private String rowDelimite="\t";
    /**
     * connection time out
     */
    private int connectionTimeout = 30000;
    /**
     * user agent string
     */
    private String userAgentString = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0)";

    /**
     * url param selector (http://106.37.208.228:8082/CityForecast/Index?CityCode=420100, get all params(420100))
     */
    private String urlParamSelector = null;

    /**
     * wait politenessDelay before crawler web site
     */
    private int politenessDelay = 200;

    /**
     * resumable crawling when it stop
     */
    private boolean resumableCrawling = true;

    /**
     * proxy host
     */
    private String proxyHost = "113.118.159.186";
    /**
     * proxy port
     */
    private int proxyPort =9000;
    /**
     * insert database file name after crawler data
     */
    private String inDbDatafile="wea-2017120756920.properties";


    private BaseCrawlerRuleParam() {
     /**read configuration file
     //private Map<String,String> configurations;
     */
        Boolean osType = System.getProperty("os.name").toLowerCase().contains("windows");
        Properties props = new Properties();
        File storagePath;

        try {

            //FileInputStream in  = new FileInputStream(configurationPath);
            props.load(configurationStream);

            storagePath = new File(props.getProperty("crawlStorageFolder"));
            this.setUrlPrefix(props.getProperty("urlPrefix"));
            this.setBodyCssSelector(props.getProperty("bodyCssSelector"));
            this.setElementsSelector(props.getProperty("elementsSelector"));
            this.setFieldDelimite(props.getProperty("fieldDelimite"));
            this.setConnectionTimeout(Integer.parseInt(props.getProperty("connectionTimeout")));
            this.setUserAgentString(props.getProperty("userAgentString"));
            this.setNumberOfCrawlers(Integer.parseInt(props.getProperty("numberOfCrawlers")));
            this.setPolitenessDelay(Integer.parseInt(props.getProperty("politenessDelay")));
            this.setInDbDatafile(props.getProperty("in_db_datafile"));
            if(!StringUtils.EMPTY.equals(props.getProperty("proxyHost"))){
                this.setProxyHost(props.getProperty("proxyHost"));
            }
            if(!StringUtils.EMPTY.equals(props.getProperty("proxyPort"))){
                this.setProxyPort(Integer.parseInt(props.getProperty("proxyPort")));
            }
            this.setUrlParamSelector(props.getProperty("urlParamSelector"));
            /**
             * url params selector is not null, load suffix code params
             */
            if(StringUtils.EMPTY.equals(props.getProperty("urlParamSelector"))){
                seedSite.add(props.getProperty("urlSite"));
            }else{
                ParamSuffix urls = new ParamSuffix();
                seedSite.addAll(urls.getUrls());
            }
            /**
             * os is Windows , then D:\\test\\data, else /home/crawler/crawlStorageFolder
             */
            if(osType){
                this.setCrawlStorageFolder("D:\\test\\data\\");
            }else{
                logger.info("storagePath is "+storagePath);
                if(!StringUtils.EMPTY.equals(storagePath.getName())&&!storagePath.exists()){
                    storagePath.getParentFile().mkdirs();
                    storagePath.setWritable(true);
                    this.setCrawlStorageFolder(storagePath.getPath()+"/");
                }else{
                    this.setCrawlStorageFolder(storagePath.getPath()+"/");
                }
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            logger.error("IO exception, please modify it......");
        }
    }

    public InputStream getConfigurationStream() {
        return configurationStream;
    }

    public void setConfigurationStream(InputStream configurationStream) {
        this.configurationStream = configurationStream;
    }

    public String getInDbDatafile() {
        return inDbDatafile;
    }

    public void setInDbDatafile(String inDbDatafile) {
        this.inDbDatafile = inDbDatafile;
    }

    public void setCrawlStorageFolder(String crawlStorageFolder) {
        this.crawlStorageFolder = crawlStorageFolder;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setPolitenessDelay(int politenessDelay) {
        this.politenessDelay = politenessDelay;
    }

    public void setUrlParamSelector(String urlParamSelector) {
        this.urlParamSelector = urlParamSelector;
    }

    public void setNumberOfCrawlers(int numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setUserAgentString(String userAgentString) {
        this.userAgentString = userAgentString;
    }

    public String getUserAgentString() {
        return userAgentString;
    }

    public int getPolitenessDelay() {
        return politenessDelay;
    }

    public boolean isResumableCrawling() {
        return resumableCrawling;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public static BaseCrawlerRuleParam getInstanceConf(){
        return new BaseCrawlerRuleParam();
    }

    public String getCrawlStorageFolder() {
        return crawlStorageFolder;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public int getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public String getUrlParamPattern() {
        return urlParamPattern;
    }

    public List<String> getSeedSite() {
        return seedSite;
    }

    public String getBodyCssSelector() {
        return bodyCssSelector;
    }

    public String getElementsSelector() {
        return elementsSelector;
    }

    public String getFieldDelimite() {
        return fieldDelimite;
    }

    public String getRowDelimite() {
        return rowDelimite;
    }

    public String getFilter() {
        return filter;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public void setBodyCssSelector(String bodyCssSelector) {
        this.bodyCssSelector = bodyCssSelector;
    }

    public void setElementsSelector(String elementsSelector) {
        this.elementsSelector = elementsSelector;
    }

    public void setFieldDelimite(String fieldDelimite) {
        this.fieldDelimite = fieldDelimite;
    }

    @Override
    public String toString() {
        StringBuilder strs = new StringBuilder();
        strs.append("Base crawler Rule parameters is numberOfCrawlers "+numberOfCrawlers);
        strs.append(" urlPrefix "+urlPrefix);
        strs.append(" urlParamPattern "+urlParamPattern);
        strs.append(" urlPrefix "+urlPrefix);
        strs.append(" bodyCssSelector "+bodyCssSelector);
        strs.append(" elementsSelector "+elementsSelector);
        strs.append(" filter "+filter);
        strs.append(" userAgentString "+userAgentString);
        return strs.toString();
    }
}
