import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.mahout.text.wikipedia.XmlInputFormat
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

case class POSRetailTransaction(GUID: String)

object XMLTest {
  val logger = LoggerFactory.getLogger("XMLTest")

  def main(args: Array[String]) {
    val conf = ConfigFactory.load()
    val WATCHDIR = conf.getString("xmlingest.watchdir")
    val OUTDIR = conf.getString("xmlingest.outputdir")
    val START = conf.getString("xmlingest.xmlstart")
    val END = conf.getString("xmlingest.xmlend")

    logger.info(f"Start: $START")
    logger.info(f"End: $END")
    val hadoopConf = new Configuration()
    hadoopConf.set("xmlinput.start", START)
    hadoopConf.set("xmlinput.end", END)

    val sparkConf = new SparkConf().setAppName("XMLTest")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(5))

    logger.info("Starting XMLTest App.")

    val fStream = {
      ssc.fileStream[LongWritable, Text, XmlInputFormat](
        WATCHDIR, xmlFilter _, newFilesOnly = true, conf = hadoopConf)
    }
    fStream.foreachRDD(rdd => logger.info(rdd.toDebugString))

    val posLog: DStream[generated.RetailTransactionType] = fStream.map{ case(x, y) =>
        logger.info(y.toString)
        scalaxb.fromXML[generated.RetailTransactionType](scala.xml.XML.loadString(y.toString))
    }

    val transaction = posLog.map(x => POSRetailTransaction(x.GUID))

    transaction.foreachRDD(rdd => rdd.saveAsTextFile(OUTDIR))

    ssc.start()
    ssc.awaitTermination()
  }

  def xmlFilter(path: Path): Boolean = path.toString.toLowerCase.endsWith("xml")
}
