package com.dfssi.dataplatform.analysis.preprocess

import java.net.URI

import com.dfssi.spark.common.Applications
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{Logging, SparkContext}

import scala.xml.{Elem, XML}

trait AnalysisService extends Logging {

  def getSparkTaskDef(nameNode: String, appPath: String, fileName: String): Elem = {
    val uri = new URI(nameNode);
    val conf = new Configuration();
    val fs = FileSystem.get(uri, conf);
    val is = fs.open(new Path(appPath + "/" + fileName));
    val sparkDefEl = XML.load(is);

    is.close();
    fs.close()

    return sparkDefEl;
  }

  def applicationExist(appName: String,
                       sc: SparkContext): Boolean ={

    val conf = sc.getConf
    val appId = conf.getAppId

    try {
      val appAmUrl = conf.get("spark.org.apache.hadoop.yarn.server.webproxy.amfilter.AmIpFilter.param.PROXY_URI_BASES")
      val uri = URI.create(appAmUrl)
      val host = uri.getHost
      val port = uri.getPort
      return Applications.applicationExist(host, port, appName, appId)
    } catch {
      case e: Exception =>
        logError(s"检查任务${appName}是否存在失败。", e)
    }
    false
  }
}
