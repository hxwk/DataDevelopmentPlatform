package org.apache.spark.sql.hive

import org.apache.hadoop.hive.conf.HiveConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.execution.CacheManager
import org.apache.spark.sql.hive.HiveContext.hiveExecutionVersion
import org.apache.spark.sql.hive.client.{ClientWrapper, IsolatedClientLoader}
import org.apache.spark.util.Utils

import scala.collection.mutable.HashMap

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2017/4/10 17:37 
  */
object HiveTools {

    def newHiveContext(sc:SparkContext): HiveContext ={
        new HiveContext(sc, new CacheManager, SQLContext.createListenerAndUI(sc), executionHive, null, true)
    }

   private def executionHive: ClientWrapper = {

        val loader = new IsolatedClientLoader(
            version = IsolatedClientLoader.hiveVersion(hiveExecutionVersion),
            execJars = Seq(),
            config = newConfiguration(),
            isolationOn = false,
            baseClassLoader = Utils.getContextOrSparkClassLoader)
        loader.createClient().asInstanceOf[ClientWrapper]
    }

    /** Constructs a configuration for hive, where the metastore is located in a temp directory. */
    private def newConfiguration(): Map[String, String] = {
       // val tempDir = Utils.createTempDir()
        //val localMetastore = new File(tempDir, "metastore")
        //val scratchDir = new File(tempDir, "scratch")
        val propMap: HashMap[String, String] = HashMap()
        HiveConf.ConfVars.values().foreach { confvar =>
            if (confvar.varname.contains("datanucleus") || confvar.varname.contains("jdo")
                    || confvar.varname.contains("hive.metastore.rawstore.impl")) {
                propMap.put(confvar.varname, confvar.getDefaultExpr())
            }
        }

        //propMap.put(HiveConf.ConfVars.METASTOREWAREHOUSE.varname, localMetastore.toURI.toString)

        propMap.put(HiveConf.ConfVars.METASTORECONNECTURLKEY.varname, "jdbc:mysql://hadoop-cm:3306/hive")
        propMap.put(HiveConf.ConfVars.METASTORE_CONNECTION_DRIVER.varname,"com.mysql.jdbc.Driver")
        propMap.put(HiveConf.ConfVars.METASTORE_CONNECTION_USER_NAME.varname,"root")
        propMap.put(HiveConf.ConfVars.METASTOREPWD.varname,"root")

       // propMap.put(HiveConf.ConfVars.SCRATCHDIR.varname, scratchDir.toURI.toString)
        propMap.put("datanucleus.rdbms.datastoreAdapterClassName",
                     "org.datanucleus.store.rdbms.adapter.MySQLAdapter")
        propMap.toMap
    }

}
