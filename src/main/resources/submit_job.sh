JARFILE=/Users/cmatta/Downloads/Spark-XML-Ingest/target/scala-2.11/Spark-XML-Ingest-assembly-0.1-SNAPSHOT.jar
CLASSNAME=com.mapr.xml2json.XML2Json
MASTER="local[2]"
CONFIGFILE=$1

if [[ ! -f $CONFIGFILE ]]; then
    echo "Config file not found!"
    exit 1
fi

if [[ -z $SPARK_HOME ]]; then
    echo "ERROR: Please set environment variable SPARK_HOME"
    exit 1
fi

$SPARK_HOME/bin/spark-submit --master=$MASTER --class $CLASSNAME --files $CONFIGFILE $JARFILE
