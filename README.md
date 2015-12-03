### Intro

This is an example project using Spark to ingest XML data from a Hadoop compatible filesystem or directory (HDFS, S3, MapR-FS). The XML is ingested and converted to simple JSON objects and then written out as a text file.

This project relies on the work of Douglas Crockford and his native Java XML->JSON library: https://github.com/douglascrockford/JSON-java

## Instructions

Edit `src/main/resources/application.conf` to specify the start and end tags for the XML, which directory to watch, and which directory to write out to:
```
xmlingest {
    watchdir="/tmp/in",
    outputdir="/tmp/out",
    xmlstart="<book>",
    xmlend="</book>"
}

```

### Build

```
$ sbt assembly
```

### Usage

```
$./src/main/resources/submit_job.sh
```

