package com.dfssi.dataplatform.analysis.preprocess.process.resource

import com.dfssi.dataplatform.analysis.preprocess.process.utils.{HttpRequest, JacksonUtils}
import com.google.gson.{JsonArray, JsonElement, JsonParser}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.Logging
import java.util
import scala.collection.JavaConversions._

import scala.util.control.Breaks.{break, breakable}


object HttpRequestUtils extends Serializable with Logging {

  var httpRequest: HttpRequest = _

  def getHttpRequest: HttpRequest = {
    if (httpRequest == null) {
      httpRequest = new HttpRequest("[Vechicle Process]")
    }

    httpRequest
  }

  def getPositionIfno(url: String,
                      lat: Double,
                      lng: Double): PositionInfo = {
    val reqParams: util.Map[String, AnyRef] = new util.LinkedHashMap[String, AnyRef]()
    reqParams.put("l", lat + "," + lng)
    val resultJsonStr = getHttpRequest.get(url, reqParams)
    buildPositionInfo(resultJsonStr)
  }

  private def buildPositionInfo(respJsonStr: String): PositionInfo = {
    val positionInfo = new PositionInfo()
    if (StringUtils.isBlank(respJsonStr)) {
      return positionInfo
    }

    try {
      val respJsonObject = new JsonParser().parse(respJsonStr).getAsJsonObject
      val addrJsonArray: JsonArray = respJsonObject.getAsJsonArray("addrList")
      breakable {
        for (jsonEl: JsonElement <- addrJsonArray) {
          val jsonObj = jsonEl.getAsJsonObject
          val admName = JacksonUtils.getAsString(jsonObj, "admName")
          if (StringUtils.isNotBlank(admName)) {
            val addrs = admName.split(",")
            if (addrs.nonEmpty) {
              positionInfo.province = addrs(0)
              if (addrs.length > 1 && addrs.length <= 2) {
                positionInfo.city = addrs(1)
              } else if (addrs.length > 2) {
                positionInfo.city = addrs(1)
                positionInfo.area = addrs(2)
              }
            }
            break
          }
        }
      }
    } catch {
      case t: Throwable =>
        logError(
          "---------Error in execution of http request " + t.getMessage + "\n-----------------------\n" + t
            .printStackTrace())
    }
    positionInfo
  }
}

class PositionInfo {
  var province: String = ""
  var city: String = ""
  var area: String = ""

  override def toString: String = s"$province $city $area"
}