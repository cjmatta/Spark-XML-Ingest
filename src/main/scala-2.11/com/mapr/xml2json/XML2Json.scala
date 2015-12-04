package com.mapr.xml2json

import java.io.File

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.mahout.text.wikipedia.XmlInputFormat
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.json.XML
import org.slf4j.LoggerFactory

object XML2Json {
  val logger = LoggerFactory.getLogger("XMLTest")

  def main(args: Array[String]) {
    val configFile = new File(args(0))
    val conf = ConfigFactory.parseFile(configFile).getConfig("xmlingest")
    val settings = new Settings(conf)

    val WATCHDIR = settings.watchdir
    val OUTDIR = settings.output

    val START = settings.xmlStartTag
    val END = settings.xmlEndTag

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

    val posLog: DStream[String] = fStream.map{ case(x, y) =>
        XML.toJSONObject(y.toString).toString
    }

    posLog.foreachRDD(rdd => rdd.saveAsTextFile(OUTDIR + s"/${new java.util.Date().getTime}"))

    ssc.start()
    ssc.awaitTermination()
  }

  def xmlFilter(path: Path): Boolean = path.toString.toLowerCase.endsWith("xml")
}
