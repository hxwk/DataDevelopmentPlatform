package com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * Created by HSF on 2017/11/27.
 * 插件管理
 */
public class PluginManager {
    public static final Logger logger = LoggerFactory.getLogger(PluginManager.class);
    private static PluginManager instance = null;
    private  static PluginFileMonitor pluginFileMonitor =null ;
    private static PluginFileListener pluginFileListener = null;
    private static long pluginScanInterval = 5000;
    private static String pluginLibDir = null;


   public static PluginManager getInstance(){
       if (null==instance){
           return new PluginManager();
       }else{
           return instance;
       }

   }

   private PluginManager(){
       SystemContext sc = new SystemContext(ClientConstants.CLIENT_PROPERTITIES_FILE);
       pluginLibDir = sc.getString(ClientConstants.PLUGIN_LIB_PATH,"lib");
       //pluginLibDir = sc.getString("plugin.lib.path","E:\\filepath");
       pluginScanInterval = sc.getLong(ClientConstants.PLUGIN_LIB_SCAN_INTERVAL,5000L);
       ClassLoaderUtil.loadJarPath(pluginLibDir);
       pluginFileListener = new PluginFileListener();
       pluginFileMonitor = new PluginFileMonitor(pluginScanInterval);
   }

   public static void start(){
       try{
           pluginFileMonitor.monitor(pluginLibDir,pluginFileListener);
           pluginFileMonitor.start();
       }catch (Exception e){
           logger.error(e.getMessage());
       }
   }

   public static void stop(){
       try{
           pluginFileMonitor.stop();
       }catch (Exception e){
           logger.error(e.getMessage());
       }
   }


    public static void main(String[] args) throws Exception {
//        PluginFileMonitor m = new PluginFileMonitor(5000);
//        m.monitor("E:\\filepath",new PluginFileListener());
//        m.start();
        PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.start();
        sleep(3000);

    }

}
