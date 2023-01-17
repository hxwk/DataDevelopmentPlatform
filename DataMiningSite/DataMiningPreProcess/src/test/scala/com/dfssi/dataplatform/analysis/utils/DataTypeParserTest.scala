package com.dfssi.dataplatform.analysis.utils


import java.util.UUID

import org.apache.spark.sql.types.DataType
import org.junit.Test


@Test
class DataTypeParserTest {

  @Test
  def testDateTypehel:Unit = {
    val dt:DataType=DataTypeParser.toDataType("string")
    println(dt)
  }

  @Test
  def randomUUID = {
    0 until 19 foreach{ i =>
      println(UUID.randomUUID().toString)
    }
  }

}
