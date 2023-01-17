package com.dfssi.dataplatform.analysis.preprocess.offline
import com.dfssi.dataplatform.analysis.preprocess.offline.commonTest.algTest

object logisticRegressionTest {
	def main(args:Array[String]):Unit = {
		val xmlFilePath = "logisticRegressionTest.xml"
		val testName = "logisticRegressionDataTest"
		algTest(xmlFilePath,testName)
	}
}
