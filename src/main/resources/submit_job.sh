JARFILE=/Users/cmatta/Downloads/Spark-XML-Ingest/target/scala-2.11/Spark-XML-Ingest-assembly-0.1-SNAPSHOT.jar

$SPARK_HOME/bin/spark-submit --files /Users/cmatta/Downloads/Spark-XML-Ingest/src/main/resources/application.conf --master="local[2]" --class XMLTest $JARFILE
