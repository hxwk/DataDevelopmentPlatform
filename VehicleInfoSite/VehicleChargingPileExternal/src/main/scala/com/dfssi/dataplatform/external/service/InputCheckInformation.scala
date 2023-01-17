//package com.dfssi.dataplatform.service
//
//import java.io.InputStream
//import java.util.{ArrayList, HashMap, UUID}
//
//import com.google.gson.internal.LinkedTreeMap
//import com.google.gson.{GsonBuilder, JsonParser}
//import org.apache.commons.io.IOUtils
//
//import scala.collection.mutable.ArrayBuffer
//import scala.util.Try
//
///**
//  * Description
//  *
//  * @author bin.Y
//  * @version 2018/4/12 10:11
//  */
//class InputCheckInformation {
//  def processData(json: String): String = {
////        val message = analysisJson("/baowen.json");
//    val gb = new GsonBuilder();
//    val gson = gb.create();
//    val jsonMap = gson.fromJson(json, classOf[HashMap[String, ArrayList[LinkedTreeMap[String, ArrayList[LinkedTreeMap[String, String]]]]]]);
//    println(jsonMap)
//    var platfromList = jsonMap.get("platfrom")
////    var carcompanyId = platfromList.get(0).get("carcompanyInfo").get(0).get("carcompanyId")
////    val queryCarcompany = JdbcManage.executeQuery("select check_status from compliance_carcompany_status a where a.carcompany_id='" + carcompanyId + "'");
////    if (queryCarcompany.size > 0 && queryCarcompany(0).get("check_status").get.equals("0")) {
////      return "0";
////    }
////    if (queryCarcompany.size > 0 && queryCarcompany(0).get("check_status").get.equals("1")) {
////      JdbcManage.executeUpdate("update compliance_carcompany_status set check_status='0' where carcompany_id='"+carcompanyId+"'")
////    }
////    if(queryCarcompany.size == 0){
////      JdbcManage.executeUpdate("insert into  compliance_carcompany_status (carcompany_id,check_status) values ('" + carcompanyId + "','0')")
////    }
//    //生成该检测的唯一ID
//    val checkId = ConformanceCommon.nowDateStr()
//    println("checkId~~:"+checkId)
//    for (i <- 0 until platfromList.size()) {
//      var platfromLoginOut = platfromList.get(i).get("platfromLoginOut")
//      var singleCarCheck = platfromList.get(i).get("singleCarCheck")
////      var manyCarCheck = platfromList.get(i).get("manyCarCheck")
////      singleCarCheck.addAll(manyCarCheck)
//      singleCarCheck.addAll(platfromLoginOut)
//      //1.插入每个检测项信息  2.插入车辆信息
//      insertItemAndCar(singleCarCheck, checkId)
//      var platfromCheckInfo = platfromList.get(i).get("carcompanyInfo").get(0)
//      //插入车企信息
//      insertCarcompany(platfromCheckInfo, checkId)
//    }
//    checkId
//  }
//
//  /**
//    * @author bin.Y
//    *         Description:1.插入每个检测项信息  2.插入车辆信息
//    *         Date:  2018/4/12 16:32
//    */
//  def insertItemAndCar(arrMap: ArrayList[LinkedTreeMap[String, String]], checkId: String): Unit = {
//    //将时间作为车辆ID的基数
//    val carId = ConformanceCommon.nowDateStr()
//    var arr: ArrayBuffer[String] = ArrayBuffer()
//    for (i <- 0 until arrMap.size()) {
//      var params = Map[String, String]()
//      if (!arr.contains(Try(arrMap.get(i).get("carVin")).getOrElse(""))) arr += arrMap.get(i).get("carVin")
////      params += ("carVin" -> Try(arrMap.get(i).get("carVin")).getOrElse(""))
//      params += ("checkType" -> arrMap.get(i).get("checkType"))
//      params += ("dataBeginTime" -> arrMap.get(i).get("beginTime"))
//      params += ("dataEndTime" -> arrMap.get(i).get("endTime"))
//      params += ("checkId" -> checkId)
//      params += ("carId" -> (carId + (arr.size - 1)))
//      //插入每个检测项的相关信息
//      JdbcManage.executeUpdate(insertItemSql(params))
//    }
//    for (i <- 0 until arr.size) {
//      //插入将检测车辆
//      JdbcManage.executeUpdate(insertCarSql(carId + i, checkId, arr(i)))
//    }
//  }
//
//  /**
//    * @author bin.Y
//    *         Description:插入检测车企信息
//    *         Date:  2018/4/12 16:32
//    */
//  def insertCarcompany(map: LinkedTreeMap[String, String], checkId: String): Unit = {
//    var params = Map[String, String]()
//    params += ("carcompanyId" -> map.get("carcompanyId"))
//    params += ("bespeakTime" -> map.get("bespeakTime"))
//    params += ("dutyPeople" -> map.get("dutyPeople"))
//    params += ("dutyPhone" -> map.get("dutyPhone"))
//    params += ("dutyEmail" -> map.get("dutyEmail"))
//    params += ("checkId" -> checkId)
//    JdbcManage.executeUpdate(insertCarcompanySql(params))
//  }
//
//  /**
//    * @author bin.Y
//    *         Description:获取插入检测项SQL
//    *         Date:  2018/4/12 16:33
//    */
//  def insertItemSql(params: Map[String, String]): String = {
//    val sql =
//      "INSERT INTO compliance_check_result_item (\n" +
//        "	item_id,\n" +
//        "	check_id,\n" +
//        "	check_car_id,\n" +
//        "	check_item_no,\n" +
//        "	data_begin_time,\n" +
//        "	data_end_time\n" +
//        ")\n" +
//        "VALUES\n" +
//        "	(\n" +
//        "		'" + UUID.randomUUID.toString + "',\n" +
//        "		'" + params.get("checkId").get + "',\n" +
//        "		'" + params.get("carId").get + "',\n" +
//        "		'" + params.get("checkType").get + "',\n" +
//        "		DATE_FORMAT('" + params.get("dataBeginTime").get + "','%Y-%c-%d %H:%i:%s'),\n" +
//        "		DATE_FORMAT('" + params.get("dataEndTime").get + "','%Y-%c-%d %H:%i:%s')\n" +
//        "	)"
//    sql
//  }
//
//  /**
//    * @author bin.Y
//    *         Description:获取插入车辆信息SQL
//    *         Date:  2018/4/12 16:33
//    */
//  def insertCarSql(carId: String, checkId: String, carVin: String): String = {
//    val sql =
//      "INSERT INTO compliance_check_result_car (\n" +
//        "	check_car_id,\n" +
//        "	check_id,\n" +
//        "	car_vin,\n" +
//        "	check_status\n" +
//        ")\n" +
//        "VALUES\n" +
//        "	(\n" +
//        "		'" + carId + "',\n" +
//        "		'" + checkId + "',\n" +
//        "		'" + carVin + "',\n" +
//        "		'0'\n" +
//        "	)"
//    sql
//  }
//
//  /**
//    * @author bin.Y
//    *         Description:获取插入车企信息SQL
//    *         Date:  2018/4/12 16:33
//    */
//  def insertCarcompanySql(params: Map[String, String]): String = {
//    val sql =
//      "INSERT INTO compliance_check_result (\n" +
//        "	check_id,\n" +
//        "	carcompany_id,\n" +
//        "	duty_people,\n" +
//        "	duty_people_phone,\n" +
//        "	duty_people_email,\n" +
//        "	bespeak_time,\n" +
//        "	check_status\n" +
//        ")\n" +
//        "VALUES\n" +
//        "	(\n" +
//        "		'" + params.get("checkId").get + "',\n" +
//        "		'" + params.get("carcompanyId").get + "',\n" +
//        "		'" + params.get("dutyPeople").get + "',\n" +
//        "		'" + params.get("dutyPhone").get + "',\n" +
//        "		'" + params.get("dutyEmail").get + "',\n" +
//        "		DATE_FORMAT('" + params.get("bespeakTime").get + "','%Y-%c-%d %H:%i:%s'),\n" +
//        "		'0'\n" +
//        "	)"
//    System.out.print("insertCarcompanySql---sql:"+sql)
//    sql
//  }
//
//  /**
//    * @author bin.Y
//    *         Description:
//    *         Date:  2018/4/12 10:06
//    */
//  def analysisJson(path: String): String = {
//    val is: InputStream = this.getClass.getResourceAsStream(path)
//    val jsonStr: String = IOUtils.toString(is)
//    new JsonParser().parse(jsonStr).getAsJsonObject.toString
//  }
//}
