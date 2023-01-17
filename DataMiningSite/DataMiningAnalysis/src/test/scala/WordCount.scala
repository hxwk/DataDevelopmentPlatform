import org.apache.spark.SparkContext

object WordCount {
  def main(args: Array[String]): Unit = {
    val env = new SparkContext()
    　　val data = List("hi","how are you","hi")
    　　val dataSet = env.fromCollection(data)
    　　val words = dataSet.flatMap(value => value.split("\\s+"))
    　　val mappedWords = (value => (value,1))
    　　val grouped = mappedWords.groupBy(0)
    　　val sum = grouped.sum(1)
    　　println(sum.collect())



  }
}
