package com.mapr.xml2json

import com.typesafe.config.{Config, ConfigFactory}

/**
 * Created by cmatta on 11/2/15.
 */
class Settings(config: Config) {
  config.checkValid(ConfigFactory.defaultReference(), "xmlingest")

  val watchdir = config.getString("xmlingest.watchdir")
  val output = config.getString("xmlingest.outputdir")
  val xmlStartTag = config.getString("xmlingest.xmlstart")
  val xmlEndTag = config.getString("xmlingest.xmlend")
  val intervalSeconds = config.getInt("xmlingest.window")
}
