import java.text.SimpleDateFormat
import java.time.LocalDateTime

/**
  * Description:
  *
  * @author Weijj
  * @version 2018/9/13 11:01 
  */
class TestObject {

}
object TestObject {
  def main(args: Array[String]): Unit = {
//    println("0.0".toString.substring(0, "0.0".toString.length() - 2).toInt)
    // 测试时间转为毫秒
//    val dateData = "180913110811020"
//    val map = formateollTime(dateData)
//    println(map.getOrElse("collTime", 0L).toString.toLong + "=====" + map.getOrElse("collectionTime", "").toString)

    //当前时间
    println(LocalDateTime.now().toString.substring(0,LocalDateTime.now().toString.length-4).replace('T', ' '));


  }

  def formateollTime(collTime:String): Map[String, Any] = {
    val yearMonthDay= "20" + collTime.substring(0,2) + "-" + collTime.substring(2, 4) + "-" + collTime.substring(4, 6) + " " + collTime.substring(6, 8) + ":" + collTime.substring(8, 10) + ":"+collTime.substring(10, 12)
    //    val yearMonthDay= collTime.substring(0, 2) + "-" + collTime.substring(2, 4)
    println(yearMonthDay)
    val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(yearMonthDay)
    val time = date.getTime + collTime.substring(12, 15)
    println(date.getTime + collTime.substring(12, 15))
    scala.collection.immutable.Map[String, Any](("collTime", time),("collectionTime", yearMonthDay))
  }
}
