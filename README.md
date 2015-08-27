### Intro

This is an example project using Spark to ingest XML data from a Hadoop compatible filesystem or directory (HDFS, S3, MapR-FS). Currently it simply writes the objects out to a text file.

## Instructions

Put all `xsd` files in `src/main/xsd` and [scalaxb](http://scalaxb.org) will generate Scala code for your objects. A sample `music.xsd` file is in there as well as sample `music.xml` files in `src/main/resources`

### Build

```
$ sbt assembly
```

### Usage

```
spark-submit --master [yarn-client|local[n]] --class XMLTest ./target/scala-2.10/Spark-XML-Ingest-assembly-0.1-SNAPSHOT.jar [file:// | hdfs:// | mapr://]/directory/to/watch "<xmltag>" "</xmltag>"
```

The spark program will watch the specified directory for files to be moved or copied in, once they're in it will process them and write them out the filesystem at `/tmp/xmlout`