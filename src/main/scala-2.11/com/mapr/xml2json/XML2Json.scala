package com.mapr.xml2json

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.mahout.text.wikipedia.XmlInputFormat
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import org.xml.sax.InputSource

object XML2Json {
  val logger = LoggerFactory.getLogger("XMLTest")

  def main(args: Array[String]) {
    val conf = ConfigFactory.load()
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
    val ssc = new StreamingContext(sc, Seconds(settings.intervalSeconds))

    logger.info("Starting XMLTest App.")

    val fStream = {
      ssc.fileStream[LongWritable, Text, XmlInputFormat](
        WATCHDIR, xmlFilter _, newFilesOnly = true, conf = hadoopConf)
    }

//    Convert XML to json string
    val posLog: DStream[String] = fStream.map{ case(x, y) =>
        val parserFactory = SAXParserFactory.newInstance();
        val parser = parserFactory.newSAXParser();
        val handler = new MySaxParser();
        handler.setLogger(logger)
        parser.parse(new InputSource(new ByteArrayInputStream(
//          remove all control characters from the xml and return a bytestream
          y.toString.split('\n').map(_.trim.filter(_ >= ' ')).mkString.getBytes(StandardCharsets.UTF_8))
        ), handler)
        handler.getVal.toJSONString
    }

//    Write out our JSON files to disk
    posLog.foreachRDD(rdd =>
      if(rdd.count() > 0) {
        rdd.saveAsTextFile(OUTDIR + s"/${new java.util.Date().getTime}")
      }
    )

    ssc.start()
    ssc.awaitTermination()
  }

  def xmlFilter(path: Path): Boolean = path.toString.toLowerCase.endsWith("xml")
}
