import java.text.SimpleDateFormat

import generated.Music
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.mahout.text.wikipedia.XmlInputFormat
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

object XMLTest {
  val logger = LoggerFactory.getLogger("XMLTest")

  def main(args: Array[String]) {
    if(args.length < 3) {
      logger.error("XMLTest /path/to/watch <xmlStartTag> </xmlEndTag>")
    }
    val WATCHDIR = args(0)

    val start = args(1)
    val end = args(2)

    logger.info(f"Start: $start\nEnd: $end")
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val hadoopConf = new Configuration()
    hadoopConf.set("xmlinput.start", start)
    hadoopConf.set("xmlinput.end", end)

    val sparkConf = new SparkConf().setAppName("XMLTest")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(5))

    logger.info("Starting XMLTest App.")

    val fStream = {
      ssc.fileStream[LongWritable, Text, XmlInputFormat](
        WATCHDIR, xmlFilter _, newFilesOnly = false, conf = hadoopConf)
    }


    fStream.foreachRDD(rdd =>
      if (rdd.count() == 0) {
        logger.info("No files..")
      })

    val d = fStream.map{ case(x, y) =>
        logger.info("Hello from the RDD")
        logger.info(y.toString)
        scalaxb.fromXML[Music](scala.xml.XML.loadString(y.toString))
    }

    fStream.map{case(x, y) => logger.info(y.toString)}

    d.foreachRDD(rdd => rdd.saveAsTextFile("file:///tmp/xmlout"))
//    need the sqlContext and implicits to save as Parquet.
//    val sqlContext = new SQLContext(sc)
//    import sqlContext.implicits._
//    Currently this results in a "Schema for type not supported" error
//    d.foreachRDD(rdd => rdd.toDF().saveAsParquetFile("file:///tmp/xmloutparquet"))

    ssc.start()
    ssc.awaitTermination()
  }

  def xmlFilter(path: Path): Boolean = path.toString.toLowerCase.endsWith("xml")
}
