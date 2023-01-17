package com.dfssi.dataplatform.streaming.store

import java.io.InputStream

import com.dfssi.dataplatform.streaming.store.config.{
  HiveTableDef,
  MessageDef,
  StreamingStoreConfig
}
import com.dfssi.dataplatform.streaming.store.map.Mapper0704
import com.google.gson.{JsonObject, JsonParser}
import org.apache.commons.io.IOUtils
import org.apache.spark.sql.types.{DataType, DataTypes}
import org.junit.Test

class StreamingStoreConfigTest {

  def loadConfFile(): String = {
    val is = getClass.getResourceAsStream(
      "/com/dfssi/dataplatform/streaming/store/config/StoreConfig.xml");
    return IOUtils.toString(is);
  }

  @Test def testStreamingStoreConfigTable: Unit = {
    val xmlStr = loadConfFile();
    val conf: StreamingStoreConfig = new StreamingStoreConfig();
    val processContext = new ProcessContext()

    conf.loadConfig(xmlStr);
    val table: HiveTableDef = conf.getHiveTableDef("terminal_0705");

    println(table.toDDL(conf));
  }

  @Test def testStreamingStoreConfigMessage: Unit = {
    val xmlStr = loadConfFile();
    val conf: StreamingStoreConfig = new StreamingStoreConfig();

    conf.loadConfig(xmlStr);
    val message: MessageDef = conf.getMessageDef("0200");

    println(message.name);
  }

  @Test def testStreamingStoreConfigMessage0704: Unit = {
    val xmlStr = loadConfFile();
    val conf: StreamingStoreConfig = new StreamingStoreConfig();

    conf.loadConfig(xmlStr);
    val message: MessageDef = conf.getMessageDef("0704");

    println(message.name);
  }

  @Test def testStreamingStoreConfigMessage0704msg: Unit = {
    val xmlStr = loadConfFile();
    val conf: StreamingStoreConfig = new StreamingStoreConfig();

    conf.loadConfig(xmlStr);
    val message: MessageDef = conf.getMessageDef("0704");
    val table: HiveTableDef = conf.getHiveTableDef("terminal_0200")
    val is = this.getClass
      .getResourceAsStream("/com/dfssi/bigdata/interface/test/sample0704.json")
    val jsonStr = IOUtils.toString(is)
    val modelJsonObject = new JsonParser().parse(jsonStr).getAsJsonObject

    Mapper0704.map(modelJsonObject, message, table)
  }

  @Test def testStreamingStoreConfigParam: Unit = {
    val xmlStr = loadConfFile();
    val conf: StreamingStoreConfig = new StreamingStoreConfig();

    conf.loadConfig(xmlStr);
    val message: String = conf.getParamValue("hiveExternalTableDataRootPath");

    println(message);
  }

  //  private def getDataType(): DataType = {
  //    val dataType = getSimpleDataType(colType)
  //
  //    if (dataType != null) {
  //      return dataType
  //    } else if ("array".equalsIgnoreCase(colType)) {
  //      new ArrayType(getSimpleDataType(paramizedClassName), true)
  //    } else {
  //      DataTypes.StringType
  //    }
  //  }
  private def getSimpleDataType(typeName: String): DataType = {
    if ("string".equalsIgnoreCase(typeName)) {
      DataTypes.StringType
    } else if ("integer".equalsIgnoreCase(typeName)) {
      DataTypes.IntegerType
    } else if ("float".equalsIgnoreCase(typeName)) {
      DataTypes.FloatType
    } else if ("double".equalsIgnoreCase(typeName)) {
      DataTypes.DoubleType
    } else if ("byte".equalsIgnoreCase(typeName)) {
      DataTypes.ByteType
    } else if ("timestamp".equalsIgnoreCase(typeName)) {
      DataTypes.TimestampType
    } else if ("binary".equalsIgnoreCase(typeName)) {
      DataTypes.BinaryType
    } else if ("long".equalsIgnoreCase(typeName)) {
      DataTypes.LongType
    } else if ("bigint".equalsIgnoreCase(typeName)) {
      DataTypes.LongType
    }

    null
  }

  @Test def testType(): Unit = {
    var dt = getSimpleDataType("bigint")
    println(dt.simpleString)
  }

  @Test def testMergeSql(): Unit = {
    val xmlStr = loadConfFile();
    val conf: StreamingStoreConfig = new StreamingStoreConfig();
    val processContext = new ProcessContext()

    conf.loadConfig(xmlStr);
    val table: HiveTableDef = conf.getHiveTableDef("terminal_0705");

    println(table.buildMergeSql(conf, "2018", "20180205"));
  }

}
