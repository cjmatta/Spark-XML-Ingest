import org.apache.spark._
import org.apache.spark.streaming._

/**
 * Created by cmatta on 10/30/15.
 */
class FileStreamer {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("filestreamer")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))
    val textFileRdd = ssc.textFileStream("/user/mapr/textfile/in")
    textFileRdd.foreachRDD(rdd =>
      if(!rdd.isEmpty()) {
        println("Files found!")
      }
    )
    val modifiedLines = textFileRdd.map(line => line + " -- modified.")
    modifiedLines.foreachRDD(rdd => rdd.saveAsTextFile("/user/mapr/textfile/out"))
  }
}
