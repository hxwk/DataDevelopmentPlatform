package com.dfssi.dataplatform.analysis.common

import org.apache.spark.sql.DataFrame
import org.apache.spark.localMl.feature.PCA

trait DataVisualization extends Serializable{
	def visualizate(rawData:DataFrame,dems:Int,inputCol:String,outputCol:String):DataFrame ={
		val pca = new PCA()
				.setInputCol(inputCol)
				.setOutputCol(outputCol)
				.setK(dems)
				.fit(rawData)
		pca.transform(rawData)
	}
}
