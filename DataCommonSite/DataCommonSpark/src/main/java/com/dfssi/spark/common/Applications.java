package com.dfssi.spark.common;

import com.dfssi.common.json.Jsons;
import com.dfssi.common.net.Https;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *   重复任务检测、
 * @author LiXiaoCong
 * @version 2018/2/13 14:46
 */
public class Applications {
   private Applications(){}

   private final static String RUNNING_APPLICATION_URI_MODEL = "http://%s:%s/ws/v1/cluster/apps?states=RUNNING";
   private final static String ACCEPTED_APPLICATION_URI_MODEL = "http://%s:%s/ws/v1/cluster/apps?states=ACCEPTED";

    public static boolean applicationExist(SparkContext sc) throws Exception {
        String appName = sc.appName();
        SparkConf conf = sc.getConf();
        String appId = conf.getAppId();

        String appAmUrl = conf.get("spark.org.apache.hadoop.yarn.server.webproxy.amfilter.AmIpFilter.param.PROXY_URI_BASES");
        URI uri = URI.create(appAmUrl);

        return Applications.applicationExist(uri.getHost(), uri.getPort(), appName, appId);
    }

    /**
     * @param rmHost  resourceManager host
     * @param rmPort  resourceManager port
     * @param applicationName
     * @return
     */
   public static boolean applicationExist(String rmHost,
                                          int rmPort,
                                          String applicationName) throws Exception {

       return applicationExist(rmHost, rmPort, applicationName, null);
   }

    public static boolean applicationExist(String rmHost,
                                           int rmPort,
                                           String applicationName,
                                           String applicationId) throws Exception {

        String applications = getAcceptedApplications(rmHost, rmPort);
        if(!exist(applications, applicationName, applicationId)){
            applications = getRunningApplications(rmHost, rmPort);
            return exist(applications, applicationName, applicationId);
        }

        return true;
    }

   private static boolean exist(String jsonApps,
                                String applicationName,
                                String applicationId) throws Exception {
       Map<String, Map<String, List<Map<String, Object>>>> resMap = Jsons.toMap(jsonApps);
       Map<String, List<Map<String, Object>>> appMap = resMap.get("apps");
       if(appMap == null) return false;

       List<Map<String, Object>> apps = appMap.get("app");
       if(apps == null) return false;
       String name;
       String id;
       for(Map<String, Object> app : apps){
           name = app.get("name").toString();
           if(name.equals(applicationName)){
               id = app.get("id").toString();
               if(!id.equals(applicationId)){
                   return true;
               }
           }
       }
       return false;
   }


   private static String getRunningApplications(String rmHost, int rmPort) throws Exception {
       String uri = String.format(RUNNING_APPLICATION_URI_MODEL, rmHost, rmPort);
      return Https.get(uri);
   }

   private static String getAcceptedApplications(String rmHost, int rmPort) throws Exception {
       String uri = String.format(ACCEPTED_APPLICATION_URI_MODEL, rmHost, rmPort);
      return Https.get(uri);
   }

    public static void main(String[] args) throws Exception {
        boolean b = applicationExist("devmaster", 8088, "FuelDataAnalysisFromKafka");
        System.out.println(b);
    }


}
